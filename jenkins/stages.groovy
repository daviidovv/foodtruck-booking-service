def setReleaseVersion(String releaseVersion ) {
    return {
        stage('Set Release Version') {
            script {
                if (!releaseVersion) {
                    error 'Eine Release-Version muss angegeben werden, wenn der main-Branch gebaut wird.'
                }

                // Prüfen, ob die Version in der POM-Datei bereits gesetzt ist
                def currentVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed -r 's/\\x1b\\[[0-9;]*m//g'", returnStdout: true).trim()
                releaseVersion = releaseVersion.trim()

                if (currentVersion == releaseVersion) {
                    echo "Die POM-Datei enthält bereits die Version ${releaseVersion}. Kein Update erforderlich."
                } else {
                    echo "Aktuelle Version in der POM: ${currentVersion}, wird auf ${releaseVersion} aktualisiert."

                    withCredentials([sshUserPrivateKey(credentialsId: 'gitlab-cloud-jonasblaschek-ssh', keyFileVariable: 'SSH_KEY')]) {
                        sh """
                                git config --global user.name ${GIT_COMMITTER_NAME}
                                git config --global user.email ${GIT_COMMITTER_EMAIL}
                                eval \$(ssh-agent -s)
                                ssh-add \$SSH_KEY

                                # Aktualisiere den lokalen Branch
                                git checkout main
                                git fetch
                                git pull --ff-only

                                # Version in der POM setzen
                                mvn versions:set -DnewVersion=${releaseVersion}
                                mvn versions:commit

                                # Änderungen vornehmen, committen und pushen
                                git add pom.xml
                                git commit -m "Release Version ${releaseVersion} gesetzt"
                                git push origin main
                                """
                    }
                }
            }
        }
    }
}

def buildAndTest() {
    return {
        stage('Build') {
            sh 'mvn clean package'
        }
        stage('Dependency Security Check') {
            try {
                sh 'mvn dependency-check:check'
            } finally {
                archiveArtifacts artifacts: 'target/dependency-check-report.html', fingerprint: true
            }
        }
        stage('Test') {
            try {
                sh 'mvn test'
            } finally {
                    junit '**/target/surefire-reports/*.xml'
            }
        }
    }
}

def dockerPushImage( String tag, boolean pushToLatest = false) {
    return {
        stage('Docker Build and Push Image') {
            script {
                if(!tag) {
                    error "Fehler im Jenkinsfile, es wurde keine Version angegeben!"
                }

                echo "Baue Docker-Image: ${DOCKER_IMAGE}:${tag}"


                withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_CREDENTIALS,
                        usernameVariable: 'DOCKER_CREDENTIALS_USR',
                        passwordVariable: 'DOCKER_CREDENTIALS_PSW'
                )]) {
                    sh """
                        echo "Login in Docker Repository"
                        echo $DOCKER_CREDENTIALS_PSW | docker login $DOCKER_REGISTRY -u $DOCKER_CREDENTIALS_USR --password-stdin
                    
                        echo "Docker-Image Tag erstellen: '${tag}'"
                        docker build -t ${DOCKER_IMAGE}:${tag} .
                        docker push ${DOCKER_IMAGE}:${tag}
                    
                        if [ "${pushToLatest}" = "true" ]; then
                            echo "Zusätzliches Tag 'latest' wird gebaut und gepusht"
                            docker build -t ${DOCKER_IMAGE}:latest .
                            docker push ${DOCKER_IMAGE}:latest
                        else
                            echo "Kein Push von 'latest' (pushToLatest=false)"
                        fi
                    """
                }
            }
        }
    }
}

def deploy( String serverIP, String sshCredentialsId) {
    return {
        stage('Deploy') {
            script {
                if (!serverIP) {
                    error 'Die IP-Adresse/Hostname des Zielservers muss für ein Deployment angegeben werden.'
                }

                serverIP = serverIP.trim()

                withCredentials([sshUserPrivateKey(
                        credentialsId: sshCredentialsId,
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                )]) {
                    def pomVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout | sed -r 's/\\x1b\\[[0-9;]*m//g'", returnStdout: true).trim()
                    echo "Deploye Version ${pomVersion} auf Server ${serverIP}."


                    echo "Hostschlüssel hinzufügen"
                    // Hostschlüssel hinzufügen
                    sh """
                            ssh-keyscan -H ${serverIP} >> ~/.ssh/known_hosts
                    """
                    echo "docker-compose kopieren"

                    sh """
                            sed -i 's/:latest/:${pomVersion}/' docker-compose.yml
                            scp -i $SSH_KEY docker-compose.yml $SSH_USER@${serverIP}:.
                    """

                    echo "docker-compose starten"

                    withCredentials([usernamePassword(
                            credentialsId: env.DOCKER_CREDENTIALS,
                            usernameVariable: 'DOCKER_CREDENTIALS_USR',
                            passwordVariable: 'DOCKER_CREDENTIALS_PSW'
                    )]) {
                        // Dateien kopieren und Docker-Befehle ausführen
                        sh """
                            ssh -i $SSH_KEY $SSH_USER@${serverIP} '
                                echo $DOCKER_CREDENTIALS_PSW | docker login $DOCKER_REGISTRY -u $DOCKER_CREDENTIALS_USR --password-stdin
                                docker compose down 2> /dev/null
                                mkdir db logs 2> /dev/null
                                docker compose pull && docker compose up -d
                                '
                        """
                    }
                }
            }

        }
    }
}

def createReleaseTag(String releaseVersion) {
    return {
        stage('Create Release Tag') {
            script {

                if(!releaseVersion) {
                    error "Es muss eine Release Version angegeben werden!"
                }

                releaseVersion = releaseVersion.trim()

                def tagName = "v${releaseVersion}"
                echo "Erstelle und pushe Git-Tag ${tagName}."
                withCredentials([sshUserPrivateKey(credentialsId: 'gitlab-cloud-jonasblaschek-ssh', keyFileVariable: 'SSH_KEY')]) {
                    sh """
                        git config --global user.name ${GIT_COMMITTER_NAME}
                        git config --global user.email ${GIT_COMMITTER_EMAIL}
                        eval \$(ssh-agent -s)
                        ssh-add \$SSH_KEY

                        git tag -a ${tagName} -m "Release ${releaseVersion}"
                        git push origin ${tagName}
                    """
                }
            }
        }
    }
}

// <<< DAS MUSS GANZ UNTEN STEHEN
return this
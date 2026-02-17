// ===========================================
// Foodtruck Booking - Jenkins Pipeline
// ===========================================
// Diese Pipeline erkennt automatisch den Branch und führt
// die entsprechenden Stages aus.

pipeline {
    agent any

    parameters {
        string(name: 'RELEASE_VERSION', defaultValue: '', description: 'Release-Version (nur für main-Branch, z.B. 1.0.0)')
    }

    environment {
        // Docker Konfiguration
        DOCKER_REGISTRY = 'jenkins.lands.between'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/foodtruck-booking"
        DOCKER_CREDENTIALS = 'docker-registry-local'

        // Git Konfiguration
        GIT_COMMITTER_NAME = 'Jenkins CI'
        GIT_COMMITTER_EMAIL = 'jenkins@hendl-heinrich.de'
    }

    stages {
        // =====================
        // BUILD & TEST (alle Branches)
        // =====================
        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests -B'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        // =====================
        // DOCKER (main + develop)
        // =====================
        stage('Docker Build & Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                script {
                    def tag = (env.BRANCH_NAME == 'main') ? params.RELEASE_VERSION : "develop-${BUILD_NUMBER}"

                    if (env.BRANCH_NAME == 'main' && !params.RELEASE_VERSION) {
                        error 'Für main-Branch muss eine RELEASE_VERSION angegeben werden!'
                    }

                    withCredentials([usernamePassword(
                        credentialsId: env.DOCKER_CREDENTIALS,
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo \$DOCKER_PASS | docker login \$DOCKER_REGISTRY -u \$DOCKER_USER --password-stdin
                            docker build -t ${DOCKER_IMAGE}:${tag} .
                            docker push ${DOCKER_IMAGE}:${tag}
                        """

                        // Bei main auch :latest pushen
                        if (env.BRANCH_NAME == 'main') {
                            sh """
                                docker tag ${DOCKER_IMAGE}:${tag} ${DOCKER_IMAGE}:latest
                                docker push ${DOCKER_IMAGE}:latest
                            """
                        }
                    }
                }
            }
        }

        // =====================
        // VERSION UPDATE (nur main)
        // =====================
        stage('Update Version') {
            when {
                branch 'main'
            }
            steps {
                script {
                    if (!params.RELEASE_VERSION) {
                        error 'RELEASE_VERSION muss angegeben werden!'
                    }

                    def currentVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()

                    if (currentVersion != params.RELEASE_VERSION) {
                        withCredentials([sshUserPrivateKey(credentialsId: 'github-ssh', keyFileVariable: 'SSH_KEY')]) {
                            sh """
                                git config user.name "${GIT_COMMITTER_NAME}"
                                git config user.email "${GIT_COMMITTER_EMAIL}"

                                eval \$(ssh-agent -s)
                                ssh-add \$SSH_KEY

                                mvn versions:set -DnewVersion=${params.RELEASE_VERSION} -B
                                mvn versions:commit -B

                                git add pom.xml
                                git commit -m "Release Version ${params.RELEASE_VERSION}"
                                git push origin main
                            """
                        }
                    } else {
                        echo "Version ${params.RELEASE_VERSION} ist bereits gesetzt."
                    }
                }
            }
        }

        // =====================
        // DEPLOY (nur main)
        // =====================
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                script {
                    // ⚠️ HIER ANPASSEN: Dein Server-Hostname
                    def serverHostname = 'DEIN-SERVER.example.com'
                    def sshCredentialsId = 'foodtruck-server-ssh'

                    if (serverHostname == 'DEIN-SERVER.example.com') {
                        error '''
                        ⚠️ DEPLOYMENT NICHT KONFIGURIERT!

                        Öffne das Jenkinsfile und ersetze in der Deploy-Stage:
                        def serverHostname = 'DEIN-SERVER.example.com'

                        mit deinem echten Server-Hostnamen.
                        '''
                    }

                    withCredentials([sshUserPrivateKey(
                        credentialsId: sshCredentialsId,
                        keyFileVariable: 'SSH_KEY',
                        usernameVariable: 'SSH_USER'
                    )]) {
                        sh """
                            mkdir -p ~/.ssh
                            ssh-keyscan -H ${serverHostname} >> ~/.ssh/known_hosts 2>/dev/null || true

                            sed 's/:latest/:${params.RELEASE_VERSION}/' docker-compose.deploy.yml > docker-compose.tmp.yml
                            scp -i \$SSH_KEY docker-compose.tmp.yml \$SSH_USER@${serverHostname}:docker-compose.yml
                            rm docker-compose.tmp.yml
                        """

                        withCredentials([usernamePassword(
                            credentialsId: env.DOCKER_CREDENTIALS,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )]) {
                            sh """
                                ssh -i \$SSH_KEY \$SSH_USER@${serverHostname} '
                                    echo "${DOCKER_PASS}" | docker login ${DOCKER_REGISTRY} -u ${DOCKER_USER} --password-stdin
                                    docker compose down || true
                                    docker compose pull
                                    docker compose up -d
                                '
                            """
                        }
                    }

                    echo "✅ Deployment auf ${serverHostname} erfolgreich!"
                }
            }
        }

        // =====================
        // GIT TAG (nur main)
        // =====================
        stage('Create Git Tag') {
            when {
                branch 'main'
            }
            steps {
                script {
                    def tagName = "v${params.RELEASE_VERSION}"

                    withCredentials([sshUserPrivateKey(credentialsId: 'github-ssh', keyFileVariable: 'SSH_KEY')]) {
                        sh """
                            git config user.name "${GIT_COMMITTER_NAME}"
                            git config user.email "${GIT_COMMITTER_EMAIL}"

                            eval \$(ssh-agent -s)
                            ssh-add \$SSH_KEY

                            git tag -a ${tagName} -m "Release ${params.RELEASE_VERSION}" || echo "Tag existiert bereits"
                            git push origin ${tagName} || echo "Tag bereits gepusht"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo '✅ Pipeline erfolgreich abgeschlossen!'
        }
        failure {
            echo '❌ Pipeline fehlgeschlagen!'
        }
        always {
            cleanWs()
        }
    }
}

pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'foodtruck-app'
        DEPLOY_DIR = '/opt/foodtruck'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/YOUR_USERNAME/foodtruck-booking-service.git',
                    credentialsId: 'github-credentials'
            }
        }

        stage('Test Backend') {
            agent {
                docker {
                    image 'maven:3.9-eclipse-temurin-21-alpine'
                    args '-v $HOME/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                    sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'postgres-password', variable: 'POSTGRES_PASSWORD')]) {
                    sh '''
                        # Stop existing containers
                        docker-compose -f docker-compose.prod.yml down || true

                        # Start with new image
                        export POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
                        docker-compose -f docker-compose.prod.yml up -d --build

                        # Wait for health check
                        echo "Waiting for application to start..."
                        sleep 45
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    # Check if app is healthy
                    for i in 1 2 3 4 5; do
                        if curl -sf http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
                            echo "Application is healthy!"
                            exit 0
                        fi
                        echo "Attempt $i: Waiting for application..."
                        sleep 10
                    done
                    echo "Health check failed!"
                    exit 1
                '''
            }
        }

        stage('Cleanup') {
            steps {
                sh '''
                    # Remove old images (keep last 3)
                    docker images ${DOCKER_IMAGE} --format "{{.ID}} {{.Tag}}" | \
                        grep -v latest | \
                        sort -t. -k2 -n -r | \
                        tail -n +4 | \
                        awk '{print $1}' | \
                        xargs -r docker rmi || true
                '''
            }
        }
    }

    post {
        success {
            echo '✅ Deployment erfolgreich!'
        }
        failure {
            echo '❌ Build oder Deployment fehlgeschlagen!'
            sh 'docker-compose -f docker-compose.prod.yml logs --tail=100'
        }
        always {
            cleanWs()
        }
    }
}

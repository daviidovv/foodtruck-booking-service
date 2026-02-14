pipeline {
    agent any

    tools {
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    environment {
        DOCKER_IMAGE = 'foodtruck-api'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/daviidovv/foodtruck-booking-service.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'db-password', variable: 'DB_PASSWORD')]) {
                    sh '''
                        docker stop foodtruck-api || true
                        docker rm foodtruck-api || true
                        docker run -d \
                            --name foodtruck-api \
                            --restart unless-stopped \
                            -p 8080:8080 \
                            -e SPRING_DATASOURCE_URL=jdbc:postgresql://foodtruck-db:5432/foodtruck \
                            -e SPRING_DATASOURCE_USERNAME=foodtruck \
                            -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
                            -e SPRING_PROFILES_ACTIVE=prod \
                            --network foodtruck-net \
                            ${DOCKER_IMAGE}:latest
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    sleep 30
                    curl -f http://localhost:8080/actuator/health || exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Deployment erfolgreich!'
        }
        failure {
            echo 'Build oder Deployment fehlgeschlagen!'
        }
        always {
            cleanWs()
        }
    }
}

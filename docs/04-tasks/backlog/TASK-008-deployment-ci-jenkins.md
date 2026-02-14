# TASK-008: Deployment & CI/CD mit Jenkins

## Status
- [x] Neu
- [ ] In Bearbeitung
- [ ] Review
- [ ] Abgeschlossen

## Priorität
**Hoch** - Vor Frontend-Entwicklung sinnvoll

## Ziel
Backend auf eigenem Server deployen mit automatisierter CI/CD Pipeline via Jenkins.

## Voraussetzungen

### Server-Anforderungen
- Linux Server (Ubuntu 22.04+ empfohlen)
- Mindestens 2GB RAM, 2 CPU Cores
- Docker installiert
- PostgreSQL 16 (oder als Docker Container)
- Java 21+ (oder via Docker)
- Jenkins Server (kann gleicher oder separater Server sein)

### Benötigte Zugänge
- SSH Zugang zum Server
- GitHub Repository Zugang für Jenkins
- Domain/Subdomain (optional, für HTTPS)

---

## Anleitung: Schritt für Schritt

### 1. Server vorbereiten

```bash
# Als root oder mit sudo

# System updaten
apt update && apt upgrade -y

# Docker installieren
curl -fsSL https://get.docker.com | sh
usermod -aG docker $USER

# Docker Compose installieren
apt install docker-compose-plugin -y

# Java 21 installieren (falls nicht via Docker)
apt install openjdk-21-jdk -y
```

### 2. PostgreSQL aufsetzen

**Option A: Docker (empfohlen)**
```bash
docker run -d \
  --name foodtruck-db \
  --restart unless-stopped \
  -e POSTGRES_DB=foodtruck \
  -e POSTGRES_USER=foodtruck \
  -e POSTGRES_PASSWORD=SICHERES_PASSWORT_HIER \
  -p 5432:5432 \
  -v foodtruck-data:/var/lib/postgresql/data \
  postgres:16
```

**Option B: Native Installation**
```bash
apt install postgresql-16 -y
sudo -u postgres createuser foodtruck
sudo -u postgres createdb foodtruck -O foodtruck
sudo -u postgres psql -c "ALTER USER foodtruck PASSWORD 'SICHERES_PASSWORT';"
```

### 3. Anwendung als Docker Container

**Dockerfile erstellen** (im Projekt-Root):
```dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml erstellen**:
```yaml
version: '3.8'

services:
  app:
    build: .
    container_name: foodtruck-api
    restart: unless-stopped
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/foodtruck
      - SPRING_DATASOURCE_USERNAME=foodtruck
      - SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - db
    networks:
      - foodtruck-net

  db:
    image: postgres:16
    container_name: foodtruck-db
    restart: unless-stopped
    environment:
      - POSTGRES_DB=foodtruck
      - POSTGRES_USER=foodtruck
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - foodtruck-data:/var/lib/postgresql/data
    networks:
      - foodtruck-net

volumes:
  foodtruck-data:

networks:
  foodtruck-net:
```

**application-prod.yml erstellen**:
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

logging:
  level:
    root: WARN
    org.example.foodtruckbookingservice: INFO
```

### 4. Jenkins aufsetzen

**Jenkins als Docker installieren:**
```bash
docker run -d \
  --name jenkins \
  --restart unless-stopped \
  -p 8081:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts
```

**Initial-Passwort holen:**
```bash
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

**Jenkins konfigurieren:**
1. Browser öffnen: `http://SERVER_IP:8081`
2. Initial-Passwort eingeben
3. "Install suggested plugins" wählen
4. Admin-User erstellen
5. Plugins installieren: "Docker Pipeline", "GitHub Integration"

### 5. Jenkins Pipeline erstellen

**Jenkinsfile im Projekt-Root erstellen:**
```groovy
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
                    junit '**/target/surefire-reports/*.xml'
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

    post {
        success {
            echo 'Deployment erfolgreich!'
        }
        failure {
            echo 'Build oder Deployment fehlgeschlagen!'
        }
    }
}
```

### 6. GitHub Webhook einrichten

1. GitHub Repository → Settings → Webhooks → Add webhook
2. Payload URL: `http://JENKINS_SERVER:8081/github-webhook/`
3. Content type: `application/json`
4. Events: "Just the push event"
5. Active: ✓

### 7. Jenkins Job erstellen

1. Jenkins → New Item → "foodtruck-api" → Pipeline
2. Build Triggers: ✓ "GitHub hook trigger for GITScm polling"
3. Pipeline: "Pipeline script from SCM"
4. SCM: Git
5. Repository URL: `https://github.com/daviidovv/foodtruck-booking-service.git`
6. Branch: `*/main`
7. Script Path: `Jenkinsfile`
8. Save

---

## Checkliste für Deployment

### Erstmalige Einrichtung
- [ ] Server mit SSH erreichbar
- [ ] Docker + Docker Compose installiert
- [ ] PostgreSQL läuft (Container oder nativ)
- [ ] Datenbank `foodtruck` erstellt
- [ ] Jenkins installiert und konfiguriert
- [ ] GitHub Webhook eingerichtet
- [ ] Jenkins Job erstellt und getestet
- [ ] Firewall-Ports offen (8080 für API, 8081 für Jenkins)

### Sicherheit (wichtig!)
- [ ] Starke Passwörter für DB und Jenkins
- [ ] DB-Passwort als Jenkins Credential (nicht im Code!)
- [ ] HTTPS einrichten (Let's Encrypt / Nginx Reverse Proxy)
- [ ] Firewall konfigurieren (nur nötige Ports)
- [ ] Jenkins hinter Reverse Proxy mit Auth

### Optional: HTTPS mit Nginx

```bash
apt install nginx certbot python3-certbot-nginx -y

# Nginx Config: /etc/nginx/sites-available/foodtruck
server {
    listen 80;
    server_name api.deine-domain.de;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# Aktivieren und SSL
ln -s /etc/nginx/sites-available/foodtruck /etc/nginx/sites-enabled/
certbot --nginx -d api.deine-domain.de
```

---

## Troubleshooting

### Container startet nicht
```bash
docker logs foodtruck-api
```

### DB-Verbindung fehlgeschlagen
```bash
# Prüfen ob DB läuft
docker ps | grep postgres

# Verbindung testen
docker exec -it foodtruck-db psql -U foodtruck -d foodtruck
```

### Jenkins Build fehlgeschlagen
- Logs im Jenkins Job prüfen
- Maven/JDK Tools in Jenkins konfiguriert?
- Docker Socket Berechtigung?

---

## Geschätzter Aufwand
- Server-Setup: 1-2h
- Jenkins-Setup: 1h
- Pipeline + Webhook: 1h
- Testen & Troubleshooting: 1-2h

**Gesamt: ~4-6h**

---

## Abhängigkeiten
- Backend muss fertig sein (TASK-001 bis TASK-006) ✅
- Server muss verfügbar sein

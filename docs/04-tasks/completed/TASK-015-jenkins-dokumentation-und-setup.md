# TASK-015: Jenkins CI/CD Dokumentation & Setup für Foodtruck

## Status
- [x] Abgeschlossen (2026-02-17)

## Ziel

Vollständige Dokumentation erstellen, die erklärt wie die Jenkins CI/CD Pipeline funktioniert, und dann die lokalen Jenkins-Files für das Foodtruck-Projekt anpassen.

## Teil 1: Dokumentation erstellen ✅

Eine Markdown-Datei `docs/07-deployment/JENKINS-ERKLAERUNG.md` mit folgenden Abschnitten:

### 1.1 Übersicht: Was ist CI/CD? ✅
### 1.2 Die Architektur (mit Diagramm) ✅
### 1.3 Jenkinsfile erklärt (Zeile für Zeile) ✅
### 1.4 stages.groovy erklärt (Funktion für Funktion) ✅
### 1.5 Credentials erklärt ✅
### 1.6 Multibranch Pipeline erklärt ✅
### 1.7 Der Deployment-Flow (Schritt für Schritt) ✅

## Teil 2: Foodtruck Jenkins-Files anpassen ✅

### 2.2 Dateien angepasst
- [x] `jenkins/Jenkinsfile.main` - Umgebungsvariablen für Foodtruck
- [x] `jenkins/Jenkinsfile.develop` - Umgebungsvariablen anpassen
- [x] `jenkins/Jenkinsfile.feature` - Umgebungsvariablen anpassen
- [x] `jenkins/stages.groovy` - Credential-IDs anpassen

### 2.3 Zusätzliche Dateien
- [x] `Dockerfile` im Root existierte bereits
- [x] `docker-compose.deploy.yml` für Jenkins-Deployment erstellt

## Teil 3: Jenkins-Konfiguration (Anleitung) ✅

Schritt-für-Schritt-Anleitung in der Dokumentation:
- [x] SSH Credential für Foodtruck-Server anlegen
- [x] Docker Registry Credential anlegen
- [x] Git/GitHub Credential anlegen
- [x] Multibranch Pipeline erstellen
- [x] GitHub Webhook einrichten
- [x] Produktionsserver vorbereiten
- [x] Troubleshooting Guide
- [x] Deployment Checkliste

## Ergebnis

Erstellt:
- `docs/07-deployment/JENKINS-ERKLAERUNG.md` - Vollständige Dokumentation (500+ Zeilen)
- `docker-compose.deploy.yml` - Für Jenkins-Deployment auf Server

Angepasst:
- `jenkins/Jenkinsfile.main` - Foodtruck-Umgebungsvariablen
- `jenkins/Jenkinsfile.develop` - Foodtruck-Umgebungsvariablen
- `jenkins/Jenkinsfile.feature` - Foodtruck-Umgebungsvariablen
- `jenkins/stages.groovy` - Verbesserte Credential-Nutzung

**Hinweis:** Die Jenkins-Files sind lokal und gitignored. Sie müssen später mit den echten Server-Daten angepasst werden.

# Jenkins CI/CD - Vollständige Erklärung

Diese Dokumentation erklärt Schritt für Schritt, wie die Jenkins CI/CD Pipeline für das Foodtruck-Projekt funktioniert.

---

## ⚠️ WAS DU NOCH TUN MUSST

Bevor die Pipeline funktioniert, musst du folgende Schritte erledigen:

### Bereits erledigt ✅

| Was | Status | Wert |
|-----|--------|------|
| Docker Registry Credential | ✅ Existiert | `docker-registry-local` (User: `lands.between`) |
| GitHub Repository | ✅ Öffentlich | `git@github.com:daviidovv/foodtruck-booking-service.git` |
| Jenkins-Files angepasst | ✅ Fertig | Docker-Image: `jenkins.lands.between/foodtruck-booking` |

### Noch offen ❌

| # | Was | Warum | Siehe Abschnitt |
|---|-----|-------|-----------------|
| 1 | **GitHub SSH Credential anlegen** | Damit Jenkins auf GitHub pushen kann (Version, Tags) | [Schritt 12.1](#121-github-ssh-credential-anlegen-pflicht) |
| 2 | **Server-Hostname eintragen** | Damit Jenkins weiß wohin deployed werden soll | [Schritt 12.4](#124-server-hostname-in-jenkinsfile-eintragen) |
| 3 | **SSH Credential für Server anlegen** | Damit Jenkins sich auf dem Server einloggen kann | [Schritt 12.2](#122-ssh-credential-für-produktionsserver-anlegen) |
| 4 | **Multibranch Pipeline erstellen** | Das Jenkins-Projekt für Foodtruck | [Schritt 12.3](#123-multibranch-pipeline-in-jenkins-erstellen) |
| 5 | **Produktionsserver vorbereiten** | Docker, User, SSH-Key, .env Datei | [Schritt 12.5](#125-produktionsserver-vorbereiten) |

### Schnellstart-Checkliste

```
□ 1. GitHub SSH Key auf Jenkins-Server generieren (falls nicht vorhanden)
□ 2. Public Key zu GitHub Account hinzufügen
□ 3. In Jenkins: Credential 'github-ssh' anlegen mit dem Private Key
□ 4. In Jenkins: Credential 'foodtruck-server-ssh' anlegen
□ 5. In Jenkins: Multibranch Pipeline 'foodtruck-booking' erstellen
□ 6. In jenkins/Jenkinsfile.main: Server-Hostname eintragen
□ 7. Produktionsserver vorbereiten (Docker, .env)
□ 8. Ersten Build starten!
```

---

## 1. Was ist CI/CD?

### CI = Continuous Integration
**"Kontinuierliche Integration"** - Jedes Mal wenn du Code pushst, wird automatisch:
- Der Code gebaut (kompiliert)
- Tests ausgeführt
- Geprüft ob alles funktioniert

**Vorteil:** Fehler werden sofort entdeckt, nicht erst Wochen später.

### CD = Continuous Deployment
**"Kontinuierliche Auslieferung"** - Nach erfolgreichem Build wird der Code automatisch auf den Produktionsserver deployed.

**Vorteil:** Neue Features sind innerhalb von Minuten live, nicht Tagen.

### Wo passt Jenkins rein?
Jenkins ist der **"Arbeiter"**, der diese Aufgaben automatisch ausführt. Er:
- Überwacht dein GitHub Repository
- Führt bei jedem Push die Pipeline aus
- Meldet Erfolg oder Fehler

---

## 2. Die Architektur

### Das große Bild

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              DEIN COMPUTER                                    │
│  ┌─────────────┐                                                             │
│  │ Code ändern │                                                             │
│  │ git commit  │                                                             │
│  │ git push    │─────────────────┐                                           │
│  └─────────────┘                 │                                           │
└──────────────────────────────────│───────────────────────────────────────────┘
                                   │
                                   ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                              GITHUB                                          │
│  ┌─────────────────────────────────────────────┐                            │
│  │ Repository: daviidovv/foodtruck-booking-    │                            │
│  │             service (ÖFFENTLICH)            │                            │
│  │                                             │                            │
│  │ Branches:                                   │                            │
│  │   - main (Produktion)                       │                            │
│  │   - develop (Entwicklung)                   │                            │
│  │   - feature/* (neue Features)               │                            │
│  └─────────────────────────────────────────────┘                            │
│                    │                                                         │
│                    │ Webhook: "Hey Jenkins, neuer Code!"                     │
│                    ▼                                                         │
└──────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                         JENKINS SERVER                                       │
│                    (jenkins.lands.between)                                   │
│                                                                              │
│  ┌─────────────────────────────────────────┐                                │
│  │ 1. Code von GitHub holen (git clone)    │                                │
│  │ 2. Maven Build (mvn clean package)      │                                │
│  │ 3. Tests ausführen (mvn test)           │                                │
│  │ 4. Docker Image bauen                   │                                │
│  │ 5. Image in Registry pushen             │                                │
│  │ 6. Auf Server deployen via SSH          │                                │
│  └─────────────────────────────────────────┘                                │
│                    │                                                         │
│                    │ docker push                                             │
│                    ▼                                                         │
│  ┌─────────────────────────────────────────┐                                │
│  │      DOCKER REGISTRY                    │                                │
│  │   (jenkins.lands.between)               │                                │
│  │                                         │                                │
│  │   Credential: docker-registry-local     │                                │
│  │   User: lands.between                   │                                │
│  │                                         │                                │
│  │   Images:                               │                                │
│  │   - foodtruck-booking:1.0.0             │                                │
│  │   - foodtruck-booking:latest            │                                │
│  └─────────────────────────────────────────┘                                │
└──────────────────────────────────────────────────────────────────────────────┘
                                   │
                                   │ SSH + docker compose up
                                   ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                      PRODUKTIONSSERVER                                       │
│               (⚠️ HOSTNAME NOCH EINTRAGEN!)                                  │
│                                                                              │
│  ┌─────────────────────────────────────────┐                                │
│  │           Docker Container              │                                │
│  │  ┌─────────────────┐ ┌──────────────┐  │                                │
│  │  │   Foodtruck     │ │  PostgreSQL  │  │                                │
│  │  │   Spring Boot   │ │   Datenbank  │  │                                │
│  │  │   Port 8080     │ │   Port 5432  │  │                                │
│  │  └─────────────────┘ └──────────────┘  │                                │
│  └─────────────────────────────────────────┘                                │
└──────────────────────────────────────────────────────────────────────────────┘
```

### Aktuelle Konfiguration

| Komponente | Hostname/IP | Status |
|------------|-------------|--------|
| **GitHub** | github.com/daviidovv/foodtruck-booking-service | ✅ Öffentlich |
| **Jenkins Server** | jenkins.lands.between | ✅ Vorhanden |
| **Docker Registry** | jenkins.lands.between | ✅ Credential existiert |
| **Produktionsserver** | ⚠️ NOCH EINTRAGEN | ❌ Offen |

---

## 3. Die Jenkinsfiles erklärt

### Wo liegen die Dateien?

```
foodtruck-booking-service/
├── jenkins/                      ← Diese Dateien sind lokal (gitignored)
│   ├── Jenkinsfile.main          ← Für main-Branch (Produktion)
│   ├── Jenkinsfile.develop       ← Für develop-Branch
│   ├── Jenkinsfile.feature       ← Für feature-Branches
│   └── stages.groovy             ← Wiederverwendbare Bausteine
├── docker-compose.deploy.yml     ← Wird auf Server kopiert
└── Dockerfile                    ← Multi-Stage Build
```

### Unterschied: main vs. develop vs. feature

| Branch | Was passiert | Deployment? |
|--------|--------------|-------------|
| **main** | Build → Test → Docker → Deploy → Git-Tag | ✅ Ja, auf Produktion |
| **develop** | Build → Test → Docker Push | ❌ Nein, nur Image erstellen |
| **feature/*** | Build → Test | ❌ Nein, nur prüfen ob Code funktioniert |

---

## 4. Die Credentials im Überblick

### Welche Credentials braucht die Pipeline?

| ID | Typ | Wofür | Status |
|----|-----|-------|--------|
| `docker-registry-local` | Username/Password | Docker Images pushen | ✅ Existiert |
| `github-ssh` | SSH Key | Auf GitHub pushen (Version, Tags) | ❌ **Anlegen!** |
| `foodtruck-server-ssh` | SSH Key | Auf Produktionsserver deployen | ❌ **Anlegen!** |

---

## 5-11. [Detaillierte Erklärungen...]

*(Die ausführlichen Erklärungen zu stages.groovy, Credentials, Multibranch Pipeline etc. bleiben wie gehabt - siehe vorherige Abschnitte)*

---

## 12. SCHRITT-FÜR-SCHRITT ANLEITUNG

Dies ist die detaillierte Anleitung was du in Jenkins konfigurieren musst.

### 12.1 GitHub SSH Credential anlegen (PFLICHT)

Obwohl das Repository öffentlich ist, braucht Jenkins einen SSH-Key um:
- Die Version in `pom.xml` zu ändern und zu pushen
- Git-Tags zu erstellen und zu pushen

**Schritt 1: Prüfen ob SSH-Key auf Jenkins existiert**

Verbinde dich per SSH auf den Jenkins-Server und prüfe:
```bash
ssh jenkins-server
ls -la ~/.ssh/
```

Falls kein `id_rsa` oder `id_ed25519` vorhanden, erstelle einen:
```bash
ssh-keygen -t ed25519 -C "jenkins@lands.between"
# Enter drücken für Standard-Pfad
# Kein Passwort eingeben (leer lassen)
```

**Schritt 2: Public Key zu GitHub hinzufügen**

1. Public Key anzeigen:
   ```bash
   cat ~/.ssh/id_ed25519.pub
   ```

2. In GitHub: **Settings** → **SSH and GPG keys** → **New SSH key**
   - Title: `Jenkins CI`
   - Key: Den Public Key einfügen
   - **Add SSH key** klicken

**Schritt 3: Credential in Jenkins anlegen**

1. Jenkins öffnen: `https://jenkins.lands.between`

2. Navigieren zu:
   ```
   Manage Jenkins → Credentials → System → Global credentials (unrestricted)
   ```

3. **+ Add Credentials** klicken

4. Ausfüllen:

   | Feld | Wert |
   |------|------|
   | Kind | `SSH Username with private key` |
   | Scope | `Global` |
   | ID | `github-ssh` ← **Genau so schreiben!** |
   | Description | `GitHub SSH Key für Foodtruck` |
   | Username | `git` |
   | Private Key | `Enter directly` auswählen |

5. Private Key einfügen:
   ```bash
   # Auf Jenkins-Server:
   cat ~/.ssh/id_ed25519
   ```
   Den gesamten Inhalt (inkl. `-----BEGIN...` und `-----END...`) kopieren und einfügen.

6. **Create** klicken

### 12.2 SSH Credential für Produktionsserver anlegen

**Schritt 1: SSH-Key für Server erstellen (falls nicht vorhanden)**

Auf dem Jenkins-Server:
```bash
# Falls du einen separaten Key für den Produktionsserver willst:
ssh-keygen -t ed25519 -f ~/.ssh/foodtruck-server -C "jenkins-deploy"
# Kein Passwort
```

**Schritt 2: Public Key auf Produktionsserver kopieren**

```bash
# Ersetze DEIN-SERVER mit dem echten Hostnamen
ssh-copy-id -i ~/.ssh/foodtruck-server.pub deploy@DEIN-SERVER
```

Oder manuell:
```bash
# Public Key anzeigen
cat ~/.ssh/foodtruck-server.pub

# Auf dem Produktionsserver:
echo "DER_PUBLIC_KEY" >> /home/deploy/.ssh/authorized_keys
```

**Schritt 3: Credential in Jenkins anlegen**

1. **+ Add Credentials** klicken

2. Ausfüllen:

   | Feld | Wert |
   |------|------|
   | Kind | `SSH Username with private key` |
   | Scope | `Global` |
   | ID | `foodtruck-server-ssh` ← **Genau so schreiben!** |
   | Description | `SSH Zugang Foodtruck Produktionsserver` |
   | Username | `deploy` ← Der User auf dem Server |
   | Private Key | `Enter directly` auswählen |

3. Private Key einfügen:
   ```bash
   cat ~/.ssh/foodtruck-server
   ```

4. **Create** klicken

### 12.3 Multibranch Pipeline in Jenkins erstellen

1. Jenkins Dashboard öffnen

2. **+ New Item** klicken (links oben)

3. Ausfüllen:
   - Name: `foodtruck-booking`
   - Typ: **Multibranch Pipeline** auswählen
   - **OK** klicken

4. **Branch Sources** konfigurieren:
   - **Add source** → **Git**

   | Feld | Wert |
   |------|------|
   | Project Repository | `https://github.com/daviidovv/foodtruck-booking-service.git` |
   | Credentials | `- none -` (Repository ist öffentlich) |

5. **Build Configuration**:

   | Feld | Wert |
   |------|------|
   | Mode | `by Jenkinsfile` |
   | Script Path | `jenkins/Jenkinsfile.main` |

6. **Scan Multibranch Pipeline Triggers**:
   - [x] Periodically if not otherwise run
   - Interval: `1 hour`

7. **Save** klicken

Jenkins wird jetzt das Repository scannen und die Branches finden.

### 12.4 Server-Hostname in Jenkinsfile eintragen

**WICHTIG:** Dieser Schritt ist nötig bevor du deployen kannst!

Öffne die Datei `jenkins/Jenkinsfile.main` und ändere Zeile 30:

```groovy
// VORHER (Platzhalter):
stages.deploy('DEIN-SERVER.example.com', 'foodtruck-server-ssh')()

// NACHHER (dein echter Server):
stages.deploy('mein-server.de', 'foodtruck-server-ssh')()
```

**Hinweis:** Die Datei ist gitignored, also nur lokal ändern. Du musst sie dann auf den Jenkins-Server kopieren oder dort direkt bearbeiten.

### 12.5 Produktionsserver vorbereiten

Auf dem Server, auf dem die App laufen soll:

**1. Docker installieren:**
```bash
curl -fsSL https://get.docker.com | sh
```

**2. Deploy-User erstellen:**
```bash
sudo adduser deploy
sudo usermod -aG docker deploy
```

**3. SSH-Key hinzufügen:**
```bash
sudo -u deploy mkdir -p /home/deploy/.ssh
sudo -u deploy chmod 700 /home/deploy/.ssh

# Den Public Key vom Jenkins-Server hier einfügen:
echo "ssh-ed25519 AAAA... jenkins-deploy" | sudo -u deploy tee /home/deploy/.ssh/authorized_keys
sudo -u deploy chmod 600 /home/deploy/.ssh/authorized_keys
```

**4. .env Datei erstellen:**
```bash
sudo -u deploy bash -c 'cat > /home/deploy/.env << EOF
POSTGRES_DB=foodtruck
POSTGRES_USER=foodtruck
POSTGRES_PASSWORD=EIN_SICHERES_PASSWORT_HIER
EOF'
sudo -u deploy chmod 600 /home/deploy/.env
```

**5. Testen ob SSH funktioniert:**

Vom Jenkins-Server aus:
```bash
ssh -i ~/.ssh/foodtruck-server deploy@DEIN-SERVER "echo 'Verbindung OK'"
```

### 12.6 Ersten Build starten

1. In Jenkins: `foodtruck-booking` → `main`

2. **Build with Parameters** klicken

3. `RELEASE_VERSION` eingeben: `1.0.0`

4. **Build** klicken

5. Build beobachten:
   - Blau = läuft
   - Grün ✅ = erfolgreich
   - Rot ❌ = fehlgeschlagen (Console Output prüfen)

---

## 13. Troubleshooting

### "Credential not found: github-ssh"

**Problem:** Jenkins findet das GitHub-Credential nicht.

**Lösung:**
1. Prüfe ob das Credential existiert: Manage Jenkins → Credentials
2. Prüfe ob die ID genau `github-ssh` ist (Groß-/Kleinschreibung!)

### "Permission denied (publickey)" bei GitHub

**Problem:** Der SSH-Key wird nicht akzeptiert.

**Lösung:**
1. Prüfe ob der Public Key in GitHub hinterlegt ist
2. Teste manuell: `ssh -T git@github.com`

### "Host key verification failed"

**Problem:** Der Server ist nicht in known_hosts.

**Lösung:** Wird automatisch behoben durch `ssh-keyscan` in der Pipeline.

### Build bleibt hängen bei "Set Release Version"

**Problem:** Git push funktioniert nicht.

**Lösung:**
1. Prüfe GitHub SSH Credential
2. Prüfe ob der Jenkins-User Push-Rechte hat

---

## 14. Zusammenfassung

### Was bereits konfiguriert ist:

- ✅ Docker Registry Credential (`docker-registry-local`)
- ✅ Jenkins-Files für Foodtruck angepasst
- ✅ Dockerfile und docker-compose.deploy.yml vorhanden

### Was du noch tun musst:

1. ❌ **GitHub SSH Credential** anlegen (`github-ssh`)
2. ❌ **Server SSH Credential** anlegen (`foodtruck-server-ssh`)
3. ❌ **Multibranch Pipeline** erstellen
4. ❌ **Server-Hostname** in Jenkinsfile.main eintragen
5. ❌ **Produktionsserver** vorbereiten

Nach diesen Schritten ist die Pipeline einsatzbereit!

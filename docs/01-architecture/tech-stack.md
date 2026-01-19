# Tech Stack

## Zweck dieser Datei
Vollständige Liste aller verwendeten Technologien mit Versionen und Begründungen.

## Verwendung
- **Wann ausfüllen**: Zu Projektbeginn
- **Wann referenzieren**: Bei Dependency-Updates, neuen Libraries, Onboarding
- **Wann aktualisieren**: Bei jedem Technologie-Wechsel oder Version-Update

## Was du mit dem LLM machst
```
Prompt: "Aktualisiere den Tech Stack in /docs/01-architecture/tech-stack.md:

Backend: [Java 21, Spring Boot 3.2, ...]
Datenbank: [PostgreSQL 16, Redis, ...]
Testing: [JUnit 5, Mockito, Testcontainers, ...]
Build: [Maven/Gradle]
etc."
```

## Struktur

### Backend
Liste ALLE verwendeten Technologien:

```markdown
**Sprache**: Java XX
**Framework**: Spring Boot X.X.X
**Build Tool**: Maven / Gradle
```

### Spring Boot Module
Welche Spring-Starter werden verwendet?

```markdown
- `spring-boot-starter-web` - REST API
- `spring-boot-starter-data-jpa` - Datenbank-Zugriff
- `spring-boot-starter-security` - Sicherheit
- etc.
```

### Datenbank
```markdown
**Typ**: PostgreSQL / MySQL / MongoDB
**Version**: X.X
**Migration-Tool**: Flyway / Liquibase
```

### Testing
```markdown
**Unit-Tests**: JUnit 5
**Integration-Tests**: Testcontainers
**Mocking**: Mockito
**Assertions**: AssertJ
```

### Weitere Tools
- Logging: Logback, SLF4J
- Validation: Hibernate Validator
- Mapping: MapStruct
- etc.

### Entwicklungs-Tools
- IDE: IntelliJ IDEA
- Containerisierung: Docker, Docker Compose
- Versionskontrolle: Git + GitHub

### Deployment / Infrastruktur
(Wenn vorhanden)
- Cloud: AWS / Azure / GCP
- Container: Kubernetes
- CI/CD: GitHub Actions / Jenkins
- Monitoring: Prometheus, Grafana

---

## ⚠️ Best Practice
Bei jeder neuen Dependency:
1. Version angeben
2. Kurze Begründung schreiben
3. Ggf. ADR erstellen (bei wichtigen Entscheidungen)

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Code generiert wird (damit richtige Imports/Annotations verwendet werden)
- Dependencies hinzugefügt werden sollen
- Version-Updates geplant sind

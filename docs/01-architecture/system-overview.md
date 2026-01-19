# System Overview

## Zweck dieser Datei
Big Picture der Systemarchitektur. Zeigt die Hauptkomponenten und deren Zusammenspiel.

## Verwendung
- **Wann ausfüllen**: Zu Projektbeginn + bei strukturellen Änderungen
- **Wann referenzieren**: Bei Architekturentscheidungen, Onboarding, größeren Features
- **Wann aktualisieren**: Bei neuen Komponenten oder Technologie-Wechseln

## Was du mit dem LLM machst
```
Prompt: "Schreibe den System Overview in /docs/01-architecture/system-overview.md basierend auf diesen Infos:

Komponenten: [Backend: Spring Boot, DB: PostgreSQL, Frontend: ...]
Kommunikation: [REST API, ...]
Deployment: [Docker, Kubernetes, ...]"
```

## Struktur

### Architektur-Diagramm
Erstelle ein einfaches ASCII/Mermaid-Diagramm der Hauptkomponenten.

**Beispiel ASCII**:
```
┌─────────────┐
│   Frontend  │
└──────┬──────┘
       │ HTTP/REST
       ▼
┌─────────────┐
│   Backend   │
└──────┬──────┘
       │ JDBC
       ▼
┌─────────────┐
│   Database  │
└─────────────┘
```

### Komponenten-Beschreibung
Für jede Komponente:
- **Name/Typ**: Was ist es?
- **Technologie**: Welcher Stack?
- **Aufgabe**: Was macht es?
- **Port/URL**: Wo erreichbar?
- **Deployment**: Wie deployed?

**Beispiel**:
```markdown
### Backend
- **Framework**: Spring Boot X.X
- **Aufgabe**: REST API für [Domäne]
- **Port**: 8080
- **Deployment**: Docker Container
```

### Schnittstellen
Wie kommunizieren die Komponenten?

**Beispiel**:
- REST API: `/api/v1`, JSON-Format
- WebSocket: Für Realtime-Updates
- Message Queue: Kafka für Async-Processing

### Deployment-Übersicht
Wie wird das System deployed?

**Lokal**:
- Backend: Maven / Gradle
- Datenbank: Docker Compose
- etc.

**Produktion**:
- Cloud Provider
- Container-Orchestrierung
- etc.

---

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Architekturentscheidungen getroffen werden
- Neue Komponenten hinzugefügt werden
- Integration zwischen Komponenten implementiert wird
- Onboarding stattfindet

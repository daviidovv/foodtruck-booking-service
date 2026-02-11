# System Overview - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-11

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Architekturentscheidungen (neue Komponenten)
- Bei Integration zwischen Komponenten
- Bei Onboarding neuer Entwickler
- Bei Deployment-Planung
- Bei Performance-Optimierung (Bottleneck-Analyse)

**Prompt-Beispiel:**
```
Plane die Integration von [Feature] basierend auf:
- /docs/01-architecture/system-overview.md (Architektur-Kontext)
- /docs/01-architecture/tech-stack.md (Technologien)
```

---

## Architektur-Diagramm

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              INTERNET                                         │
└─────────────────────────────────────┬────────────────────────────────────────┘
                                      │
                                      │ HTTPS
                                      ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (React + Vite)                            │
│                                                                              │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐             │
│  │  Kunden-UI      │  │ Mitarbeiter-UI  │  │   Admin-UI      │             │
│  │  (Reservierung) │  │    (Tablet)     │  │  (Dashboard)    │             │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘             │
│                                                                              │
│  React 18 │ TypeScript 5 │ Tailwind CSS │ shadcn/ui │ React Query          │
│                                                                              │
│  Port: 5173 (Dev) / 80 (Prod)                                               │
└─────────────────────────────────────┬────────────────────────────────────────┘
                                      │
                                      │ REST API (JSON)
                                      │ /api/v1/*
                                      ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                        BACKEND (Spring Boot 4.0.1)                           │
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         REST Controller Layer                        │    │
│  │  LocationController │ ReservationController │ StaffController │ ...  │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                           Service Layer                              │    │
│  │  LocationService │ ReservationService │ CapacityService │ ...        │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                      │                                       │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                         Repository Layer (JPA)                       │    │
│  │  LocationRepository │ ReservationRepository │ ScheduleRepository     │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│  Java 25 │ Spring Security │ Flyway │ Hibernate │ Lombok                    │
│                                                                              │
│  Port: 8080                                                                  │
└─────────────────────────────────────┬────────────────────────────────────────┘
                                      │
                                      │ JDBC (PostgreSQL Protocol)
                                      ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           DATABASE (PostgreSQL 16)                           │
│                                                                              │
│  ┌─────────────┐  ┌─────────────────────┐  ┌─────────────────┐             │
│  │  location   │  │  location_schedule  │  │   reservation   │             │
│  └─────────────┘  └─────────────────────┘  └─────────────────┘             │
│                                                                              │
│  Port: 5432                                                                  │
└──────────────────────────────────────────────────────────────────────────────┘
```

---

## Komponenten-Beschreibung

### Frontend (React SPA)

| Aspekt | Details |
|--------|---------|
| **Technologie** | React 18 + TypeScript 5 + Vite 5 |
| **Aufgabe** | Single-Page-Application für alle Benutzergruppen |
| **Port (Dev)** | 5173 |
| **Port (Prod)** | 80/443 |
| **Deployment** | Statische Dateien (Nginx / CDN) |

**Sub-Komponenten:**

| UI-Bereich | Zielgruppe | Beschreibung |
|------------|------------|--------------|
| Kunden-UI | Endkunden | Reservierung erstellen, Standorte sehen |
| Mitarbeiter-UI | Foodtruck-Personal | Tablet-Ansicht, Reservierungen verwalten |
| Admin-UI | Betreiber | Dashboard, Standort-/Kapazitätsverwaltung |

**Kommunikation:**
- REST API via Axios
- React Query für Caching & Synchronisation
- Auto-Refresh für Echtzeit-Updates (Polling, 30s Intervall)

---

### Backend (Spring Boot Monolith)

| Aspekt | Details |
|--------|---------|
| **Technologie** | Java 25 + Spring Boot 4.0.1 |
| **Aufgabe** | REST API, Business Logic, Datenbankzugriff |
| **Port** | 8080 |
| **Deployment** | Docker Container / JAR |

**Layered Architecture:**

```
┌─────────────────────────────────────────────────┐
│              Controller Layer                    │
│  - HTTP Request/Response Handling               │
│  - Input Validation (@Valid)                    │
│  - DTO-Mapping                                  │
├─────────────────────────────────────────────────┤
│                Service Layer                     │
│  - Business Logic                               │
│  - Transaktionsmanagement (@Transactional)      │
│  - Kapazitätsprüfung, Status-Übergänge         │
├─────────────────────────────────────────────────┤
│              Repository Layer                    │
│  - JPA Repositories                             │
│  - Database Queries                             │
│  - Entity Management                            │
├─────────────────────────────────────────────────┤
│                Entity Layer                      │
│  - JPA Entities (Location, Reservation, etc.)   │
│  - Audit Fields (createdAt, updatedAt)         │
└─────────────────────────────────────────────────┘
```

**Security:**
- Spring Security
- Basic Auth (MVP) / JWT (Phase 2)
- Rollen: `ROLE_STAFF`, `ROLE_ADMIN`
- Public Endpoints für Kunden (keine Auth)

---

### Datenbank (PostgreSQL)

| Aspekt | Details |
|--------|---------|
| **Technologie** | PostgreSQL 16 |
| **Aufgabe** | Persistente Datenspeicherung |
| **Port** | 5432 |
| **Deployment** | Docker Container (Dev) / Managed DB (Prod) |

**Schema:**
- `location` - Standorte
- `location_schedule` - Wochenpläne mit Öffnungszeiten/Kapazität
- `reservation` - Kundenreservierungen

**Migrations:**
- Flyway für Schema-Versionierung
- `V1__create_initial_schema.sql` etc.

---

## Datenfluss

### Reservierung erstellen (Kunde)

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  Kunde   │───>│ Frontend │───>│ Backend  │───>│    DB    │
│ (Browser)│    │  (React) │    │ (Spring) │    │(Postgres)│
└──────────┘    └──────────┘    └──────────┘    └──────────┘
     │               │               │               │
     │  1. Formular  │               │               │
     │     ausfüllen │               │               │
     │               │ 2. POST       │               │
     │               │ /reservations │               │
     │               │───────────────>               │
     │               │               │ 3. Validieren │
     │               │               │    Kapazität  │
     │               │               │    prüfen     │
     │               │               │───────────────>
     │               │               │ 4. INSERT     │
     │               │               │<───────────────
     │               │               │ 5. UUID       │
     │               │<───────────────               │
     │               │ 6. 201 Created│               │
     │<───────────────               │               │
     │ 7. Bestätigung│               │               │
     │    anzeigen   │               │               │
```

### Reservierungen abrufen (Mitarbeiter)

```
┌──────────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ Mitarbeiter  │───>│ Frontend │───>│ Backend  │───>│    DB    │
│   (Tablet)   │    │  (React) │    │ (Spring) │    │(Postgres)│
└──────────────┘    └──────────┘    └──────────┘    └──────────┘
     │                   │               │               │
     │  1. Login         │               │               │
     │──────────────────>│               │               │
     │                   │ 2. POST /auth │               │
     │                   │───────────────>               │
     │                   │<───────────────               │
     │                   │ 3. Session    │               │
     │                   │               │               │
     │  4. Dashboard     │               │               │
     │     öffnen        │               │               │
     │                   │ 5. GET        │               │
     │                   │ /staff/       │               │
     │                   │ reservations  │               │
     │                   │───────────────>               │
     │                   │               │ 6. SELECT     │
     │                   │               │    WHERE      │
     │                   │               │    location   │
     │                   │               │───────────────>
     │                   │               │<───────────────
     │                   │               │ 7. Results    │
     │                   │<───────────────               │
     │                   │ 8. JSON       │               │
     │<───────────────────               │               │
     │ 9. Liste anzeigen │               │               │
     │                   │               │               │
     │   [Auto-Refresh   │               │               │
     │    alle 30 Sek.]  │               │               │
```

---

## Schnittstellen

### REST API

| Schnittstelle | Beschreibung |
|---------------|--------------|
| **Base URL** | `/api/v1` |
| **Content-Type** | `application/json` |
| **Auth** | Basic Auth Header (MVP) |

**Endpoint-Gruppen:**
- `/api/v1/locations/*` - Standorte (Public)
- `/api/v1/reservations/*` - Reservierungen (Public: Create, Get)
- `/api/v1/staff/*` - Mitarbeiter-Funktionen (Protected)
- `/api/v1/admin/*` - Admin-Funktionen (Protected)
- `/api/v1/auth/*` - Authentifizierung

**Dokumentation:**
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

### Datenbank-Verbindung

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/foodtruck
    username: foodtruck
    password: ${POSTGRES_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway übernimmt Schema!
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

---

## Deployment-Übersicht

### Lokale Entwicklung

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose                            │
│                                                             │
│  ┌─────────────────┐                                        │
│  │   PostgreSQL    │ ← Port 5432                            │
│  │   (Container)   │                                        │
│  └─────────────────┘                                        │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────┐    ┌─────────────────┐
│   Backend       │    │   Frontend      │
│   (IDE/Maven)   │    │   (npm/Vite)    │
│   Port: 8080    │    │   Port: 5173    │
└─────────────────┘    └─────────────────┘
```

**Start-Befehle:**
```bash
# 1. Datenbank starten
docker-compose up -d postgres

# 2. Backend starten
./mvnw spring-boot:run

# 3. Frontend starten
cd frontend && npm run dev
```

### Produktion (Geplant Phase 2+)

```
┌─────────────────────────────────────────────────────────────┐
│                    Cloud Environment                         │
│                                                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │    Frontend     │  │    Backend      │                  │
│  │   (Container)   │  │   (Container)   │                  │
│  │   Nginx + SPA   │  │   Spring Boot   │                  │
│  └────────┬────────┘  └────────┬────────┘                  │
│           │                    │                            │
│           └────────────────────┘                            │
│                    │                                        │
│                    ▼                                        │
│           ┌─────────────────┐                              │
│           │   PostgreSQL    │                              │
│           │  (Managed DB)   │                              │
│           └─────────────────┘                              │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Sicherheits-Architektur

### Authentifizierung

```
┌──────────────────────────────────────────────────────────────┐
│                        Endpoints                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  PUBLIC (Keine Auth)         PROTECTED (Auth Required)       │
│  ────────────────────        ─────────────────────────       │
│  GET  /api/v1/locations      GET  /api/v1/staff/*            │
│  GET  /api/v1/locations/*/   PATCH /api/v1/staff/*           │
│       schedule               GET  /api/v1/admin/*            │
│  GET  /api/v1/locations/*/   POST /api/v1/admin/*            │
│       availability           PUT  /api/v1/admin/*            │
│  POST /api/v1/reservations   DELETE /api/v1/admin/*          │
│  GET  /api/v1/reservations/* POST /api/v1/auth/login         │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

### Rollen

| Rolle | Berechtigung |
|-------|--------------|
| `ROLE_STAFF` | Reservierungen des eigenen Standorts verwalten |
| `ROLE_ADMIN` | Alle Standorte, Konfiguration, Statistiken |

---

## Monitoring & Logging (MVP)

### Spring Actuator Endpoints

| Endpoint | Beschreibung |
|----------|--------------|
| `/actuator/health` | Health Check (Liveness/Readiness) |
| `/actuator/info` | Anwendungsinformationen |
| `/actuator/metrics` | Metriken (JVM, HTTP, etc.) |

### Logging

- **Format:** JSON (Production), Console (Development)
- **Framework:** SLF4J + Logback
- **Log Levels:**
  - `ERROR` - Fehler, die Intervention erfordern
  - `WARN` - Potenzielle Probleme
  - `INFO` - Business Events (Reservierung erstellt, etc.)
  - `DEBUG` - Technische Details (nur Development)

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initiale Architektur-Dokumentation |

---

## 🔴 Wichtig für LLM

**Diese Datei gibt das Big Picture der Architektur.**

**Verwenden zusammen mit:**
- `/docs/01-architecture/tech-stack.md` (Technologie-Details)
- `/docs/01-architecture/data-model.md` (Datenbank-Schema)
- `/docs/02-requirements/api-contracts.md` (API-Details)

**Architektur-Regeln:**
- Monolithische Architektur (kein Microservices ohne ADR)
- Layered Architecture (Controller → Service → Repository)
- REST API (kein GraphQL ohne ADR)
- PostgreSQL (keine andere DB ohne ADR)

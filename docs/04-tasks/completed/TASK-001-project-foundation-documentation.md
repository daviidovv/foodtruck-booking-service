# TASK-001: Project Foundation - Requirements & Architecture Documentation

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [x] Review
- [ ] Abgeschlossen

## Kontext

Aktueller Stand:
- ✅ vision.md ist vollständig (Multi-Standort-Konzept dokumentiert)
- ✅ glossary.md ist vollständig
- ✅ mandatory-context.md ist vollständig (Tech-Stack, DB-Schema, Standards)
- ⚠️ **forbidden-actions.md** ist nur Template (KRITISCH!)
- ⚠️ **functional-requirements.md** ist nur Anleitung (leer)
- ⚠️ **api-contracts.md** ist nur Anleitung (leer)
- ⚠️ **data-model.md** ist nur Anleitung (leer)
- ⚠️ **tech-stack.md** ist nur Anleitung (leer)
- ⚠️ **system-overview.md** ist nur Anleitung (leer)
- ⚠️ **non-functional-requirements.md** ist nur Anleitung (leer)
- ⚠️ **user-stories.md** ist nur Anleitung (leer)

**Problem:**
Ohne vollständige Dokumentation kann keine konsistente Code-Generierung erfolgen. Die Grundlagen-Dokumentation ist die Basis für alle weiteren Implementation-Tasks.

## Ziel

Vervollständige alle kritischen Dokumentations-Dateien, sodass:
1. LLM bei Code-Generierung alle notwendigen Infos hat (mandatory-context + forbidden-actions)
2. Klare Requirements existieren (functional + non-functional)
3. API-Contracts vor Implementation definiert sind
4. Architektur dokumentiert ist
5. Datenmodell detailliert beschrieben ist

**Messbar:** Alle 8 Dateien sind vollständig ausgefüllt und commit-ready.

## Akzeptanzkriterien

### 🔴 Kritisch (MUSS)
- [x] **forbidden-actions.md** vollständig ausgefüllt mit projektspezifischen Verboten
- [x] **functional-requirements.md** mit allen MVP-Requirements (REQ-001 bis REQ-019)
- [x] **api-contracts.md** mit allen REST-Endpoints für Reservierungen, Locations, Schedules
- [x] **data-model.md** mit detaillierter Beschreibung aller 3 Tabellen (reservation, location, location_schedule)

### ⚠️ Wichtig (SOLLTE)
- [x] **tech-stack.md** dokumentiert aktuellen Stack (aus mandatory-context.md übernehmen)
- [x] **system-overview.md** dokumentiert Architektur-Big-Picture mit ASCII-Diagramm
- [x] **non-functional-requirements.md** mit Performance, Security, DSGVO-Anforderungen
- [x] **user-stories.md** mit Epics und Stories für Kunden, Mitarbeiter, Admin

### ✅ Nice-to-have
- [ ] ADR-002 für Frontend-Framework-Entscheidung (React + TypeScript)
- [ ] ADR-003 für UI-Library-Entscheidung (shadcn/ui + Tailwind)

## Constraints

### Technische Einschränkungen
- Dokumentation muss mit vision.md und mandatory-context.md konsistent sein
- Alle Requirements müssen MVP-Scope beachten (keine Phase 2 Features)
- API-Contracts müssen RESTful Best Practices folgen

### Zeitliche Vorgaben
- Keine - Dokumentations-Task hat Priorität vor Implementation
- Qualität > Geschwindigkeit

### Abhängigkeiten
- **KEINE** - Das ist der allererste Task
- Alle folgenden Tasks hängen von diesem ab

## Betroffene Komponenten

**Keine Code-Komponenten** - reine Dokumentation:
- `/docs/03-prompts/forbidden-actions.md`
- `/docs/02-requirements/functional-requirements.md`
- `/docs/02-requirements/non-functional-requirements.md`
- `/docs/02-requirements/user-stories.md`
- `/docs/02-requirements/api-contracts.md`
- `/docs/01-architecture/data-model.md`
- `/docs/01-architecture/tech-stack.md`
- `/docs/01-architecture/system-overview.md`

## Verknüpfungen

- Vision: `/docs/00-meta/vision.md` (Basis für Requirements)
- Glossary: `/docs/00-meta/glossary.md` (Begriffe für Requirements)
- Mandatory Context: `/docs/03-prompts/mandatory-context.md` (Tech-Basis)
- ADR-001: PostgreSQL-Entscheidung

## Offene Fragen

### 1. E-Mail-Pflicht bei Reservierungen?
**Status:** Offen (aus vision.md)
**Entscheidung notwendig:** JA oder NEIN
**Impact:** Beeinflusst functional-requirements.md, api-contracts.md, data-model.md

**Empfehlung für MVP:**
- **E-Mail OPTIONAL** (customer_email kann NULL sein)
- Vorteile: Niedrige Hürde + Bestätigungsmail wenn gewünscht
- Implementation: Wenn E-Mail vorhanden → Mail senden, sonst nur Name

### 2. Kapazitätsmanagement-Strategie?
**Status:** Offen
**Optionen:**
- A) Admin gibt täglich pro Standort Kapazität ein
- B) System nutzt location_schedule.daily_capacity als Standard
- C) Automatische Berechnung basierend auf Historie

**Empfehlung für MVP:** Option B (Standard aus DB, manuell überschreibbar in Phase 2)

### 3. Stornierungsfunktion im MVP?
**Status:** Offen
**Frage:** Können Kunden selbst stornieren?

**Empfehlung für MVP:**
- NEIN für Kunden (zu komplex ohne E-Mail-Verifikation)
- JA für Mitarbeiter (können Reservierungen auf CANCELLED setzen)

### 4. Authentifizierung für Mitarbeiter/Admin?
**Status:** Offen für Detail-Design
**MVP:** Spring Security Basic Auth
**Phase 2:** JWT-basiert

**Für diesen Task:** Dokumentiere in functional-requirements.md, dass Mitarbeiter/Admin Login benötigen

## Detaillierte Aufgabenliste

### 1. forbidden-actions.md (KRITISCH - 10 Min)
**Inhalt:**
- Code-Änderungen: Package-Umbenennungen, API-Breaking-Changes, Dependencies
- Architektur: REST→GraphQL, neue Layer ohne ADR
- Qualität: Tests löschen, Warnings ignorieren
- Datenbank: Flyway-History ändern, Breaking Schema Changes
- Anti-Patterns: Field Injection, God Classes, Entities nach außen
- Frontend: `any` Type, Prop Drilling, Redux

### 2. functional-requirements.md (30 Min)
**Requirements-Gruppen:**

**Reservierungsverwaltung (Kunde):**
- REQ-001: Standort auswählen basierend auf Wochentag
- REQ-002: Reservierung erstellen (Hähnchen, Pommes, Abholzeit, optional E-Mail)
- REQ-003: Reservierungsbestätigung erhalten (E-Mail wenn vorhanden)
- REQ-004: Verfügbare Standorte/Zeiten sehen

**Reservierungsverwaltung (Mitarbeiter):**
- REQ-005: Login für Mitarbeiter
- REQ-006: Reservierungen des Tages am eigenen Standort sehen
- REQ-007: Reservierung bestätigen (PENDING → CONFIRMED)
- REQ-008: Reservierung stornieren (→ CANCELLED)
- REQ-009: Reservierung abschließen (→ COMPLETED oder NO_SHOW)
- REQ-010: Verfügbare Kapazität sehen

**Standortverwaltung (Admin):**
- REQ-011: Login für Admin
- REQ-012: Standorte verwalten (CRUD)
- REQ-013: Wochentags-Schedule konfigurieren
- REQ-014: Kapazität pro Standort/Tag konfigurieren
- REQ-015: Alle Reservierungen (beide Standorte) sehen
- REQ-016: Statistiken sehen (Auslastung, etc.)

**Validierung & Business Rules:**
- REQ-017: Zeitvalidierung (Abholzeit in Zukunft, innerhalb Öffnungszeiten)
- REQ-018: Kapazitätsprüfung (nicht überbuchen)
- REQ-019: Doppelbuchungen vermeiden (gleicher Name/E-Mail zur gleichen Zeit)

### 3. api-contracts.md (45 Min)
**Endpoint-Gruppen:**

**Public API (Kunden):**
```
GET    /api/v1/locations                    # Alle Standorte
GET    /api/v1/locations/{id}/schedule      # Schedule für Standort
GET    /api/v1/locations/{id}/availability  # Verfügbarkeit prüfen
POST   /api/v1/reservations                 # Reservierung erstellen
GET    /api/v1/reservations/{id}            # Reservierung abrufen (mit ID oder Token)
```

**Protected API (Mitarbeiter):**
```
POST   /api/v1/auth/login                   # Login
GET    /api/v1/staff/reservations           # Reservierungen des Standorts
PATCH  /api/v1/staff/reservations/{id}/status  # Status ändern
GET    /api/v1/staff/capacity               # Aktuelle Kapazität
```

**Admin API:**
```
GET    /api/v1/admin/reservations           # Alle Reservierungen
POST   /api/v1/admin/locations              # Standort erstellen
PUT    /api/v1/admin/locations/{id}         # Standort ändern
POST   /api/v1/admin/locations/{id}/schedule  # Schedule hinzufügen
GET    /api/v1/admin/statistics             # Statistiken
```

Für jeden Endpoint: Request/Response Schema, Errors, HTTP Status Codes

### 4. data-model.md (30 Min)
**Detaillierte Beschreibung:**
- ER-Diagramm (ASCII)
- Alle 3 Tabellen mit ALLEN Spalten
- Datentypen, Constraints, Foreign Keys
- Indizes mit Begründung
- Status-Enum-Werte
- Beispiel-Daten
- Migration-Strategie

### 5. tech-stack.md (15 Min)
**Übernehmen aus mandatory-context.md:**
- Backend: Java 25, Spring Boot 4.0.1, PostgreSQL 16, Maven, Flyway
- Frontend: React 18, TypeScript 5, Vite 5, Tailwind CSS, React Query, shadcn/ui
- Testing: JUnit 5, Mockito, Testcontainers, Vitest, RTL
- DevOps: Docker, Docker Compose
- Begründungen (kurz)

### 6. system-overview.md (20 Min)
**Architektur-Diagramm:**
```
[Kunde Browser] ← HTTP → [React Frontend (Vite)]
                              ↓ Axios
                         [REST API]
                    (Spring Boot Backend)
                              ↓ JPA
                        [PostgreSQL]
```

- Komponenten-Beschreibung
- Deployment-Strategie
- Schnittstellen

### 7. non-functional-requirements.md (25 Min)
**NFR-Kategorien:**
- Performance: Response Time < 200ms, 100 concurrent users
- Availability: 99% Uptime
- Security: DSGVO-konform, OWASP Top 10, verschlüsselte Verbindungen
- Scalability: 10.000+ Reservierungen
- Maintainability: 80% Test-Coverage
- Usability: Mobile-optimiert, Lighthouse > 90
- Compliance: DSGVO, Impressumspflicht

### 8. user-stories.md (25 Min)
**Epics:**
- Epic 1: Kundenreservierung
- Epic 2: Mitarbeiter-Verwaltung
- Epic 3: Admin-Dashboard

Pro Epic 3-5 User Stories mit Akzeptanzkriterien

**Beispiel:**
```
US-001: Standort auswählen
Als Kunde möchte ich sehen, welcher Foodtruck wann an welchem Standort ist,
damit ich den richtigen Standort für meine Reservierung wähle.

Akzeptanzkriterien:
- Kalender-Ansicht zeigt Standorte pro Wochentag
- Öffnungszeiten werden angezeigt
- Nur verfügbare Tage sind auswählbar
```

## Entscheidungen während Umsetzung

| Datum | Entscheidung | Begründung |
|-------|--------------|------------|
|       |              |            |

## Implementierungs-Notizen

**Reihenfolge:**
1. forbidden-actions.md (KRITISCH - ohne diese keine Code-Generierung)
2. functional-requirements.md (Basis für alles)
3. api-contracts.md (Basis für Controller)
4. data-model.md (Basis für Entities)
5. tech-stack.md (Dokumentation)
6. system-overview.md (Dokumentation)
7. non-functional-requirements.md (Qualitätsanforderungen)
8. user-stories.md (Fachliche Sicht)

**Wichtig:**
- E-Mail-Frage entscheiden (Empfehlung: optional)
- Konsistenz mit vision.md und mandatory-context.md prüfen
- Alle Begriffe aus glossary.md verwenden

## Prompt-Vorlage für LLM

```
Ich möchte TASK-001 umsetzen: Project Foundation Documentation.

Kontext-Dateien:
- /docs/00-meta/vision.md (Projektvision mit Multi-Standort)
- /docs/00-meta/glossary.md (Fachbegriffe)
- /docs/03-prompts/mandatory-context.md (Tech-Stack, DB-Schema)

Bitte fülle folgende Dateien aus:

1. /docs/03-prompts/forbidden-actions.md (KRITISCH!)
   - Projektspezifische Verbote basierend auf mandatory-context.md

2. /docs/02-requirements/functional-requirements.md
   - REQ-001 bis REQ-019 (siehe Task-Beschreibung)
   - Gruppiert nach: Kunde, Mitarbeiter, Admin, Validierung

3. /docs/02-requirements/api-contracts.md
   - Public API, Protected API, Admin API
   - Alle Endpoints mit Request/Response/Errors

4. /docs/01-architecture/data-model.md
   - ER-Diagramm
   - Detaillierte Tabellen-Beschreibung (reservation, location, location_schedule)

5-8. Weitere Dateien gemäß Task-Beschreibung

Offene Fragen:
- E-Mail: OPTIONAL (customer_email kann NULL sein)
- Kapazität: Aus location_schedule.daily_capacity
- Stornierung Kunden: NEIN (nur Mitarbeiter)
- Auth: Basic Auth für MVP

Beginne mit forbidden-actions.md!
```

## Ergebnis

**Implementierte Änderungen**:
- `api-contracts.md`: Vollständige REST-API-Spezifikation mit 15+ Endpoints, Request/Response-Schemas, Error-Handling (RFC 7807)
- `data-model.md`: ER-Diagramm, detaillierte Tabellenbeschreibungen, JPA-Entity-Beispiele, Flyway-Migration, Status-Enum
- `tech-stack.md`: Backend (Java 25, Spring Boot 4.0.1), Frontend (React 18, TypeScript 5), Testing, DevOps
- `system-overview.md`: Architektur-Diagramme, Komponenten-Beschreibungen, Datenfluss, Deployment
- `non-functional-requirements.md`: 20 NFRs (Performance, Security, DSGVO, Usability)
- `user-stories.md`: 15 User Stories in 3 Epics (Kunde, Mitarbeiter, Admin), Story Map, MoSCoW-Priorisierung
- Alle Dateien mit "Zweck & Verwendung beim Prompting"-Sektion für LLM-Kontext

**Commits**:
- Noch nicht committed (ready for review)

**Learnings**:
- `forbidden-actions.md` und `functional-requirements.md` waren bereits ausgefüllt
- Konsistenz mit vision.md und mandatory-context.md war kritisch
- E-Mail optional (customer_email NULL) als Design-Entscheidung dokumentiert

**Follow-up Tasks**:
- [ ] TASK-002: Datenbank-Migrationen (Flyway) schreiben
- [ ] TASK-003: Entity-Klassen implementieren
- [ ] TASK-004: Repository-Layer implementieren
- [ ] TASK-005: Service-Layer implementieren
- [ ] TASK-006: REST-Controller implementieren
- [ ] TASK-007: Frontend-Projekt-Setup

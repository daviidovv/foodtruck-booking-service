# Mandatory Context - Foodtruck Booking Service

Letzte Aktualisierung: 2026-01-19

## 🔴 KRITISCH 🔴
Diese Datei MUSS bei JEDEM Code-generierenden Prompt mitgegeben werden!

---

## Tech-Stack

### Backend
- **Java**: 25
- **Spring Boot**: 4.0.1
- **Datenbank**: PostgreSQL 16
- **Build Tool**: Maven 3.9+
- **Migration Tool**: Flyway

### Frontend
- **Framework**: React 18.3+
- **Language**: TypeScript 5.3+
- **Build Tool**: Vite 5+
- **Package Manager**: npm
- **Styling**: Tailwind CSS 3.4+
- **HTTP Client**: Axios 1.6+
- **State Management**: React Query (TanStack Query) 5+ für Server-State
- **Routing**: React Router 6+
- **Form Handling**: React Hook Form + Zod (Validation)
- **UI Components**: shadcn/ui (Radix UI primitives + Tailwind)
- **Date/Time**: date-fns

### Testing

**Backend:**
- JUnit 5 (Jupiter)
- Mockito 5+
- AssertJ
- Testcontainers (PostgreSQL)
- Spring Boot Test

**Frontend:**
- Vitest (Unit Tests)
- React Testing Library
- MSW (Mock Service Worker) für API-Mocking

### DevOps & Tools
- **Containerisierung**: Docker & Docker Compose
- **API Dokumentation**: SpringDoc OpenAPI (Swagger UI)
- **Code Quality**: SonarLint, ESLint, Prettier
- **Versionskontrolle**: Git + GitHub

---

## Spring Boot Module

### Produktive Dependencies
```xml
- spring-boot-starter-web (REST API)
- spring-boot-starter-data-jpa (Datenbankzugriff)
- spring-boot-starter-validation (Bean Validation)
- spring-boot-starter-security (Security)
- spring-boot-starter-actuator (Health Checks & Monitoring)
- spring-boot-starter-flyway (DB Migrations)
- flyway-database-postgresql (PostgreSQL Support)
- postgresql (PostgreSQL JDBC Driver)
- lombok (Code-Generierung)
- springdoc-openapi-starter-webmvc-ui (OpenAPI/Swagger)
```

### Test Dependencies
```xml
- spring-boot-starter-test
- spring-boot-starter-security-test
- h2 (In-Memory DB für Tests)
- testcontainers (Container-basierte Integration Tests)
```

---

## Package-Struktur

### Backend (Java)
```
org.example.foodtruckbookingservice
├── controller          # REST Controller
├── service            # Business Logic
├── repository         # Data Access Layer (JPA Repositories)
├── entity             # JPA Entities
├── dto                # Data Transfer Objects
│   ├── request        # Request DTOs
│   └── response       # Response DTOs
├── mapper             # Entity ↔ DTO Mapper
├── exception          # Custom Exceptions
├── config             # Configuration Classes
├── security           # Security Configuration
└── util               # Utility Classes
```

### Frontend (React + TypeScript)
```
src/
├── components/        # React Components
│   ├── ui/           # Reusable UI Components (shadcn/ui)
│   ├── layout/       # Layout Components (Header, Footer, etc.)
│   └── features/     # Feature-spezifische Components
├── pages/            # Page Components (Routen)
├── hooks/            # Custom React Hooks
├── services/         # API Services (Axios)
├── types/            # TypeScript Types & Interfaces
├── utils/            # Utility Functions
├── constants/        # Constants
└── styles/           # Global Styles
```

---

## Coding-Constraints

### Backend (Java)

**Dependency Injection:**
- ✅ Constructor Injection (IMMER)
- ❌ Field Injection (@Autowired auf Feldern) VERBOTEN

**Lombok:**
- ✅ @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor für DTOs und Entities
- ✅ @Slf4j für Logging
- ❌ @SneakyThrows VERBOTEN
- ❌ @Cleanup VERBOTEN

**Validation:**
- ✅ @Validated auf allen Controllern
- ✅ Bean Validation Annotations auf DTOs (@NotNull, @NotBlank, @Email, @Size, etc.)
- ✅ Custom Validators für komplexe Business-Regeln

**Transactions:**
- ✅ @Transactional(readOnly = true) auf Service-Klassen-Ebene
- ✅ @Transactional (ohne readOnly) auf schreibenden Methoden
- ❌ Transactions NIEMALS in Controllern

**Entities:**
- ✅ IMMER Audit-Felder (createdAt, updatedAt) mit @CreatedDate und @LastModifiedDate
- ✅ UUID als ID-Typ (nicht Long/Integer)
- ✅ @Entity, @Table Annotations
- ❌ Business-Logik in Entities VERBOTEN

**DTOs:**
- ✅ Separate Request und Response DTOs
- ✅ Entities NIEMALS direkt nach außen geben
- ✅ Mapper-Klassen für Entity ↔ DTO Konvertierung
- ❌ Entities in Controller-Signaturen VERBOTEN

**Exception Handling:**
- ✅ @RestControllerAdvice für globales Exception Handling
- ✅ Custom Exceptions für Business-Fehler
- ✅ RFC 7807 Problem Details Format für Fehler-Responses

**Logging:**
- ✅ SLF4J mit Lombok @Slf4j
- ✅ Strukturiertes Logging (JSON für Production)
- ✅ Log Levels: ERROR (Fehler), WARN (Warnung), INFO (Business Events), DEBUG (Details)

### Frontend (React + TypeScript)

**TypeScript:**
- ✅ Strict Mode aktiviert
- ✅ Explizite Typen für Props, State, Funktionen
- ✅ Interfaces für API-Responses
- ❌ `any` Type VERMEIDEN (nur in Ausnahmefällen)

**React Best Practices:**
- ✅ Functional Components (keine Class Components)
- ✅ Custom Hooks für wiederverwendbare Logik
- ✅ Props Destructuring
- ✅ Early Returns für bessere Lesbarkeit
- ❌ Prop Drilling vermeiden (Context oder State Management nutzen)

**State Management:**
- ✅ React Query für Server-State (API-Daten)
- ✅ useState/useContext für UI-State
- ❌ Redux NICHT verwenden (zu komplex für dieses Projekt)

**Styling:**
- ✅ Tailwind CSS Utility Classes
- ✅ Responsive Design (Mobile First)
- ✅ Dark Mode Support (optional für Phase 2)

**API Calls:**
- ✅ Axios für HTTP Requests
- ✅ Interceptors für Auth-Token
- ✅ Error Handling mit try-catch
- ✅ Loading States und Error States

---

## Aktuelle Datenbank-Struktur

### Tabellen (Stand: Init)

**reservation (Haupttabelle)**
- `id` (UUID, PK): Primärschlüssel
- `location_id` (UUID, FK): Referenz auf Standort
- `customer_name` (VARCHAR 200, NOT NULL): Kundenname
- `customer_email` (VARCHAR 255, NULL): Kunden-E-Mail (optional)
- `chicken_count` (INT, NOT NULL): Anzahl Hähnchen
- `fries_count` (INT, NOT NULL): Anzahl Pommes
- `pickup_time` (TIMESTAMP, NOT NULL): Abholzeitpunkt
- `status` (VARCHAR 30, NOT NULL): Status (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- `notes` (TEXT, NULL): Zusätzliche Notizen
- `created_at` (TIMESTAMP, NOT NULL): Erstellungszeitpunkt
- `updated_at` (TIMESTAMP, NOT NULL): Letzte Änderung

**location (Standorte)**
- `id` (UUID, PK): Primärschlüssel
- `name` (VARCHAR 200, NOT NULL): Standortname (z.B. "Innenstadt", "Gewerbegebiet")
- `address` (VARCHAR 500, NOT NULL): Adresse
- `active` (BOOLEAN, NOT NULL): Aktiv/Inaktiv
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP, NOT NULL)

**location_schedule (Wochentags-Zuordnung)**
- `id` (UUID, PK): Primärschlüssel
- `location_id` (UUID, FK): Referenz auf Standort
- `day_of_week` (INT, NOT NULL): Wochentag (1=Montag, 7=Sonntag)
- `opening_time` (TIME, NOT NULL): Öffnungszeit
- `closing_time` (TIME, NOT NULL): Schließzeit
- `daily_capacity` (INT, NOT NULL): Kapazität (Anzahl Hähnchen)
- `active` (BOOLEAN, NOT NULL): Aktiv für diesen Tag?

**Indizes:**
- idx_reservation_location_id (reservation.location_id)
- idx_reservation_pickup_time (reservation.pickup_time)
- idx_reservation_status (reservation.status)
- idx_location_schedule_location_day (location_schedule.location_id, location_schedule.day_of_week)

Siehe `/docs/01-architecture/data-model.md` für Details.

---

## API-Standard

### Base URL
```
/api/v1
```

### Content Type
```
Content-Type: application/json
```

### HTTP-Status-Codes

**Erfolg:**
- `200 OK` - GET, PUT, PATCH erfolgreich
- `201 Created` - POST erfolgreich, Ressource erstellt
- `204 No Content` - DELETE erfolgreich

**Client-Fehler:**
- `400 Bad Request` - Validierungsfehler
- `401 Unauthorized` - Nicht authentifiziert
- `403 Forbidden` - Keine Berechtigung
- `404 Not Found` - Ressource nicht gefunden
- `409 Conflict` - Konflikt (z.B. Doppelbuchung)
- `422 Unprocessable Entity` - Business-Regel-Verletzung

**Server-Fehler:**
- `500 Internal Server Error` - Unerwarteter Serverfehler

### Error Response Format (RFC 7807)
```json
{
  "type": "https://api.foodtruck-booking.de/errors/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Die Eingabedaten sind ungültig",
  "instance": "/api/v1/reservations",
  "timestamp": "2026-01-19T15:30:00Z",
  "errors": [
    {
      "field": "customerEmail",
      "message": "E-Mail-Format ist ungültig"
    }
  ]
}
```

### Pagination (Spring Data Pageable)
```
GET /api/v1/reservations?page=0&size=20&sort=pickupTime,desc
```

**Response:**
```json
{
  "content": [ ... ],
  "pageable": { ... },
  "totalElements": 150,
  "totalPages": 8,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

### Authentication (aktuell)
- Spring Security Default (Basic Auth) für MVP
- Phase 2: JWT-basierte Authentication

---

## Qualitäts-Gates

### Backend
- ✅ Test-Coverage: Min. 80% (gemessen mit JaCoCo)
- ✅ Keine Compiler-Warnings
- ✅ Alle Tests müssen grün sein
- ✅ Flyway-Migrationen rückwärtskompatibel
- ✅ OpenAPI/Swagger-Dokumentation automatisch generiert
- ✅ SonarLint zeigt keine Critical/Major Issues

### Frontend
- ✅ Test-Coverage: Min. 70%
- ✅ ESLint Errors: 0
- ✅ TypeScript Errors: 0
- ✅ Build erfolgreich (Vite)
- ✅ Lighthouse Score: > 90 (Performance, Accessibility)

### Allgemein
- ✅ Code Review durchgeführt (siehe review-checklist.md)
- ✅ Definition of Done erfüllt
- ✅ Dokumentation aktualisiert

---

## Projekt-Status

### ✅ Abgeschlossen
- [x] Projekt-Setup (Java, Spring Boot, PostgreSQL)
- [x] Dokumentationsstruktur erstellt
- [x] Git-Repository auf GitHub
- [x] Docker Compose für PostgreSQL

### 🔄 In Arbeit
- [ ] Datenbank-Schema Design
- [ ] Entity-Klassen implementieren
- [ ] Frontend-Setup (React + TypeScript + Vite)

### ⏳ Geplant
- [ ] REST API Implementation (Controller, Service, Repository)
- [ ] Frontend UI Components
- [ ] Reservierungs-Workflow
- [ ] Mitarbeiter-Tablet-Interface
- [ ] Admin-Dashboard
- [ ] Security Implementation
- [ ] Testing (Backend + Frontend)
- [ ] Deployment-Vorbereitung

---

## Dokumentation

### JavaDoc (Backend)
- ✅ Öffentliche APIs (Controller-Methoden) IMMER dokumentieren
- ✅ Service-Layer Public Methods dokumentieren
- ✅ Komplexe Business-Logik erklären
- ❌ Keine überflüssigen Kommentare für selbsterklärenden Code

### TSDoc (Frontend)
- ✅ Öffentliche Components dokumentieren
- ✅ Custom Hooks dokumentieren
- ✅ Komplexe Utility Functions dokumentieren

### README
- ✅ Setup-Anleitung für Entwickler
- ✅ Architektur-Übersicht
- ✅ API-Dokumentation (Link zu Swagger)
- ✅ Deployment-Anleitung

---

## Naming Conventions

### Backend (Java)

**Klassen:**
- Controller: `*Controller` (z.B. `ReservationController`)
- Service: `*Service` (z.B. `ReservationService`)
- Repository: `*Repository` (z.B. `ReservationRepository`)
- Entity: Kein Suffix (z.B. `Reservation`)
- DTO Request: `*Request` (z.B. `CreateReservationRequest`)
- DTO Response: `*Response` (z.B. `ReservationResponse`)
- Mapper: `*Mapper` (z.B. `ReservationMapper`)
- Exception: `*Exception` (z.B. `ReservationNotFoundException`)

**Methoden:**
- CRUD: `create*`, `get*`, `update*`, `delete*`
- Finder: `findBy*`, `findAllBy*`
- Boolean: `is*`, `has*`, `can*`

**Variablen:**
- camelCase
- Aussagekräftig (keine Abkürzungen außer id, dto, etc.)

### Frontend (TypeScript/React)

**Komponenten:**
- PascalCase (z.B. `ReservationForm.tsx`)
- Suffix: `.tsx` für Components mit JSX

**Hooks:**
- Prefix: `use` (z.B. `useReservations.ts`)

**Types/Interfaces:**
- PascalCase
- Interface Prefix: `I` (optional, z.B. `IReservation`)
- Type für Props: `*Props` (z.B. `ReservationFormProps`)

**Files:**
- camelCase für Utilities (z.B. `dateUtils.ts`)
- PascalCase für Components (z.B. `ReservationCard.tsx`)

---

## Environment Variables

### Backend (application.yml / .env)
```yaml
# Database
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=foodtruck
POSTGRES_USER=foodtruck
POSTGRES_PASSWORD=foodtruck

# Server
SERVER_PORT=8080

# Security
JWT_SECRET=your-secret-key (Phase 2)
JWT_EXPIRATION=86400000

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_ORG_EXAMPLE=DEBUG
```

### Frontend (.env)
```bash
VITE_API_BASE_URL=http://localhost:8080/api/v1
VITE_APP_TITLE=Foodtruck Booking
```

---

## 🔴 WICHTIG: Bei JEDEM Code-Prompt verwenden!

Referenziere immer diese Datei zusammen mit:
- `/docs/03-prompts/forbidden-actions.md`
- `/docs/05-quality/definition-of-done.md`

Ohne diese Dateien generiert das LLM inkonsistenten Code!

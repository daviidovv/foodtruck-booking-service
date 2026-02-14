# Datenmodell - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-14

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Entity-Klassen-Implementierung (JPA Entities)
- Bei Flyway-Migrationen (Schema-Änderungen)
- Bei Repository-Implementierung (Queries)
- Bei DTO-Mapping (Entity → Response)
- Bei Datenbank-Optimierung (Indizes)

**Prompt-Beispiel:**
```
Implementiere die Reservation-Entity basierend auf:
- /docs/01-architecture/data-model.md (Schema-Definition)
- /docs/03-prompts/mandatory-context.md (JPA-Constraints)
- /docs/03-prompts/forbidden-actions.md (Verbote)
```

**Wichtig:** Nach JEDER Flyway-Migration muss diese Datei aktualisiert werden!

---

## ER-Diagramm

```
┌─────────────────────────────────────┐
│            location                 │
├─────────────────────────────────────┤
│ id (UUID, PK)                       │
│ name (VARCHAR 200)                  │
│ address (VARCHAR 500)               │
│ active (BOOLEAN)                    │
│ created_at (TIMESTAMP)              │
│ updated_at (TIMESTAMP)              │
└─────────────────────────────────────┘
            │
            │ 1
            │
            ├──────────────────────────┬──────────────────────────┐
            │                          │                          │
            │ N                        │ N                        │ N
            ▼                          ▼                          ▼
┌───────────────────────────┐  ┌───────────────────────────┐  ┌───────────────────────────┐
│    location_schedule      │  │     daily_inventory       │  │       reservation         │
├───────────────────────────┤  ├───────────────────────────┤  ├───────────────────────────┤
│ id (UUID, PK)             │  │ id (UUID, PK)             │  │ id (UUID, PK)             │
│ location_id (UUID, FK)    │  │ location_id (UUID, FK)    │  │ location_id (UUID, FK)    │
│ day_of_week (INT)         │  │ date (DATE)               │  │ confirmation_code (8)     │
│ opening_time (TIME)       │  │ total_chickens (INT)      │  │ customer_name (200)       │
│ closing_time (TIME)       │  │ created_at (TIMESTAMP)    │  │ customer_email (255,NULL) │
│ daily_capacity (INT)      │  │ updated_at (TIMESTAMP)    │  │ chicken_count (INT)       │
│ active (BOOLEAN)          │  └───────────────────────────┘  │ fries_count (INT)         │
└───────────────────────────┘                                 │ pickup_time (TS, NULL)    │
                                                              │ status (VARCHAR 30)       │
                                                              │ notes (TEXT, NULL)        │
                                                              │ created_at (TIMESTAMP)    │
                                                              │ updated_at (TIMESTAMP)    │
                                                              └───────────────────────────┘
```

**Beziehungen:**
- `location` 1:N `location_schedule` - Ein Standort hat mehrere Wochentags-Schedules
- `location` 1:N `daily_inventory` - Ein Standort hat pro Tag einen Vorratseintrag
- `location` 1:N `reservation` - Ein Standort hat mehrere Reservierungen

---

## Tabellen-Beschreibungen

### location

**Zweck:** Speichert die Foodtruck-Standorte (z.B. "Innenstadt", "Gewerbegebiet")

**Spalten:**

| Spalte | Datentyp | Constraints | Beschreibung |
|--------|----------|-------------|--------------|
| `id` | UUID | PK, NOT NULL | Primärschlüssel (automatisch generiert) |
| `name` | VARCHAR(200) | NOT NULL, UNIQUE | Standortname (z.B. "Innenstadt") |
| `address` | VARCHAR(500) | NOT NULL | Vollständige Adresse |
| `active` | BOOLEAN | NOT NULL, DEFAULT true | Soft-Delete-Flag |
| `created_at` | TIMESTAMP | NOT NULL | Erstellungszeitpunkt |
| `updated_at` | TIMESTAMP | NOT NULL | Letzte Änderung |

**Indizes:**
- `pk_location` (PRIMARY KEY): `id`
- `uk_location_name` (UNIQUE): `name` - Verhindert Duplikate

**Constraints:**
- `name` muss eindeutig sein
- Soft-Delete über `active`-Flag (nicht physisch löschen wenn Reservierungen existieren)

**JPA Entity Hinweise:**
```java
@Entity
@Table(name = "location",
       uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private Boolean active = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

---

### location_schedule

**Zweck:** Definiert wann ein Standort an welchem Wochentag geöffnet ist mit Öffnungszeiten und Kapazität

**Spalten:**

| Spalte | Datentyp | Constraints | Beschreibung |
|--------|----------|-------------|--------------|
| `id` | UUID | PK, NOT NULL | Primärschlüssel |
| `location_id` | UUID | FK, NOT NULL | Referenz auf Standort |
| `day_of_week` | INT | NOT NULL, CHECK 1-7 | Wochentag (1=Montag, 7=Sonntag, ISO 8601) |
| `opening_time` | TIME | NOT NULL | Öffnungszeit (z.B. 11:00) |
| `closing_time` | TIME | NOT NULL | Schließzeit (z.B. 20:00) |
| `daily_capacity` | INT | NOT NULL, CHECK > 0 | Max. Hähnchen pro Tag |
| `active` | BOOLEAN | NOT NULL, DEFAULT true | Schedule aktiv? |

**Indizes:**
- `pk_location_schedule` (PRIMARY KEY): `id`
- `idx_location_schedule_location_day` (UNIQUE): `location_id, day_of_week` - Ein Standort pro Tag
- `fk_location_schedule_location` (FOREIGN KEY): `location_id` → `location.id`

**Constraints:**
- `day_of_week` muss zwischen 1 und 7 liegen
- `opening_time < closing_time` - Öffnung vor Schließung
- `daily_capacity > 0` - Positive Kapazität
- Kombination `location_id + day_of_week` ist eindeutig

**Business Rules:**
- `day_of_week`: 1=Montag, 2=Dienstag, ..., 7=Sonntag (ISO 8601)
- `daily_capacity` bezieht sich auf Hähnchen (Pommes unbegrenzt im MVP)

**JPA Entity Hinweise:**
```java
@Entity
@Table(name = "location_schedule",
       uniqueConstraints = @UniqueConstraint(columnNames = {"location_id", "day_of_week"}))
public class LocationSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;  // 1-7

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "daily_capacity", nullable = false)
    private Integer dailyCapacity;

    @Column(nullable = false)
    private Boolean active = true;
}
```

---

### reservation

**Zweck:** Speichert alle Kundenreservierungen

**Spalten:**

| Spalte | Datentyp | Constraints | Beschreibung |
|--------|----------|-------------|--------------|
| `id` | UUID | PK, NOT NULL | Primärschlüssel |
| `location_id` | UUID | FK, NOT NULL | Referenz auf Standort |
| `confirmation_code` | VARCHAR(8) | NOT NULL, UNIQUE | Bestätigungscode (z.B. HUHN-K4M7) |
| `customer_name` | VARCHAR(200) | NOT NULL | Kundenname |
| `customer_email` | VARCHAR(255) | NULL | E-Mail (optional!) |
| `chicken_count` | INT | NOT NULL, CHECK >= 0 | Anzahl Hähnchen |
| `fries_count` | INT | NOT NULL, CHECK >= 0 | Anzahl Pommes |
| `reservation_date` | DATE | NOT NULL | Tag der Reservierung (nur Same-Day!) |
| `pickup_time` | TIME | NULL | Gewünschte Abholzeit (optional!) |
| `status` | VARCHAR(30) | NOT NULL | Reservierungsstatus |
| `notes` | TEXT | NULL | Zusätzliche Notizen |
| `created_at` | TIMESTAMP | NOT NULL | Erstellungszeitpunkt |
| `updated_at` | TIMESTAMP | NOT NULL | Letzte Änderung |

**Indizes:**
- `pk_reservation` (PRIMARY KEY): `id`
- `uk_reservation_confirmation_code` (UNIQUE): `confirmation_code` - Eindeutiger Bestätigungscode
- `idx_reservation_location_id` (INDEX): `location_id` - Schnelle Filterung nach Standort
- `idx_reservation_date` (INDEX): `reservation_date` - Schnelle Sortierung nach Tag
- `idx_reservation_status` (INDEX): `status` - Schnelle Filterung nach Status
- `idx_reservation_location_date` (INDEX): `location_id, reservation_date` - Tagesansicht
- `fk_reservation_location` (FOREIGN KEY): `location_id` → `location.id`

**Constraints:**
- `confirmation_code` ist UNIQUE und NOT NULL - wird automatisch generiert
- `customer_email` ist OPTIONAL (NULL erlaubt) - Design-Entscheidung!
- `pickup_time` ist OPTIONAL (NULL erlaubt) - Kunde kann kommen wann er will
- `chicken_count >= 0` und `fries_count >= 0`
- `chicken_count + fries_count > 0` - Mindestens ein Produkt
- `status` muss ein gültiger Enum-Wert sein
- `reservation_date` muss heute sein (nur Same-Day-Reservierungen)

**JPA Entity Hinweise:**
```java
@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "confirmation_code", nullable = false, unique = true, length = 8)
    private String confirmationCode;  // z.B. "HUHN-K4M7"

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_email", length = 255)  // NULL erlaubt!
    private String customerEmail;

    @Column(name = "chicken_count", nullable = false)
    private Integer chickenCount;

    @Column(name = "fries_count", nullable = false)
    private Integer friesCount;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;  // Nur heute!

    @Column(name = "pickup_time")  // NULL erlaubt!
    private LocalTime pickupTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

### daily_inventory

**Zweck:** Speichert den täglichen Hähnchen-Vorrat pro Standort, eingetragen vom Mitarbeiter

**Spalten:**

| Spalte | Datentyp | Constraints | Beschreibung |
|--------|----------|-------------|--------------|
| `id` | UUID | PK, NOT NULL | Primärschlüssel |
| `location_id` | UUID | FK, NOT NULL | Referenz auf Standort |
| `date` | DATE | NOT NULL | Datum des Vorrats |
| `total_chickens` | INT | NOT NULL, CHECK >= 0 | Vom Mitarbeiter eingetragene Anzahl |
| `created_at` | TIMESTAMP | NOT NULL | Erstellungszeitpunkt |
| `updated_at` | TIMESTAMP | NOT NULL | Letzte Änderung |

**Indizes:**
- `pk_daily_inventory` (PRIMARY KEY): `id`
- `uk_daily_inventory_location_date` (UNIQUE): `location_id, date` - Ein Eintrag pro Standort/Tag
- `fk_daily_inventory_location` (FOREIGN KEY): `location_id` → `location.id`

**Business Rules:**
- Mitarbeiter trägt morgens den Vorrat ein
- Kann jederzeit angepasst werden (Nachschub, Ausfall)
- Verfügbare Kapazität = `total_chickens - SUM(reservierte Hähnchen)`
- Wenn kein Eintrag für heute existiert → Reservierungen nicht möglich

**JPA Entity Hinweise:**
```java
@Entity
@Table(name = "daily_inventory",
       uniqueConstraints = @UniqueConstraint(columnNames = {"location_id", "date"}))
public class DailyInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "total_chickens", nullable = false)
    private Integer totalChickens;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

---

## Status-Werte (Enums)

### ReservationStatus

Erlaubte Werte für `reservation.status`:

| Wert | Beschreibung | Nächste erlaubte Status |
|------|--------------|-------------------------|
| `CONFIRMED` | Reservierung bestätigt (automatisch bei Erstellung!) | COMPLETED, NO_SHOW, CANCELLED |
| `CANCELLED` | Storniert (durch Kunde oder Mitarbeiter) | - (Endstatus) |
| `COMPLETED` | Kunde hat abgeholt | - (Endstatus) |
| `NO_SHOW` | Kunde nicht erschienen | - (Endstatus) |

**⚠️ Wichtig: Kein PENDING mehr!**
- Reservierungen werden automatisch CONFIRMED wenn Vorrat > 0
- Der Workflow ist jetzt: Kunde reserviert → Sofort CONFIRMED → Mitarbeiter schließt ab

**Status-Übergangsdiagramm:**
```
     Kunde reserviert
            │
            ▼
     ┌────────────┐
     │ CONFIRMED  │◄─── Startpunkt (automatisch)
     └─────┬──────┘
           │
     ┌─────┼─────────┐
     │     │         │
     ▼     ▼         ▼
┌──────┐ ┌──────┐ ┌─────────┐
│CANCEL│ │COMPL.│ │ NO_SHOW │
└──────┘ └──────┘ └─────────┘
```

**Java Enum:**
```java
public enum ReservationStatus {
    CONFIRMED,   // Automatisch bei Erstellung
    CANCELLED,   // Storniert
    COMPLETED,   // Abgeholt
    NO_SHOW;     // Nicht erschienen

    public boolean canTransitionTo(ReservationStatus newStatus) {
        return switch (this) {
            case CONFIRMED -> newStatus == COMPLETED || newStatus == NO_SHOW || newStatus == CANCELLED;
            case CANCELLED, COMPLETED, NO_SHOW -> false; // Endstatus
        };
    }
}
```

---

## Flyway-Migrationen

### Migrations-Übersicht

| Version | Dateiname | Beschreibung |
|---------|-----------|--------------|
| V1 | V1__create_initial_schema.sql | Initiales Schema mit 3 Tabellen |
| V2 | V2__insert_test_data.sql | Testdaten für Entwicklung |
| V3 | V3__add_inventory_and_confirmation_code.sql | daily_inventory Tabelle, confirmation_code, Reservation-Anpassungen |

### V1__create_initial_schema.sql

```sql
-- Location Table
CREATE TABLE location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_location_name UNIQUE (name)
);

-- Location Schedule Table
CREATE TABLE location_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    day_of_week INT NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    daily_capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT fk_location_schedule_location
        FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT uk_location_schedule_location_day
        UNIQUE (location_id, day_of_week),
    CONSTRAINT chk_day_of_week
        CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_daily_capacity
        CHECK (daily_capacity > 0),
    CONSTRAINT chk_opening_closing
        CHECK (opening_time < closing_time)
);

-- Reservation Table
CREATE TABLE reservation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    customer_name VARCHAR(200) NOT NULL,
    customer_email VARCHAR(255),  -- NULL erlaubt!
    chicken_count INT NOT NULL,
    fries_count INT NOT NULL,
    pickup_time TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_location
        FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT chk_chicken_count
        CHECK (chicken_count >= 0),
    CONSTRAINT chk_fries_count
        CHECK (fries_count >= 0),
    CONSTRAINT chk_min_order
        CHECK (chicken_count + fries_count > 0)
);

-- Indizes für Performance
CREATE INDEX idx_reservation_location_id ON reservation(location_id);
CREATE INDEX idx_reservation_pickup_time ON reservation(pickup_time);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_location_schedule_location_day ON location_schedule(location_id, day_of_week);
```

### V3__add_inventory_and_confirmation_code.sql

```sql
-- Daily Inventory Table (Täglicher Vorrat)
CREATE TABLE daily_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    date DATE NOT NULL,
    total_chickens INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_inventory_location
        FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT uk_daily_inventory_location_date
        UNIQUE (location_id, date),
    CONSTRAINT chk_total_chickens
        CHECK (total_chickens >= 0)
);

CREATE INDEX idx_daily_inventory_location_date ON daily_inventory(location_id, date);

-- Add confirmation_code to reservation
ALTER TABLE reservation ADD COLUMN confirmation_code VARCHAR(8);

-- Generate codes for existing reservations (for migration)
UPDATE reservation SET confirmation_code = UPPER(SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 8))
WHERE confirmation_code IS NULL;

-- Now make it NOT NULL and UNIQUE
ALTER TABLE reservation ALTER COLUMN confirmation_code SET NOT NULL;
ALTER TABLE reservation ADD CONSTRAINT uk_reservation_confirmation_code UNIQUE (confirmation_code);

-- Add reservation_date column (derived from pickup_time for existing data)
ALTER TABLE reservation ADD COLUMN reservation_date DATE;
UPDATE reservation SET reservation_date = DATE(pickup_time) WHERE reservation_date IS NULL;
ALTER TABLE reservation ALTER COLUMN reservation_date SET NOT NULL;

-- Make pickup_time optional (change from TIMESTAMP to TIME, allow NULL)
ALTER TABLE reservation ALTER COLUMN pickup_time DROP NOT NULL;
ALTER TABLE reservation ALTER COLUMN pickup_time TYPE TIME USING pickup_time::TIME;

-- Update index for new date column
DROP INDEX IF EXISTS idx_reservation_pickup_time;
CREATE INDEX idx_reservation_date ON reservation(reservation_date);
CREATE INDEX idx_reservation_location_date ON reservation(location_id, reservation_date);

-- Remove PENDING status from existing reservations (migrate to CONFIRMED)
UPDATE reservation SET status = 'CONFIRMED' WHERE status = 'PENDING';
```

---

## Beispiel-Daten

### Standorte
```sql
INSERT INTO location (id, name, address, active) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Innenstadt', 'Marktplatz 1, 12345 Musterstadt', true),
('550e8400-e29b-41d4-a716-446655440001', 'Gewerbegebiet', 'Industriestraße 42, 12345 Musterstadt', true);
```

### Wochenpläne
```sql
-- Innenstadt: Mo, Mi, Fr
INSERT INTO location_schedule (location_id, day_of_week, opening_time, closing_time, daily_capacity, active) VALUES
('550e8400-e29b-41d4-a716-446655440000', 1, '11:00', '20:00', 50, true),  -- Montag
('550e8400-e29b-41d4-a716-446655440000', 3, '11:00', '20:00', 50, true),  -- Mittwoch
('550e8400-e29b-41d4-a716-446655440000', 5, '11:00', '20:00', 60, true);  -- Freitag (mehr Kapazität)

-- Gewerbegebiet: Di, Do, Sa
INSERT INTO location_schedule (location_id, day_of_week, opening_time, closing_time, daily_capacity, active) VALUES
('550e8400-e29b-41d4-a716-446655440001', 2, '11:00', '19:00', 40, true),  -- Dienstag
('550e8400-e29b-41d4-a716-446655440001', 4, '11:00', '19:00', 40, true),  -- Donnerstag
('550e8400-e29b-41d4-a716-446655440001', 6, '10:00', '18:00', 45, true);  -- Samstag
```

### Reservierungen
```sql
INSERT INTO reservation (location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Max Mustermann', 'max@example.com', 2, 3, '2026-02-14 14:30:00', 'PENDING'),
('550e8400-e29b-41d4-a716-446655440000', 'Erika Musterfrau', NULL, 1, 2, '2026-02-14 15:00:00', 'CONFIRMED'),
('550e8400-e29b-41d4-a716-446655440001', 'Hans Müller', 'hans@firma.de', 5, 5, '2026-02-15 12:00:00', 'PENDING');
```

---

## Wichtige Queries

### Verfügbare Kapazität berechnen (NEU: basiert auf daily_inventory)
```sql
SELECT
    di.total_chickens - COALESCE(SUM(r.chicken_count), 0) AS available_chickens
FROM daily_inventory di
LEFT JOIN reservation r ON r.location_id = di.location_id
    AND r.reservation_date = di.date
    AND r.status = 'CONFIRMED'  -- Nur CONFIRMED zählt (kein PENDING mehr)
WHERE di.location_id = :locationId
    AND di.date = :date
GROUP BY di.total_chickens;
```

### Reservierungen eines Standorts für heute
```sql
SELECT r.*
FROM reservation r
WHERE r.location_id = :locationId
    AND r.reservation_date = CURRENT_DATE
ORDER BY r.pickup_time ASC NULLS LAST;  -- NULL pickup_time am Ende
```

### Reservierung per Bestätigungscode finden
```sql
SELECT r.*
FROM reservation r
WHERE r.confirmation_code = :code;
```

### Statistik: Auslastung pro Standort
```sql
SELECT
    l.name AS location_name,
    COUNT(r.id) AS total_reservations,
    SUM(CASE WHEN r.status = 'COMPLETED' THEN 1 ELSE 0 END) AS completed,
    SUM(CASE WHEN r.status = 'NO_SHOW' THEN 1 ELSE 0 END) AS no_shows,
    ROUND(100.0 * SUM(CASE WHEN r.status = 'NO_SHOW' THEN 1 ELSE 0 END) / NULLIF(COUNT(*), 0), 1) AS no_show_rate
FROM location l
LEFT JOIN reservation r ON r.location_id = l.id
    AND r.pickup_time BETWEEN :dateFrom AND :dateTo
GROUP BY l.id, l.name;
```

---

## Geplante Erweiterungen (Phase 2+)

### Tabelle: user (Mitarbeiter/Admin)
```sql
-- Geplant für Phase 2 (JWT-Authentication)
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,  -- STAFF, ADMIN
    location_id UUID,  -- NULL für Admin (alle Standorte)
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (location_id) REFERENCES location(id)
);
```

### Tabelle: daily_capacity_override
```sql
-- Geplant für Phase 2 (tagesspezifische Kapazität)
CREATE TABLE daily_capacity_override (
    id UUID PRIMARY KEY,
    location_id UUID NOT NULL,
    override_date DATE NOT NULL,
    capacity INT NOT NULL,
    reason VARCHAR(255),
    FOREIGN KEY (location_id) REFERENCES location(id),
    UNIQUE (location_id, override_date)
);
```

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initiales Schema dokumentiert |
| 2026-02-14 | 2.0 | daily_inventory Tabelle, confirmation_code, pickup_time optional, PENDING entfernt |

---

## 🔴 Wichtig für LLM

**Diese Datei MUSS nach JEDER Flyway-Migration aktualisiert werden!**

**Verwenden zusammen mit:**
- `/docs/03-prompts/mandatory-context.md` (JPA-Constraints, Coding-Standards)
- `/docs/03-prompts/forbidden-actions.md` (Schema-Verbote)
- `/docs/02-requirements/api-contracts.md` (DTO-Mapping)

**Niemals:**
- Bestehende Flyway-Migrationen ändern (immutable!)
- `customer_email` auf NOT NULL setzen (Design-Entscheidung!)
- ID-Typ von UUID auf Long/Integer ändern
- Indizes ohne Begründung löschen

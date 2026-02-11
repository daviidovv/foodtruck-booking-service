# Datenmodell - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-11

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
            ├──────────────────────────┐
            │                          │
            │ N                        │ N
            ▼                          ▼
┌─────────────────────────────────────┐    ┌─────────────────────────────────────┐
│          location_schedule          │    │           reservation               │
├─────────────────────────────────────┤    ├─────────────────────────────────────┤
│ id (UUID, PK)                       │    │ id (UUID, PK)                       │
│ location_id (UUID, FK)              │    │ location_id (UUID, FK)              │
│ day_of_week (INT)                   │    │ customer_name (VARCHAR 200)         │
│ opening_time (TIME)                 │    │ customer_email (VARCHAR 255, NULL)  │
│ closing_time (TIME)                 │    │ chicken_count (INT)                 │
│ daily_capacity (INT)                │    │ fries_count (INT)                   │
│ active (BOOLEAN)                    │    │ pickup_time (TIMESTAMP)             │
└─────────────────────────────────────┘    │ status (VARCHAR 30)                 │
                                           │ notes (TEXT, NULL)                  │
                                           │ created_at (TIMESTAMP)              │
                                           │ updated_at (TIMESTAMP)              │
                                           └─────────────────────────────────────┘
```

**Beziehungen:**
- `location` 1:N `location_schedule` - Ein Standort hat mehrere Wochentags-Schedules
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
| `customer_name` | VARCHAR(200) | NOT NULL | Kundenname |
| `customer_email` | VARCHAR(255) | NULL | E-Mail (optional!) |
| `chicken_count` | INT | NOT NULL, CHECK >= 0 | Anzahl Hähnchen |
| `fries_count` | INT | NOT NULL, CHECK >= 0 | Anzahl Pommes |
| `pickup_time` | TIMESTAMP | NOT NULL | Gewünschte Abholzeit |
| `status` | VARCHAR(30) | NOT NULL | Reservierungsstatus |
| `notes` | TEXT | NULL | Zusätzliche Notizen |
| `created_at` | TIMESTAMP | NOT NULL | Erstellungszeitpunkt |
| `updated_at` | TIMESTAMP | NOT NULL | Letzte Änderung |

**Indizes:**
- `pk_reservation` (PRIMARY KEY): `id`
- `idx_reservation_location_id` (INDEX): `location_id` - Schnelle Filterung nach Standort
- `idx_reservation_pickup_time` (INDEX): `pickup_time` - Schnelle Sortierung nach Abholzeit
- `idx_reservation_status` (INDEX): `status` - Schnelle Filterung nach Status
- `idx_reservation_location_date` (INDEX): `location_id, DATE(pickup_time)` - Tagesansicht
- `fk_reservation_location` (FOREIGN KEY): `location_id` → `location.id`

**Constraints:**
- `customer_email` ist OPTIONAL (NULL erlaubt) - Design-Entscheidung!
- `chicken_count >= 0` und `fries_count >= 0`
- `chicken_count + fries_count > 0` - Mindestens ein Produkt
- `status` muss ein gültiger Enum-Wert sein

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

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_email", length = 255)  // NULL erlaubt!
    private String customerEmail;

    @Column(name = "chicken_count", nullable = false)
    private Integer chickenCount;

    @Column(name = "fries_count", nullable = false)
    private Integer friesCount;

    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;

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

## Status-Werte (Enums)

### ReservationStatus

Erlaubte Werte für `reservation.status`:

| Wert | Beschreibung | Nächste erlaubte Status |
|------|--------------|-------------------------|
| `PENDING` | Reservierung eingegangen, wartet auf Bestätigung | CONFIRMED, CANCELLED |
| `CONFIRMED` | Von Mitarbeiter bestätigt, Produkte reserviert | COMPLETED, NO_SHOW, CANCELLED |
| `CANCELLED` | Storniert (durch Mitarbeiter) | - (Endstatus) |
| `COMPLETED` | Kunde hat abgeholt | - (Endstatus) |
| `NO_SHOW` | Kunde nicht erschienen | - (Endstatus) |

**Status-Übergangsdiagramm:**
```
                    ┌──────────────┐
                    │   PENDING    │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            │            ▼
     ┌────────────┐        │     ┌────────────┐
     │ CANCELLED  │◄───────┼─────│ CONFIRMED  │
     └────────────┘        │     └─────┬──────┘
                           │           │
                           │     ┌─────┴─────┐
                           │     │           │
                           │     ▼           ▼
                           │ ┌──────────┐ ┌─────────┐
                           │ │COMPLETED │ │ NO_SHOW │
                           │ └──────────┘ └─────────┘
```

**Java Enum:**
```java
public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    public boolean canTransitionTo(ReservationStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
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
| V1 | V1__create_initial_schema.sql | Initiales Schema mit allen 3 Tabellen |

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

### Verfügbare Kapazität berechnen
```sql
SELECT
    ls.daily_capacity - COALESCE(SUM(r.chicken_count), 0) AS available_capacity
FROM location_schedule ls
LEFT JOIN reservation r ON r.location_id = ls.location_id
    AND DATE(r.pickup_time) = :date
    AND r.status IN ('PENDING', 'CONFIRMED')
WHERE ls.location_id = :locationId
    AND ls.day_of_week = EXTRACT(ISODOW FROM :date::DATE)
    AND ls.active = true
GROUP BY ls.daily_capacity;
```

### Reservierungen eines Standorts für heute
```sql
SELECT r.*
FROM reservation r
WHERE r.location_id = :locationId
    AND DATE(r.pickup_time) = CURRENT_DATE
ORDER BY r.pickup_time ASC;
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

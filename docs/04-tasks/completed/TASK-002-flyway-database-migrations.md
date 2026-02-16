# TASK-002: Flyway Database Migrations

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [ ] Review
- [x] Abgeschlossen

## Kontext

Aktueller Stand:
- ✅ Dokumentation vollständig (TASK-001 abgeschlossen)
- ✅ Datenmodell in `/docs/01-architecture/data-model.md` definiert
- ✅ Tech-Stack dokumentiert (Flyway, PostgreSQL 16)
- ✅ Flyway-Migrationen erstellt (V1, V2)
- ✅ Datenbank-Schema erfolgreich migriert

**Problem:**
Ohne Datenbank-Schema können keine Entity-Klassen getestet werden und kein Backend-Code funktioniert. Die Flyway-Migrationen sind das Fundament für alle weiteren Backend-Tasks.

## Ziel

Erstelle die initialen Flyway-Migrationen basierend auf dem dokumentierten Datenmodell:
1. Alle 3 Tabellen (location, location_schedule, reservation) erstellt
2. Alle Constraints und Indizes definiert
3. Testdaten für Entwicklung eingefügt
4. Migration erfolgreich ausführbar

**Messbar:** `./mvnw flyway:migrate` läuft fehlerfrei, Schema ist in PostgreSQL sichtbar.

## Akzeptanzkriterien

### 🔴 Kritisch (MUSS)
- [x] **V1__create_initial_schema.sql** - Alle Tabellen (location, location_schedule, reservation) mit Constraints und Indizes
- [x] Alle Migrationen sind idempotent und rückwärtskompatibel
- [x] `./mvnw spring-boot:run` startet ohne Fehler

### ⚠️ Wichtig (SOLLTE)
- [x] **V2__insert_test_data.sql** - Beispieldaten für 2 Standorte, Schedules, 7 Reservierungen
- [x] Migrationen folgen Flyway Best Practices (Versionierung, Naming)
- [x] Schema entspricht exakt `/docs/01-architecture/data-model.md`

### ✅ Nice-to-have
- [ ] Repeatable Migration für Testdaten (R__test_data.sql)
- [x] Dokumentation in SQL-Dateien (COMMENT ON)

## Constraints

### Technische Einschränkungen
- PostgreSQL 16 Syntax verwenden
- UUID als Primary Key (gen_random_uuid())
- Flyway Naming Convention: `V{version}__{description}.sql`
- Keine Breaking Changes zu dokumentiertem Schema

### Abhängigkeiten
- **TASK-001** ✅ (Datenmodell dokumentiert)
- Docker Compose mit PostgreSQL muss laufen

## Betroffene Komponenten

**Neue/Geänderte Dateien:**
- `src/main/resources/db/migration/V1__create_initial_schema.sql` (ersetzt alte V1__init.sql)
- `src/main/resources/db/migration/V2__insert_test_data.sql`
- `src/main/resources/application.properties` (erweitert mit DB-Konfiguration)

**Konfiguration geprüft:**
- `docker-compose.yml` (PostgreSQL 16) ✅
- `pom.xml` (Flyway Dependency) ✅

## Verknüpfungen

- Datenmodell: `/docs/01-architecture/data-model.md`
- Tech-Stack: `/docs/01-architecture/tech-stack.md`
- Mandatory Context: `/docs/03-prompts/mandatory-context.md`

## Detaillierte Aufgabenliste

### 1. Projektstruktur prüfen (5 Min)
- [x] Verzeichnis `src/main/resources/db/migration/` existiert
- [x] Flyway-Dependency in `pom.xml` vorhanden
- [x] `application.properties` hat Flyway-Konfiguration
- [x] Docker Compose PostgreSQL läuft

### 2. V1__create_location_table.sql
```sql
CREATE TABLE location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_location_name UNIQUE (name)
);
```

### 3. V2__create_location_schedule_table.sql
```sql
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
```

### 4. V3__create_reservation_table.sql
```sql
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
```

### 5. V4__create_indexes.sql
```sql
-- Performance-Indizes
CREATE INDEX idx_reservation_location_id ON reservation(location_id);
CREATE INDEX idx_reservation_pickup_time ON reservation(pickup_time);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_location_schedule_location_day ON location_schedule(location_id, day_of_week);
```

### 6. V5__insert_test_data.sql
- 2 Standorte: "Innenstadt", "Gewerbegebiet"
- Schedules für beide Standorte (verschiedene Wochentage)
- 3-5 Beispiel-Reservierungen in verschiedenen Status

### 7. Migration testen
```bash
# PostgreSQL starten
docker-compose up -d postgres

# Migration ausführen
./mvnw flyway:migrate

# Oder via Spring Boot
./mvnw spring-boot:run

# Schema prüfen
docker exec -it postgres psql -U foodtruck -d foodtruck -c "\dt"
```

## Offene Fragen

Keine - Schema ist vollständig in data-model.md dokumentiert.

## Ergebnis

**Implementierte Änderungen**:
- `V1__create_initial_schema.sql`: Konsolidiertes Schema mit allen 3 Tabellen, Constraints, Indizes und SQL-Kommentaren
- `V2__insert_test_data.sql`: 2 Standorte (Innenstadt, Gewerbegebiet), 6 Schedules, 7 Reservierungen in verschiedenen Status
- `application.properties`: Vollständige DB-Konfiguration (PostgreSQL, JPA validate, Flyway enabled)

**Entscheidungen**:
- Schema in einer Migration statt 4 separaten (einfacher, keine FK-Probleme)
- Testdaten separat für einfaches Entfernen in Produktion
- `ddl-auto=validate` statt `update` (Flyway ist Single-Source-of-Truth)

**Commits**:
- Noch nicht committed (bereit für Review)

**Learnings**:
- Alte V1__init.sql mit falschem "booking"-Schema musste ersetzt werden
- Konsolidierte Migrationen sind wartbarer als viele kleine

## Follow-up Tasks

- [ ] TASK-003: Entity-Klassen implementieren
- [ ] TASK-004: Repository-Layer implementieren

# TASK-003: Entity-Klassen implementieren

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [ ] Review
- [x] Abgeschlossen

## Kontext

Aktueller Stand:
- ✅ TASK-001: Dokumentation vollständig
- ✅ TASK-002: Flyway-Migrationen erstellt und getestet
- ✅ Datenbank-Schema existiert (location, location_schedule, reservation)
- ⚠️ **Keine JPA Entity-Klassen existieren**

**Problem:**
Ohne Entity-Klassen kann der Service-Layer nicht implementiert werden. Die Entities sind die Grundlage für Repository und Service.

## Ziel

Implementiere JPA Entity-Klassen basierend auf dem dokumentierten Datenmodell:
1. `Location` Entity
2. `LocationSchedule` Entity
3. `Reservation` Entity
4. `ReservationStatus` Enum
5. JPA Auditing Konfiguration

**Messbar:** `./mvnw compile` läuft fehlerfrei, Entities matchen DB-Schema.

## Akzeptanzkriterien

### 🔴 Kritisch (MUSS)
- [x] `ReservationStatus` Enum mit allen Status-Werten und Transition-Logic
- [x] `Location` Entity mit allen Spalten und Constraints
- [x] `LocationSchedule` Entity mit FK zu Location
- [x] `Reservation` Entity mit FK zu Location und Status-Enum
- [x] JPA Auditing aktiviert (@EnableJpaAuditing)
- [x] `./mvnw compile` erfolgreich

### ⚠️ Wichtig (SOLLTE)
- [x] Lombok-Annotations (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
- [x] @CreatedDate und @LastModifiedDate für Audit-Felder
- [x] Entities entsprechen exakt `/docs/01-architecture/data-model.md`
- [x] Keine Business-Logik in Entities (außer Status-Transition)

### ✅ Nice-to-have
- [x] JavaDoc für öffentliche APIs

## Constraints

### Technische Einschränkungen
- Package: `org.example.foodtruckbookingservice.entity`
- UUID als Primary Key (GenerationType.UUID)
- Lombok für Boilerplate
- FetchType.LAZY für @ManyToOne

### Abhängigkeiten
- **TASK-002** ✅ (DB-Schema existiert)

## Betroffene Komponenten

**Neue Dateien:**
- `src/main/java/org/example/foodtruckbookingservice/entity/ReservationStatus.java`
- `src/main/java/org/example/foodtruckbookingservice/entity/Location.java`
- `src/main/java/org/example/foodtruckbookingservice/entity/LocationSchedule.java`
- `src/main/java/org/example/foodtruckbookingservice/entity/Reservation.java`
- `src/main/java/org/example/foodtruckbookingservice/config/JpaConfig.java`

## Verknüpfungen

- Datenmodell: `/docs/01-architecture/data-model.md`
- Mandatory Context: `/docs/03-prompts/mandatory-context.md`
- Forbidden Actions: `/docs/03-prompts/forbidden-actions.md`

## Ergebnis

**Implementierte Änderungen**:
- `ReservationStatus.java`: Enum mit PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW + canTransitionTo() + isTerminal()
- `Location.java`: Entity mit @OneToMany zu schedules und reservations
- `LocationSchedule.java`: Entity mit @ManyToOne(LAZY) zu Location, dayOfWeek (1-7 ISO 8601)
- `Reservation.java`: Entity mit @ManyToOne(LAZY) zu Location, @Enumerated(STRING) Status
- `JpaConfig.java`: @EnableJpaAuditing für @CreatedDate/@LastModifiedDate

**Verifiziert**:
- `./mvnw compile` erfolgreich
- `./mvnw spring-boot:run` startet in 3.2s ohne Fehler
- Hibernate validiert Schema erfolgreich (ddl-auto=validate)

**Commits**:
- Noch nicht committed (bereit für Commit)

## Follow-up Tasks

- [ ] TASK-004: Repository-Layer implementieren
- [ ] TASK-005: Service-Layer implementieren

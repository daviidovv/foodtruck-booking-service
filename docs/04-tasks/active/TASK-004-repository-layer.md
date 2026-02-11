# TASK-004: Repository-Layer implementieren

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [ ] Review
- [x] Abgeschlossen

## Kontext

Aktueller Stand:
- ✅ TASK-001: Dokumentation vollständig
- ✅ TASK-002: Flyway-Migrationen erstellt
- ✅ TASK-003: Entity-Klassen implementiert
- ⚠️ **Keine Repository-Interfaces existieren**

**Problem:**
Ohne Repositories kann der Service-Layer nicht auf die Datenbank zugreifen.

## Ziel

Implementiere Spring Data JPA Repositories für alle Entities:
1. `LocationRepository`
2. `LocationScheduleRepository`
3. `ReservationRepository`

Mit Custom Query Methods basierend auf `/docs/01-architecture/data-model.md`.

**Messbar:** `./mvnw compile` erfolgreich, Repositories werden beim Start gefunden.

## Akzeptanzkriterien

### 🔴 Kritisch (MUSS)
- [x] `LocationRepository` mit findByActiveTrue(), findByName()
- [x] `LocationScheduleRepository` mit findByLocationId(), findByLocationIdAndDayOfWeek()
- [x] `ReservationRepository` mit Queries für Tagesansicht und Kapazitätsberechnung
- [x] `./mvnw compile` erfolgreich

### ⚠️ Wichtig (SOLLTE)
- [x] Derived Query Methods wo möglich
- [x] @Query nur für komplexe Queries (sumChickenCount)
- [x] Pageable Support für Listen-Endpoints

## Betroffene Komponenten

**Neue Dateien:**
- `src/main/java/org/example/foodtruckbookingservice/repository/LocationRepository.java`
- `src/main/java/org/example/foodtruckbookingservice/repository/LocationScheduleRepository.java`
- `src/main/java/org/example/foodtruckbookingservice/repository/ReservationRepository.java`

## Verknüpfungen

- Datenmodell: `/docs/01-architecture/data-model.md` (Wichtige Queries)
- API Contracts: `/docs/02-requirements/api-contracts.md`
- Entities: TASK-003

## Ergebnis

**Implementierte Repositories:**
- `LocationRepository`: findByActiveTrue(), findByName(), existsByName()
- `LocationScheduleRepository`: findByLocationId(), findByLocationIdAndDayOfWeek(), findByLocationIdAndActiveTrue()
- `ReservationRepository`:
  - Tagesansicht: findByLocationIdAndPickupTimeBetweenOrderByPickupTimeAsc()
  - Kapazität: sumChickenCountByLocationAndDateAndStatus() mit @Query
  - Statistik: countByLocationIdAndPickupTimeBetweenAndStatus()
  - Pagination: findByLocationId(Pageable), findAll(Pageable)

**Verifiziert:**
- Spring Data JPA findet 3 Repository Interfaces
- Anwendung startet in 3.8s ohne Fehler

## Follow-up Tasks

- [ ] TASK-005: Service-Layer implementieren
- [ ] TASK-006: REST-Controller implementieren

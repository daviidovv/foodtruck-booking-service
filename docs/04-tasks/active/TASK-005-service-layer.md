# TASK-005: Service-Layer implementieren

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [ ] Review
- [x] Abgeschlossen

## Kontext

- ✅ TASK-001-004: Entity, Repository vollständig
- ⚠️ **Keine Services, DTOs, Exceptions existieren**

## Ziel

Implementiere den vollständigen Service-Layer:
1. Custom Exceptions
2. Request/Response DTOs
3. Mapper (Entity ↔ DTO)
4. Services mit Business Logic

## Akzeptanzkriterien

- [ ] Custom Exceptions (LocationNotFound, CapacityExceeded, etc.)
- [ ] DTOs für alle API-Endpoints
- [ ] Mapper für Entity ↔ DTO
- [ ] LocationService, ReservationService
- [ ] Kapazitätsprüfung und Status-Transitionen
- [ ] `./mvnw compile` erfolgreich

## Betroffene Komponenten

**Neue Packages:**
- `exception/` - Custom Exceptions
- `dto/request/` - Request DTOs
- `dto/response/` - Response DTOs
- `mapper/` - Entity ↔ DTO Mapper
- `service/` - Business Logic

## Ergebnis

**Implementierte Komponenten:**

Exceptions (6):
- LocationNotFoundException, ReservationNotFoundException
- CapacityExceededException, InvalidStatusTransitionException
- BusinessRuleViolationException, LocationNameAlreadyExistsException

Request DTOs (4):
- CreateReservationRequest, UpdateStatusRequest
- CreateLocationRequest, CreateScheduleRequest

Response DTOs (6):
- LocationResponse, ScheduleResponse, LocationWithScheduleResponse
- AvailabilityResponse, ReservationResponse, CapacityResponse

Mapper (2):
- LocationMapper, ReservationMapper

Services (2):
- LocationService (CRUD, Schedule, Availability)
- ReservationService (CRUD, Status-Transition, Kapazitätsprüfung)

**Verifiziert:** Anwendung startet erfolgreich (3.8s)

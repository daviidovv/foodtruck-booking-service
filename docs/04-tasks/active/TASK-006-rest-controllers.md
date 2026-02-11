# TASK-006: REST-Controller implementieren

## Status
- [ ] Neu
- [ ] In Bearbeitung
- [ ] Review
- [x] Abgeschlossen

## Ziel

Implementiere REST-Controller basierend auf `/docs/02-requirements/api-contracts.md`:
1. GlobalExceptionHandler (RFC 7807)
2. LocationController (Public API)
3. ReservationController (Public + Staff API)
4. Security Configuration

## Akzeptanzkriterien

- [ ] GlobalExceptionHandler mit RFC 7807 Error Format
- [ ] LocationController: GET /locations, GET /locations/{id}/schedule, GET /locations/{id}/availability
- [ ] ReservationController: POST /reservations, GET /reservations/{id}, PATCH status
- [ ] Security: Public endpoints ohne Auth
- [ ] API-Tests via curl/httpie erfolgreich

## Ergebnis

**Implementierte Komponenten:**
- `GlobalExceptionHandler` - RFC 7807 ProblemDetail für alle Exceptions
- `LocationController` - Public: GET locations, schedule, availability; Admin: CRUD
- `ReservationController` - Public: POST, GET; Staff: daily list, status update, capacity
- `SecurityConfig` - Public endpoints permitAll, Staff/Admin mit Basic Auth

**Getestete Endpoints:**
- `GET /api/v1/locations` ✅
- `POST /api/v1/reservations` ✅
- `GET /api/v1/locations/{id}/availability` ✅

**In-Memory Users (MVP):**
- staff / staff123 (ROLE_STAFF)
- admin / admin123 (ROLE_ADMIN)

# API Contracts - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-14

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Controller-Implementierung (Request/Response DTOs)
- Bei Service-Layer-Implementierung (Validierungslogik)
- Bei Frontend-Integration (API-Calls)
- Bei API-Tests (Endpoint-Tests, Contract-Tests)
- Bei OpenAPI/Swagger-Generierung

**Prompt-Beispiel:**
```
Implementiere den ReservationController basierend auf:
- /docs/02-requirements/api-contracts.md (API-Spezifikation)
- /docs/03-prompts/mandatory-context.md (Tech-Stack)
- /docs/03-prompts/forbidden-actions.md (Verbote)
```

---

## Base URL

```
/api/v1
```

**Content-Type:** `application/json`

---

## Authentifizierung

### MVP (Phase 1)
- **Methode:** HTTP Basic Authentication (Spring Security)
- **Rollen:** `ROLE_CUSTOMER` (implizit/anonym), `ROLE_STAFF`, `ROLE_ADMIN`
- **Public Endpoints:** Keine Authentifizierung erforderlich
- **Protected Endpoints:** Erfordern Basic Auth Header

### Phase 2 (geplant)
- JWT-basierte Authentifizierung

---

## Public API (Kunden - keine Authentifizierung)

### GET /api/v1/locations

Gibt alle aktiven Standorte zurück.

**Query Parameters:**
- `active` (boolean, optional, default: true): Nur aktive Standorte

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Innenstadt",
      "address": "Marktplatz 1, 12345 Musterstadt",
      "active": true
    },
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "name": "Gewerbegebiet",
      "address": "Industriestraße 42, 12345 Musterstadt",
      "active": true
    }
  ]
}
```

**Errors:**
- `500 Internal Server Error`: Serverfehler

---

### GET /api/v1/locations/{locationId}/schedule

Gibt den Wochenplan eines Standorts zurück.

**Path Parameters:**
- `locationId` (UUID, required): ID des Standorts

**Response (200 OK):**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "schedules": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440000",
      "dayOfWeek": 1,
      "dayName": "Montag",
      "openingTime": "11:00",
      "closingTime": "20:00",
      "dailyCapacity": 50,
      "active": true
    },
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "dayOfWeek": 3,
      "dayName": "Mittwoch",
      "openingTime": "11:00",
      "closingTime": "20:00",
      "dailyCapacity": 50,
      "active": true
    }
  ]
}
```

**Errors:**
- `404 Not Found`: Standort nicht gefunden

**Error Response (404):**
```json
{
  "type": "https://api.foodtruck-booking.de/errors/not-found",
  "title": "Location Not Found",
  "status": 404,
  "detail": "Standort mit ID 550e8400-e29b-41d4-a716-446655440000 nicht gefunden",
  "instance": "/api/v1/locations/550e8400-e29b-41d4-a716-446655440000/schedule",
  "timestamp": "2026-02-11T10:30:00Z"
}
```

---

### GET /api/v1/locations/{locationId}/availability

Prüft die Live-Verfügbarkeit eines Standorts für HEUTE (nur Same-Day!).

**Path Parameters:**
- `locationId` (UUID, required): ID des Standorts

**Query Parameters:**
- `date` (string, optional, default: heute, format: YYYY-MM-DD): Datum (nur heute erlaubt!)

**Response (200 OK):**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "dayOfWeek": 6,
  "dayName": "Samstag",
  "openingTime": "11:00",
  "closingTime": "20:00",
  "inventorySet": true,
  "totalChickens": 50,
  "reservedChickens": 35,
  "availableChickens": 15,
  "isOpen": true,
  "availabilityStatus": "AVAILABLE"
}
```

**Falls kein Vorrat eingetragen:**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "inventorySet": false,
  "availableChickens": 0,
  "isOpen": true,
  "availabilityStatus": "NOT_AVAILABLE",
  "message": "Vorrat wurde noch nicht eingetragen"
}
```

**availabilityStatus Werte:**
- `AVAILABLE`: Kapazität vorhanden (>30%)
- `LIMITED`: Begrenzte Kapazität (10-30%)
- `ALMOST_FULL`: Fast ausgebucht (<10%)
- `SOLD_OUT`: Ausverkauft (0 verfügbar)
- `NOT_AVAILABLE`: Kein Vorrat eingetragen
- `CLOSED`: Standort geschlossen an diesem Tag

**Errors:**
- `400 Bad Request`: Datum ist nicht heute (nur Same-Day erlaubt)
- `404 Not Found`: Standort nicht gefunden

---

### POST /api/v1/reservations

Erstellt eine neue Reservierung für HEUTE. Wird automatisch bestätigt wenn Vorrat verfügbar.

**Request Body:**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "customerName": "Max Mustermann",
  "customerEmail": "max@example.com",
  "chickenCount": 2,
  "friesCount": 3,
  "pickupTime": "14:30",
  "notes": "Bitte extra knusprig"
}
```

**Validierungsregeln:**
- `locationId`: Required, muss existieren und aktiv sein
- `customerName`: Required, 1-200 Zeichen
- `customerEmail`: Optional, gültiges E-Mail-Format, max. 255 Zeichen
- `chickenCount`: Required, 0-50, chickenCount + friesCount > 0
- `friesCount`: Required, 0-50, chickenCount + friesCount > 0
- `pickupTime`: **Optional**, Format HH:mm, falls angegeben innerhalb Öffnungszeiten
- `notes`: Optional, max. 500 Zeichen
- **Nur Same-Day**: Reservierung ist immer für heute

**Response (201 Created):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "confirmationCode": "HUHN-K4M7",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "customerName": "Max Mustermann",
  "customerEmail": "max@example.com",
  "chickenCount": 2,
  "friesCount": 3,
  "reservationDate": "2026-02-14",
  "pickupTime": "14:30",
  "status": "CONFIRMED",
  "notes": "Bitte extra knusprig",
  "createdAt": "2026-02-14T10:30:00Z",
  "message": "Reservierung erfolgreich! Bitte notieren Sie Ihren Bestätigungscode: HUHN-K4M7"
}
```

**Wichtig:**
- `status` ist immer `CONFIRMED` (automatische Bestätigung!)
- `confirmationCode` wird automatisch generiert (8 Zeichen)
- `pickupTime` kann `null` sein wenn nicht angegeben

**Errors:**
- `400 Bad Request`: Validierungsfehler
- `404 Not Found`: Standort nicht gefunden
- `409 Conflict`: Nicht genug Vorrat verfügbar
- `422 Unprocessable Entity`: Kein Vorrat eingetragen oder außerhalb Öffnungszeiten

**Error Response (400 - Validierung):**
```json
{
  "type": "https://api.foodtruck-booking.de/errors/validation-error",
  "title": "Validation Failed",
  "status": 400,
  "detail": "Die Eingabedaten sind ungültig",
  "instance": "/api/v1/reservations",
  "timestamp": "2026-02-11T10:30:00Z",
  "errors": [
    {
      "field": "customerName",
      "message": "Kundenname ist erforderlich"
    },
    {
      "field": "chickenCount",
      "message": "Mindestens ein Produkt muss ausgewählt sein"
    }
  ]
}
```

**Error Response (409 - Kapazität):**
```json
{
  "type": "https://api.foodtruck-booking.de/errors/capacity-exceeded",
  "title": "Capacity Exceeded",
  "status": 409,
  "detail": "Kapazität ausgeschöpft. Nur noch 5 Hähnchen verfügbar.",
  "instance": "/api/v1/reservations",
  "timestamp": "2026-02-11T10:30:00Z",
  "availableCapacity": 5
}
```

**Error Response (422 - Business Rule):**
```json
{
  "type": "https://api.foodtruck-booking.de/errors/business-rule-violation",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "Abholzeit liegt außerhalb der Öffnungszeiten (11:00 - 20:00)",
  "instance": "/api/v1/reservations",
  "timestamp": "2026-02-11T10:30:00Z"
}
```

---

### GET /api/v1/reservations/{reservationId}

Ruft eine Reservierung anhand der ID ab.

**Path Parameters:**
- `reservationId` (UUID, required): ID der Reservierung

**Response (200 OK):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "locationAddress": "Marktplatz 1, 12345 Musterstadt",
  "customerName": "Max Mustermann",
  "customerEmail": "max@example.com",
  "chickenCount": 2,
  "friesCount": 3,
  "pickupTime": "2026-02-14T14:30:00",
  "status": "PENDING",
  "notes": "Bitte extra knusprig",
  "createdAt": "2026-02-11T10:30:00Z",
  "updatedAt": "2026-02-11T10:30:00Z"
}
```

**Errors:**
- `404 Not Found`: Reservierung nicht gefunden

---

### GET /api/v1/reservations/code/{confirmationCode}

Ruft eine Reservierung anhand des Bestätigungscodes ab.

**Path Parameters:**
- `confirmationCode` (string, required): Bestätigungscode (z.B. "HUHN-K4M7")

**Response (200 OK):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "confirmationCode": "HUHN-K4M7",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "locationAddress": "Marktplatz 1, 12345 Musterstadt",
  "customerName": "Max Mustermann",
  "chickenCount": 2,
  "friesCount": 3,
  "reservationDate": "2026-02-14",
  "pickupTime": "14:30",
  "status": "CONFIRMED",
  "notes": "Bitte extra knusprig",
  "createdAt": "2026-02-14T10:30:00Z",
  "canCancel": true
}
```

**Hinweis:**
- `canCancel` ist `true` wenn Status = CONFIRMED (nicht abgeholt/storniert)
- E-Mail wird aus Datenschutzgründen nicht zurückgegeben

**Errors:**
- `404 Not Found`: Reservierung mit diesem Code nicht gefunden

---

### DELETE /api/v1/reservations/code/{confirmationCode}

Storniert eine Reservierung anhand des Bestätigungscodes.

**Path Parameters:**
- `confirmationCode` (string, required): Bestätigungscode (z.B. "HUHN-K4M7")

**Response (200 OK):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "confirmationCode": "HUHN-K4M7",
  "status": "CANCELLED",
  "previousStatus": "CONFIRMED",
  "message": "Reservierung wurde erfolgreich storniert. Die Hähnchen sind wieder verfügbar."
}
```

**Business Rules:**
- Stornierung ist jederzeit möglich (keine Frist)
- Nur CONFIRMED → CANCELLED erlaubt
- Bei Stornierung wird Vorrat automatisch freigegeben
- Bereits abgeschlossene/stornierte Reservierungen können nicht erneut storniert werden

**Errors:**
- `400 Bad Request`: Reservierung kann nicht storniert werden (falscher Status)
- `404 Not Found`: Reservierung mit diesem Code nicht gefunden

---

## Protected API (Mitarbeiter - ROLE_STAFF)

### POST /api/v1/auth/login

Authentifiziert einen Mitarbeiter.

**Request Body:**
```json
{
  "username": "mitarbeiter1",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "username": "mitarbeiter1",
  "role": "STAFF",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "message": "Login erfolgreich"
}
```

**Errors:**
- `401 Unauthorized`: Ungültige Anmeldedaten

---

### GET /api/v1/staff/reservations

Gibt alle Reservierungen des Tages für den Standort des Mitarbeiters zurück.

**Query Parameters:**
- `locationId` (UUID, required): ID des Standorts
- `date` (string, optional, default: heute, format: YYYY-MM-DD): Datum
- `status` (string, optional): Filter nach Status (CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- `search` (string, optional): Suche nach Kundenname oder Bestätigungscode
- `page` (int, optional, default: 0): Seitennummer
- `size` (int, optional, default: 20): Einträge pro Seite
- `sort` (string, optional, default: pickupTime,asc): Sortierung

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "confirmationCode": "HUHN-K4M7",
      "customerName": "Max Mustermann",
      "customerEmail": "max@example.com",
      "chickenCount": 2,
      "friesCount": 3,
      "reservationDate": "2026-02-14",
      "pickupTime": "14:30",
      "status": "CONFIRMED",
      "notes": "Bitte extra knusprig",
      "createdAt": "2026-02-14T10:30:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "direction": "ASC",
      "property": "pickupTime"
    }
  },
  "totalElements": 45,
  "totalPages": 3,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

**Hinweis:**
- `pickupTime` kann `null` sein (Kunde hat keine Zeit angegeben)
- Sortierung: Reservierungen ohne Abholzeit am Ende

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Berechtigung

---

### PATCH /api/v1/staff/reservations/{reservationId}/status

Ändert den Status einer Reservierung.

**Path Parameters:**
- `reservationId` (UUID, required): ID der Reservierung

**Request Body:**
```json
{
  "status": "CONFIRMED",
  "notes": "Telefonisch bestätigt"
}
```

**Erlaubte Status-Übergänge:**
- `CONFIRMED` → `COMPLETED`, `NO_SHOW`, `CANCELLED`

**⚠️ Hinweis:** Es gibt keinen PENDING-Status mehr. Reservierungen starten direkt als CONFIRMED.

**Response (200 OK):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "customerName": "Max Mustermann",
  "chickenCount": 2,
  "friesCount": 3,
  "pickupTime": "2026-02-14T14:30:00",
  "status": "CONFIRMED",
  "previousStatus": "PENDING",
  "notes": "Telefonisch bestätigt",
  "updatedAt": "2026-02-11T11:00:00Z"
}
```

**Errors:**
- `400 Bad Request`: Ungültiger Status-Übergang
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Reservierung gehört nicht zum Standort des Mitarbeiters
- `404 Not Found`: Reservierung nicht gefunden
- `409 Conflict`: Kapazität nicht mehr verfügbar (bei PENDING → CONFIRMED)

**Error Response (400 - Ungültiger Übergang):**
```json
{
  "type": "https://api.foodtruck-booking.de/errors/invalid-status-transition",
  "title": "Invalid Status Transition",
  "status": 400,
  "detail": "Status-Übergang von COMPLETED nach CONFIRMED ist nicht erlaubt",
  "instance": "/api/v1/staff/reservations/770e8400.../status",
  "timestamp": "2026-02-11T11:00:00Z",
  "currentStatus": "COMPLETED",
  "requestedStatus": "CONFIRMED",
  "allowedTransitions": []
}
```

---

### GET /api/v1/staff/capacity

Gibt die aktuelle Kapazität des Standorts zurück.

**Query Parameters:**
- `date` (string, optional, default: heute, format: YYYY-MM-DD): Datum

**Response (200 OK):**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "totalCapacity": 50,
  "reserved": {
    "pending": 10,
    "confirmed": 25,
    "total": 35
  },
  "available": 15,
  "utilizationPercent": 70.0,
  "status": "LIMITED"
}
```

**status Werte:**
- `AVAILABLE`: >30% verfügbar (grün)
- `LIMITED`: 10-30% verfügbar (gelb)
- `ALMOST_FULL`: <10% verfügbar (rot)
- `FULL`: 0% verfügbar

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert

---

### POST /api/v1/staff/inventory

Setzt den täglichen Hähnchen-Vorrat für heute.

**Request Body:**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "totalChickens": 50
}
```

**Validierungsregeln:**
- `locationId`: Required, muss existieren und zum Mitarbeiter gehören
- `totalChickens`: Required, >= 0

**Response (200 OK / 201 Created):**
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "totalChickens": 50,
  "reservedChickens": 12,
  "availableChickens": 38,
  "createdAt": "2026-02-14T08:00:00Z",
  "updatedAt": "2026-02-14T08:00:00Z",
  "message": "Vorrat erfolgreich eingetragen"
}
```

**Business Rules:**
- Ein Eintrag pro Standort pro Tag
- Bei wiederholtem Aufruf: Update statt Insert
- Bestehende Reservierungen bleiben gültig
- Warnung wenn neuer Vorrat < bereits reserviert

**Errors:**
- `400 Bad Request`: Validierungsfehler
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Mitarbeiter gehört nicht zu diesem Standort

---

### GET /api/v1/staff/inventory

Gibt den aktuellen Vorrat für den Standort des Mitarbeiters zurück.

**Query Parameters:**
- `locationId` (UUID, required): ID des Standorts
- `date` (string, optional, default: heute): Datum

**Response (200 OK):**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "inventorySet": true,
  "totalChickens": 50,
  "reservedChickens": 35,
  "availableChickens": 15,
  "reservationCount": 18,
  "utilizationPercent": 70.0,
  "status": "LIMITED"
}
```

**Falls kein Vorrat eingetragen:**
```json
{
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "locationName": "Innenstadt",
  "date": "2026-02-14",
  "inventorySet": false,
  "message": "Bitte Tagesvorrat eintragen!"
}
```

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Mitarbeiter gehört nicht zu diesem Standort

---

## Admin API (ROLE_ADMIN)

### GET /api/v1/admin/reservations

Gibt alle Reservierungen aller Standorte zurück.

**Query Parameters:**
- `locationId` (UUID, optional): Filter nach Standort
- `status` (string, optional): Filter nach Status
- `dateFrom` (string, optional, format: YYYY-MM-DD): Ab Datum
- `dateTo` (string, optional, format: YYYY-MM-DD): Bis Datum
- `search` (string, optional): Suche nach Kundenname, E-Mail, Reservierungs-ID
- `page` (int, optional, default: 0): Seitennummer
- `size` (int, optional, default: 20): Einträge pro Seite
- `sort` (string, optional, default: createdAt,desc): Sortierung

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "locationId": "550e8400-e29b-41d4-a716-446655440000",
      "locationName": "Innenstadt",
      "customerName": "Max Mustermann",
      "customerEmail": "max@example.com",
      "chickenCount": 2,
      "friesCount": 3,
      "pickupTime": "2026-02-14T14:30:00",
      "status": "CONFIRMED",
      "createdAt": "2026-02-11T10:30:00Z"
    }
  ],
  "totalElements": 150,
  "totalPages": 8
}
```

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung

---

### POST /api/v1/admin/locations

Erstellt einen neuen Standort.

**Request Body:**
```json
{
  "name": "Neuer Standort",
  "address": "Neue Straße 123, 12345 Musterstadt",
  "active": true
}
```

**Validierungsregeln:**
- `name`: Required, 1-200 Zeichen, unique
- `address`: Required, 1-500 Zeichen
- `active`: Optional, default: true

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "name": "Neuer Standort",
  "address": "Neue Straße 123, 12345 Musterstadt",
  "active": true,
  "createdAt": "2026-02-11T10:30:00Z"
}
```

**Errors:**
- `400 Bad Request`: Validierungsfehler
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung
- `409 Conflict`: Standortname bereits vorhanden

---

### PUT /api/v1/admin/locations/{locationId}

Aktualisiert einen bestehenden Standort.

**Path Parameters:**
- `locationId` (UUID, required): ID des Standorts

**Request Body:**
```json
{
  "name": "Aktualisierter Name",
  "address": "Aktualisierte Adresse 456, 12345 Musterstadt",
  "active": true
}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Aktualisierter Name",
  "address": "Aktualisierte Adresse 456, 12345 Musterstadt",
  "active": true,
  "updatedAt": "2026-02-11T11:00:00Z"
}
```

**Errors:**
- `400 Bad Request`: Validierungsfehler
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung
- `404 Not Found`: Standort nicht gefunden
- `409 Conflict`: Standortname bereits vergeben

---

### DELETE /api/v1/admin/locations/{locationId}

Löscht einen Standort (nur wenn keine Reservierungen existieren).

**Path Parameters:**
- `locationId` (UUID, required): ID des Standorts

**Response (204 No Content):**
Kein Response Body.

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung
- `404 Not Found`: Standort nicht gefunden
- `409 Conflict`: Standort hat verknüpfte Reservierungen (nur Deaktivierung möglich)

---

### POST /api/v1/admin/locations/{locationId}/schedule

Erstellt oder aktualisiert den Wochenplan eines Standorts.

**Path Parameters:**
- `locationId` (UUID, required): ID des Standorts

**Request Body:**
```json
{
  "dayOfWeek": 1,
  "openingTime": "11:00",
  "closingTime": "20:00",
  "dailyCapacity": 50,
  "active": true
}
```

**Validierungsregeln:**
- `dayOfWeek`: Required, 1-7 (1=Montag, 7=Sonntag)
- `openingTime`: Required, Format HH:mm
- `closingTime`: Required, Format HH:mm, muss nach openingTime sein
- `dailyCapacity`: Required, 1-1000
- `active`: Optional, default: true

**Response (201 Created / 200 OK):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440000",
  "locationId": "550e8400-e29b-41d4-a716-446655440000",
  "dayOfWeek": 1,
  "dayName": "Montag",
  "openingTime": "11:00",
  "closingTime": "20:00",
  "dailyCapacity": 50,
  "active": true
}
```

**Errors:**
- `400 Bad Request`: Validierungsfehler
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung
- `404 Not Found`: Standort nicht gefunden

---

### GET /api/v1/admin/statistics

Gibt Statistiken über alle Standorte zurück.

**Query Parameters:**
- `locationId` (UUID, optional): Filter nach Standort
- `dateFrom` (string, optional, format: YYYY-MM-DD): Ab Datum (default: 7 Tage zurück)
- `dateTo` (string, optional, format: YYYY-MM-DD): Bis Datum (default: heute)

**Response (200 OK):**
```json
{
  "period": {
    "from": "2026-02-04",
    "to": "2026-02-11"
  },
  "overall": {
    "totalReservations": 342,
    "completedReservations": 298,
    "cancelledReservations": 28,
    "noShowReservations": 16,
    "averageChickenPerReservation": 2.3,
    "averageFriesPerReservation": 1.8,
    "noShowRate": 4.7,
    "cancellationRate": 8.2
  },
  "byLocation": [
    {
      "locationId": "550e8400-e29b-41d4-a716-446655440000",
      "locationName": "Innenstadt",
      "totalReservations": 198,
      "averageUtilization": 72.5,
      "peakDay": "Freitag",
      "peakUtilization": 95.0
    },
    {
      "locationId": "550e8400-e29b-41d4-a716-446655440001",
      "locationName": "Gewerbegebiet",
      "totalReservations": 144,
      "averageUtilization": 58.3,
      "peakDay": "Mittwoch",
      "peakUtilization": 82.0
    }
  ],
  "byDay": [
    {
      "date": "2026-02-04",
      "dayOfWeek": 3,
      "dayName": "Mittwoch",
      "reservations": 48,
      "utilization": 65.0
    }
  ]
}
```

**Errors:**
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Keine Admin-Berechtigung

---

## Error Response Format (RFC 7807)

Alle Fehler-Responses folgen dem RFC 7807 Problem Details Format:

```json
{
  "type": "https://api.foodtruck-booking.de/errors/{error-type}",
  "title": "Human Readable Title",
  "status": 400,
  "detail": "Detaillierte Fehlerbeschreibung",
  "instance": "/api/v1/endpoint-path",
  "timestamp": "2026-02-11T10:30:00Z",
  "errors": [
    {
      "field": "fieldName",
      "message": "Feldspezifische Fehlermeldung"
    }
  ]
}
```

**Error Types:**
- `validation-error`: Eingabevalidierung fehlgeschlagen
- `not-found`: Ressource nicht gefunden
- `capacity-exceeded`: Kapazität überschritten
- `business-rule-violation`: Geschäftsregel verletzt
- `invalid-status-transition`: Ungültiger Status-Übergang
- `authentication-failed`: Authentifizierung fehlgeschlagen
- `authorization-failed`: Keine Berechtigung
- `conflict`: Konflikt (Duplikat, Constraint)

---

## HTTP Status Codes Übersicht

| Code | Bedeutung | Verwendung |
|------|-----------|------------|
| 200 | OK | GET, PUT, PATCH erfolgreich |
| 201 | Created | POST erfolgreich, Ressource erstellt |
| 204 | No Content | DELETE erfolgreich |
| 400 | Bad Request | Validierungsfehler |
| 401 | Unauthorized | Nicht authentifiziert |
| 403 | Forbidden | Keine Berechtigung |
| 404 | Not Found | Ressource nicht gefunden |
| 409 | Conflict | Kapazität, Duplikat, Constraint |
| 422 | Unprocessable Entity | Business Rule Verletzung |
| 500 | Internal Server Error | Serverfehler |

---

## Pagination Standard

Alle Listen-Endpoints nutzen Spring Data Pageable:

**Query Parameters:**
- `page` (int, default: 0): Seitennummer (0-basiert)
- `size` (int, default: 20, max: 100): Einträge pro Seite
- `sort` (string): Sortierung (z.B. `pickupTime,asc` oder `createdAt,desc`)

**Response Format:**
```json
{
  "content": [ ... ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": { ... }
  },
  "totalElements": 150,
  "totalPages": 8,
  "size": 20,
  "number": 0,
  "first": true,
  "last": false
}
```

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initial erstellt basierend auf REQ-001 bis REQ-019 |
| 2026-02-14 | 2.0 | Auto-Accept, confirmationCode, Same-Day only, Inventory-Endpoints, Lookup/Cancel by Code |

---

## 🔴 Wichtig für LLM

Diese Datei ist der **Vertrag zwischen Frontend und Backend**.

**Verwenden zusammen mit:**
- `/docs/03-prompts/mandatory-context.md` (Tech-Stack, DTOs)
- `/docs/03-prompts/forbidden-actions.md` (Verbote)
- `/docs/02-requirements/functional-requirements.md` (Fachliche Anforderungen)

**Niemals:**
- Endpoints ohne Dokumentation hier hinzufügen
- Breaking Changes ohne Versionierung
- Abweichende Error-Formate verwenden

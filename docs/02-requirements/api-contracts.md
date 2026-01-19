# API Contracts

## Zweck dieser Datei
Definiert ALLE REST-API-Endpunkte mit exakten Request/Response-Schemas. Das ist der Vertrag zwischen Frontend und Backend.

## Verwendung
- **Wann ausfüllen**: VOR der Controller-Implementierung
- **Wann referenzieren**: Bei Controller/DTO-Implementierung, API-Tests
- **Wann aktualisieren**: Bei API-Änderungen (Breaking Changes dokumentieren!)

## Was du mit dem LLM machst
```
Prompt: "Definiere API-Endpunkte in /docs/02-requirements/api-contracts.md:

Endpoint: POST /api/v1/[resource]
Request: [JSON-Schema]
Response: [JSON-Schema]
Errors: [Welche Fehler möglich]"
```

## Format

### Base URL
```markdown
## Base URL
Alle Endpunkte beginnen mit: `/api/v1`
```

### Pro Endpoint

```markdown
### [HTTP-Method] /api/v1/[resource]

[Kurze Beschreibung was der Endpoint macht]

**Request**:
```json
{
  "field1": "string",
  "field2": 123
}
```

**Query Parameters** (optional):
- `param1` (string, optional): Beschreibung
- `param2` (int, required): Beschreibung

**Response** (200 OK):
```json
{
  "id": "uuid",
  "field1": "string",
  "createdAt": "2026-01-14T10:00:00Z"
}
```

**Errors**:
- `400 Bad Request`: Validierungsfehler
- `404 Not Found`: Ressource nicht gefunden
- `409 Conflict`: [Spezifischer Conflict]

**Error Response Format**:
```json
{
  "error": "Beschreibung",
  "details": ["Detail 1", "Detail 2"],
  "timestamp": "2026-01-14T10:00:00Z"
}
```
```

## Vollständiges Beispiel

```markdown
### POST /api/v1/users

Erstellt einen neuen Benutzer.

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "Max Mustermann"
}
```

**Response** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "name": "Max Mustermann",
  "createdAt": "2026-01-14T10:30:00Z"
}
```

**Errors**:
- `400 Bad Request`: E-Mail-Format ungültig, Passwort zu kurz
- `409 Conflict`: E-Mail bereits registriert

**Error Example** (400):
```json
{
  "error": "Validation failed",
  "details": [
    "Email must be valid format",
    "Password must be at least 8 characters"
  ],
  "timestamp": "2026-01-14T10:30:00Z"
}
```
```

## Standard HTTP-Status-Codes

Nutze diese konsistent:

**Erfolg**:
- `200 OK`: GET, PUT, PATCH erfolgreich
- `201 Created`: POST erfolgreich, neue Ressource erstellt
- `204 No Content`: DELETE erfolgreich

**Client-Fehler**:
- `400 Bad Request`: Validierungsfehler, ungültige Daten
- `401 Unauthorized`: Nicht authentifiziert
- `403 Forbidden`: Authentifiziert, aber keine Berechtigung
- `404 Not Found`: Ressource nicht gefunden
- `409 Conflict`: Konflikt (z.B. Duplikat, Constraint-Verletzung)

**Server-Fehler**:
- `500 Internal Server Error`: Unerwarteter Serverfehler

## Versionierung

Bei Breaking Changes:
- Neue API-Version: `/api/v2`
- Alte Version dokumentieren, aber als deprecated markieren

```markdown
## ⚠️ Deprecated
### GET /api/v1/old-endpoint
**Status**: Deprecated seit 2026-02-01
**Migration**: Nutze `/api/v2/new-endpoint`
```

## Pagination

Wenn Listen returned werden:

```markdown
**Response** (200 OK):
```json
{
  "content": [ ... Array von Objekten ... ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

**Query Parameters**:
- `page` (int, default: 0): Seitennummer
- `size` (int, default: 20): Anzahl pro Seite
```

---

## ⚠️ KRITISCH
- API-Contracts VOR der Implementierung schreiben!
- Frontend und Backend arbeiten nach diesem Dokument
- Änderungen = Breaking Changes, müssen kommuniziert werden

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Controller implementiert werden
- DTOs erstellt werden
- API-Tests geschrieben werden
- Frontend-Integration geplant wird

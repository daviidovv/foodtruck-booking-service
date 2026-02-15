# TASK-012: Staff-Dashboard - Wagen-basiertes Login & Inventory-Fix

## Status
**Done** - Implementiert am 2026-02-15

## Kontext
- 2 Foodtrucks (Wagen 1 & Wagen 2)
- Jeder Wagen hat ein Tablet für das Staff-Dashboard
- Jeder Wagen fährt an bestimmten Tagen zu bestimmten Standorten

### Wagen-Standort-Zuordnung
| Wagen | Di | Mi | Do | Fr |
|-------|-----|-----|-----|-----|
| Wagen 1 | Traunreut | Mitterfelden | Siegsdorf | Traunreut |
| Wagen 2 | Raubling | Bad Endorf | Bruckmühl | Prien |

## Probleme (aktuell)

### 1. Login-Validierung fehlt
`getLocations()` ist öffentlich - jedes Passwort wird akzeptiert.

### 2. Keine Wagen-Zuordnung
Es gibt nur einen "staff" Account, keine Unterscheidung zwischen Wagen.

### 3. Falscher Standort
Der erste Standort wird automatisch gewählt, nicht der heutige für diesen Wagen.

### 4. Fehlendes Error-Handling
Keine Fehlermeldung wenn setInventory fehlschlägt.

## Lösung

### 1. Zwei Staff-Accounts in SecurityConfig
```java
var wagen1 = User.builder()
    .username("wagen1")
    .password(passwordEncoder.encode("wagen1"))
    .roles("STAFF")
    .build();

var wagen2 = User.builder()
    .username("wagen2")
    .password(passwordEncoder.encode("wagen2"))
    .roles("STAFF")
    .build();
```

### 2. Wagen-Standort-Mapping (Frontend)
```typescript
// Mapping: Welcher Wagen fährt an welchem Tag wohin
const TRUCK_SCHEDULE: Record<string, Record<number, string>> = {
  'wagen1': {
    2: 'Traunreut',     // Dienstag
    3: 'Mitterfelden',  // Mittwoch
    4: 'Siegsdorf',     // Donnerstag
    5: 'Traunreut',     // Freitag
  },
  'wagen2': {
    2: 'Raubling',      // Dienstag
    3: 'Bad Endorf',    // Mittwoch
    4: 'Bruckmühl',     // Donnerstag
    5: 'Prien',         // Freitag
  },
}
```

### 3. Login-Flow
1. User gibt Credentials ein (wagen1/wagen2)
2. Validiere gegen geschützten Endpoint
3. Ermittle heutigen Wochentag
4. Finde den zugehörigen Standort aus dem Mapping
5. Speichere Credentials + LocationId in sessionStorage
6. Weiterleitung zum Dashboard

### 4. Sonderfälle behandeln
- **Ruhetag (Mo, Sa, So)**: "Heute kein Betrieb" anzeigen
- **Falscher Wochentag**: Standort-Auswahl anbieten (für Ausnahmen)

## Technische Umsetzung

### Backend: SecurityConfig.java
- Zwei Staff-User: `wagen1`, `wagen2`

### Frontend: StaffLoginPage.tsx
```typescript
const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault()
  setLoading(true)
  setError(null)

  try {
    // Hole alle Standorte
    const { content: locations } = await api.getLocations()

    // Ermittle heutigen Standort für diesen Wagen
    const todayDayOfWeek = new Date().getDay() // 0=So, 1=Mo, ...
    const isoDay = todayDayOfWeek === 0 ? 7 : todayDayOfWeek // ISO: 1=Mo, 7=So

    const truckSchedule = TRUCK_SCHEDULE[credentials.username]
    if (!truckSchedule) {
      setError('Unbekannter Benutzer')
      return
    }

    const todayLocationName = truckSchedule[isoDay]
    if (!todayLocationName) {
      setError('Heute ist Ruhetag')
      return
    }

    // Finde Location ID
    const todayLocation = locations.find(l => l.name === todayLocationName)
    if (!todayLocation) {
      setError('Standort nicht gefunden')
      return
    }

    // Validiere Credentials gegen Staff-Endpoint
    await api.getStaffReservations(
      todayLocation.id,
      new Date().toISOString().split('T')[0],
      credentials.username,
      credentials.password
    )

    // Erfolg - speichere und weiterleiten
    sessionStorage.setItem('staff_credentials', JSON.stringify(credentials))
    sessionStorage.setItem('staff_location', todayLocation.id)
    navigate('/staff/dashboard')

  } catch (err: any) {
    if (err?.status === 401 || err?.status === 403) {
      setError('Ungültige Anmeldedaten')
    } else {
      setError('Verbindungsfehler')
    }
  } finally {
    setLoading(false)
  }
}
```

### Frontend: StaffDashboardPage.tsx
- Error-Handler für Mutations hinzufügen
- Toast/Alert bei Erfolg/Fehler

## Dateien zu ändern
- `backend/.../config/SecurityConfig.java` - Zwei Staff-User
- `frontend/.../staff/pages/StaffLoginPage.tsx` - Wagen-Mapping + Validierung
- `frontend/.../staff/pages/StaffDashboardPage.tsx` - Error-Handling

## Akzeptanzkriterien
- [x] Login mit `wagen1`/`wagen2` funktioniert
- [x] Falsches Passwort zeigt Fehlermeldung
- [x] Nach Login ist automatisch der heutige Standort ausgewählt
- [x] An Ruhetagen: Login trotzdem möglich (erster Standort wird gewählt)
- [x] Hähnchen setzen zeigt Erfolgs-/Fehlermeldung (Toast-Notification)
- [x] Alte `staff` Credentials funktionieren nicht mehr

## Gelöste Fragen
- [x] Passwörter: wagen1/wagen1 und wagen2/wagen2
- [x] Ruhetage: Login erlaubt (erster Standort aus Schedule wird gewählt)

## Aufwand
- Backend (SecurityConfig): ~10 Min
- Frontend (Login): ~45 Min
- Frontend (Error-Handling): ~20 Min
- Testen: ~15 Min

# TASK-011: Homepage verbessern - Tagesaktuelle Standorte & Wochenplan

## Status
**Done** - Implementiert am 2026-02-15

## Problem
Aktuell werden auf der Homepage **alle 7 Standorte** angezeigt, auch wenn diese heute gar nicht aktiv sind. Alle erscheinen als "geschlossen", was verwirrend ist.

### Gewünschtes Verhalten
1. Homepage zeigt nur die **heute aktiven Standorte** (max. 2 pro Tag laut Schedule)
2. Wenn heute **kein Foodtruck fährt** (Mo, Sa, So): Schöne "Heute geschlossen"-Nachricht mit Link zur Wochenübersicht
3. Neue Seite **Wochenplan**: Zeigt welcher Truck wann wo ist
4. **Standort-Karte**: Bei jedem Standort Link zu Google Maps/Apple Karten mit genauer Position

## Anforderungen

### 1. Homepage-Anpassung
- Nur Standorte anzeigen, die laut `location_schedule` heute aktiv sind
- Bei keinem Standort heute: Freundliche Nachricht
  ```
  "Heute sind unsere Foodtrucks leider nicht unterwegs.
   Schau dir an, wann und wo du uns findest!"
   [Button: Wochenplan ansehen]
  ```

### 2. Neue Seite: Wochenplan (`/wochenplan`)
- Übersichtliche Darstellung der Woche
- Für jeden Tag: Welche Standorte mit Öffnungszeiten
- Pro Standort: Link zur Karte (Google Maps/Apple Karten)
- Design-Vorschlag:
  ```
  Wochenplan

  Dienstag
  ├── Traunreut (11:00 - 19:00)  [📍 Karte]
  └── Raubling (11:00 - 19:00)   [📍 Karte]

  Mittwoch
  ├── Mitterfelden (11:00 - 19:00)  [📍 Karte]
  └── Bad Endorf (11:00 - 19:00)    [📍 Karte]

  Donnerstag
  ├── Siegsdorf (11:00 - 19:00)  [📍 Karte]
  └── Bruckmühl (11:00 - 19:00)  [📍 Karte]

  Freitag
  ├── Traunreut (11:00 - 19:00)  [📍 Karte]
  └── Prien (11:00 - 19:00)      [📍 Karte]

  Samstag, Sonntag, Montag - Ruhetage
  ```

### 3. Standort-Koordinaten für Karten-Links
- Datenbank erweitern: `latitude` und `longitude` in `location` Tabelle
- Maps-Link generieren:
  - Google Maps: `https://www.google.com/maps?q={lat},{lng}`
  - Apple Maps: `https://maps.apple.com/?ll={lat},{lng}`
- Button/Link der automatisch richtige App öffnet (iOS → Apple Maps, sonst → Google Maps)

## Technische Änderungen

### Datenbank (neue Migration V4)
```sql
ALTER TABLE location ADD COLUMN latitude DECIMAL(10, 8);
ALTER TABLE location ADD COLUMN longitude DECIMAL(11, 8);

-- Koordinaten für bestehende Standorte (müssen recherchiert werden!)
UPDATE location SET latitude = ???, longitude = ??? WHERE name = 'Traunreut';
-- etc.
```

### Backend
- Neuer Endpoint: `GET /api/v1/locations/schedule` - Gibt Wochenplan zurück
- `LocationDTO` erweitern um `latitude`, `longitude`
- Optional: `GET /api/v1/locations/today` - Nur heutige Standorte

### Frontend
- `HomePage.tsx`: Nur heutige Standorte anzeigen, "Heute geschlossen"-Fallback
- Neue Seite: `SchedulePage.tsx` unter `/wochenplan`
- Komponente: `MapLink.tsx` - Erkennt OS und öffnet richtige Karten-App
- Navigation: Link zum Wochenplan im Header oder Footer

## Offene Fragen
- [ ] Genaue Koordinaten für alle 7 Standorte recherchieren
- [ ] Soll der Wochenplan auch im Header verlinkt sein?

## Abhängigkeiten
- TASK-010 (Mitarbeiter-Button) könnte zusammen umgesetzt werden (Footer-Redesign)

## Aufwand
- Datenbank-Migration: ~15 Min
- Backend-Anpassungen: ~30 Min
- Frontend (HomePage + SchedulePage): ~2h
- Koordinaten recherchieren: ~30 Min

# TASK-025: Mitarbeiter können Reservierungen erstellen

## Status
- [x] Neu
- [x] In Bearbeitung
- [x] Erledigt

## Ziel

Mitarbeiter sollen in ihrer Übersicht (Staff Dashboard) schnell eine Reservierung eintragen können, wenn ein Kunde telefonisch bestellt. So fällt der handschriftliche Zettel komplett weg.

## Hintergrund

- Telefon bleibt erstmal bestehen (Übergangsphase)
- Kunde ruft an → Mitarbeiter trägt Reservierung ins System ein
- Gleiche Übersicht wie Online-Reservierungen
- Zettelwirtschaft wird sofort eliminiert

## Anforderungen

### Frontend (Staff Dashboard)

1. Button "Neue Reservierung" im Staff Dashboard
2. Schnelles Formular mit:
   - Kundenname (Pflicht)
   - Telefonnummer (optional)
   - Anzahl Hähnchen
   - Anzahl Pommes
   - Gewünschte Abholzeit (optional)
   - Anmerkungen (optional)
3. Nach Erstellen: Reservierung erscheint sofort in der Liste

### Backend

- Neuer Endpoint: `POST /api/v1/staff/reservations`
- Nur für STAFF/ADMIN Rolle
- Gleiche Logik wie Kunden-Reservierung
- Reservierung wird dem aktuellen Standort/Tag zugeordnet

## Umsetzung

### Backend Endpoint

```java
@PostMapping("/api/v1/staff/reservations")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public ReservationResponse createStaffReservation(
    @RequestBody StaffReservationRequest request
) {
    // Erstellt Reservierung für aktuellen Tag/Standort des Mitarbeiters
}
```

### Frontend

```tsx
// Button im StaffDashboardPage
<Button onClick={() => setShowNewReservation(true)}>
  + Neue Reservierung
</Button>

// Modal/Dialog mit Formular
<NewReservationDialog
  open={showNewReservation}
  onClose={() => setShowNewReservation(false)}
  onSuccess={refetchReservations}
/>
```

## Akzeptanzkriterien

- [x] Button "Neue Reservierung" im Staff Dashboard sichtbar
- [x] Formular öffnet sich (Modal)
- [x] Pflichtfelder: Name, mindestens 1 Produkt
- [x] Nach Speichern: Reservierung erscheint in Liste
- [x] Bestätigungscode wird generiert (wie bei Online-Reservierung)
- [x] Fehlerbehandlung wenn keine Hähnchen mehr verfügbar

## Entscheidungen

- [x] Mitarbeiter ist fest seinem Wagen zugeordnet (wagen1 → Standorte Wagen 1)
- [x] Bestätigungscode wird generiert (gleiche API, Code ist nur Sicherheit - Abholung meist per Name)

## Priorität

**Hoch** - Wird für Präsentation beim Onkel gebraucht (Zettel fällt weg!)

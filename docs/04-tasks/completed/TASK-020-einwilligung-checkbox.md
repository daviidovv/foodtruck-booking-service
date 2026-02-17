# TASK-020: Einwilligungs-Checkbox bei Reservierung

## Status
- [x] Neu
- [x] In Bearbeitung
- [x] Erledigt

## Ziel

DSGVO-konforme Einwilligung zur Datenverarbeitung bei der Reservierung einholen. Der Kunde muss aktiv bestätigen, dass er die Datenschutzerklärung gelesen hat und der Verarbeitung seiner Daten zustimmt.

## Anforderungen

### Frontend

1. Checkbox im Reservierungsformular (nicht vorausgewählt!)
2. Text mit Link zur Datenschutzerklärung:
   ```
   [ ] Ich habe die Datenschutzerklärung gelesen und stimme der
       Verarbeitung meiner Daten zur Reservierungsabwicklung zu.
   ```
3. Formular kann nur abgeschickt werden, wenn Checkbox aktiviert ist
4. Fehlermeldung wenn nicht aktiviert: "Bitte stimmen Sie der Datenschutzerklärung zu"

### Backend (optional)

- Timestamp speichern wann zugestimmt wurde
- Feld `privacyAcceptedAt` in Reservation-Entity

## Umsetzung

### Frontend (ReservationForm)

```tsx
// Neue Checkbox im Formular
<label>
  <input
    type="checkbox"
    checked={privacyAccepted}
    onChange={(e) => setPrivacyAccepted(e.target.checked)}
    required
  />
  Ich habe die <a href="/datenschutz">Datenschutzerklärung</a> gelesen
  und stimme der Verarbeitung meiner Daten zu.
</label>
```

### Backend (optional)

```java
// Reservation.java
@Column(name = "privacy_accepted_at")
private LocalDateTime privacyAcceptedAt;
```

```sql
-- Flyway Migration
ALTER TABLE reservations ADD COLUMN privacy_accepted_at TIMESTAMP;
```

## Akzeptanzkriterien

- [x] Checkbox im Reservierungsformular vorhanden
- [x] Checkbox ist standardmäßig NICHT aktiviert
- [x] Link zur Datenschutzerklärung funktioniert
- [x] Formular-Submit ohne Checkbox nicht möglich
- [x] Fehlermeldung wird angezeigt wenn Checkbox fehlt

## Abhängigkeiten

- TASK-019 (Datenschutzerklärung) sollte zuerst existieren, damit der Link funktioniert

## Hinweise

- Die Checkbox darf NICHT vorausgewählt sein (DSGVO-Anforderung)
- Das Speichern des Timestamps im Backend ist optional, aber empfohlen für Nachweispflicht

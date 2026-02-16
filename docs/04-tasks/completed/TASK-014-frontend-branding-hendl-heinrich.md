# TASK-014: Frontend Branding "Hendl Heinrich"

## Status
- [x] Abgeschlossen (2026-02-16)

## Ziel

Das Frontend mit dem Branding von "Hendl Heinrich" individualisieren - ein langjähriges, traditionelles Familienunternehmen. Design-Stil: Modern mit traditionellen Akzenten.

## Farbschema

| Farbe | HEX | Verwendung |
|-------|-----|------------|
| **Hendl-Grün** | `#0C6633` | Hauptfarbe (Primary), Header, Buttons |
| **Hendl-Rot** | `#BC3426` | Akzentfarbe, Warnungen, "Fast ausverkauft" |

## Akzeptanzkriterien

### Branding
- [ ] Firmenname "Hendl Heinrich" im Header sichtbar
- [ ] Hähnchen-Logo/Icon aus Icon-Library (z.B. Lucide) einbinden
- [ ] Farbschema auf Hendl-Grün (#0C6633) als Primary umstellen
- [ ] Hendl-Rot (#BC3426) als Akzent/Warn-Farbe einsetzen

### Kapazitätsanzeige (Urgency-Level: Mittel)
- [ ] Aktuelle Fortschrittsleiste durch bessere Lösung ersetzen
- [ ] Text "Nur noch X Hähnchen!" bei niedrigem Bestand (< 30%)
- [ ] Farbwechsel: Grün → Orange → Rot je nach Verfügbarkeit
- [ ] Psychologischer Anreiz ohne zu aufdringlich zu sein

### Design-Anpassungen
- [ ] Header mit Hendl-Grün Hintergrund
- [ ] Buttons in Hendl-Grün
- [ ] Warme, einladende Optik passend zur Familientradition
- [ ] Responsive Design beibehalten

### Technisch
- [ ] Tailwind Config anpassen (neue Farben)
- [ ] CSS-Variablen in index.css aktualisieren
- [ ] Betroffene Komponenten: Header, HomePage, ReservationPage, Badge

## Betroffene Dateien

- `frontend/tailwind.config.js` - Neue Farben definieren
- `frontend/src/index.css` - CSS-Variablen
- `frontend/src/components/layout/Header.tsx` - Logo + Branding
- `frontend/src/features/customer/pages/HomePage.tsx` - Kapazitätsanzeige
- `frontend/src/components/ui/badge.tsx` - Status-Badges anpassen

## Referenz

Aktuelles Grün vom Hendl-Wagen:
- HEX: #0C6633
- RGB: 12, 102, 51

Rot von der Hähnchen-Verpackung:
- HEX: #BC3426
- RGB: 188, 52, 38

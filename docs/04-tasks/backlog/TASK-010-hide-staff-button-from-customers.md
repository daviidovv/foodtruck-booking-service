# TASK-010: Mitarbeiter-Button für Kunden ausblenden

## Status
**Backlog** - Zur Evaluierung

## Problem
Der "Mitarbeiter"-Button im Header ist für alle Kunden sichtbar. Das ist aus UX-Sicht nicht ideal:
- Kunden könnten verwirrt werden
- Unnötiger UI-Clutter
- Staff-Bereich sollte "versteckt" sein

## Optionen

### Option 1: Button komplett entfernen
- Staff/Admin erreichen ihre Seiten direkt über URL
- `/staff/login` und `/admin/login` manuell eingeben
- **Pro:** Saubere Kundenansicht
- **Contra:** Mitarbeiter müssen URL kennen

### Option 2: Versteckter Zugang
- Kein sichtbarer Button
- Zugang über spezielle Geste (z.B. 5x auf Logo tippen)
- Oder Tastenkombination (z.B. Ctrl+Shift+S)
- **Pro:** Sauber + trotzdem erreichbar
- **Contra:** Muss kommuniziert werden

### Option 3: Footer-Link
- Kleiner, unauffälliger Link im Footer
- "Mitarbeiter-Bereich" in grau/klein
- **Pro:** Erreichbar aber unauffällig
- **Contra:** Immer noch sichtbar

### Option 4: Separate Subdomain (siehe TASK-008)
- `staff.foodtruck.de` für Mitarbeiter
- Hauptseite hat keinen Link
- **Pro:** Komplette Trennung
- **Contra:** Mehr Infrastruktur-Aufwand

## Empfehlung
**Option 3 (Footer-Link)** als schnelle Lösung, später ggf. Option 4.

## Implementierung (wenn entschieden)

### Für Option 1 oder 3:
```tsx
// Header.tsx - Button entfernen
// Footer.tsx - Kleinen Link hinzufügen (Option 3)
<footer className="border-t py-4 text-center text-xs text-muted-foreground">
  <p>© 2024 Hähnchen-Truck</p>
  <Link to="/staff/login" className="text-muted-foreground/50 hover:text-muted-foreground">
    Mitarbeiter
  </Link>
</footer>
```

### Für Option 2:
```tsx
// Header.tsx - Logo mit onClick Counter
const [clickCount, setClickCount] = useState(0)
const handleLogoClick = () => {
  setClickCount(c => c + 1)
  if (clickCount >= 4) {
    navigate('/staff/login')
    setClickCount(0)
  }
}
```

## Aufwand
- Option 1: ~5 Minuten
- Option 2: ~15 Minuten
- Option 3: ~10 Minuten
- Option 4: Siehe TASK-008

## Abhängigkeiten
Keine

## Notizen
- Admin-Zugang sollte gleich behandelt werden
- Entscheidung durch Product Owner

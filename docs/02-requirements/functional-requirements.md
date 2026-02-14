# Functional Requirements - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-14

---

## Reservierungsverwaltung (Kunde)

### REQ-001: Standort basierend auf Wochentag auswählen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-001

**Beschreibung**:
Kunden müssen sehen können, welcher Foodtruck-Standort an welchem Wochentag verfügbar ist, um den passenden Standort für ihre Reservierung auszuwählen.

**Akzeptanzkriterien**:
- [ ] System zeigt alle aktiven Standorte mit zugehörigen Wochentagen an
- [ ] Öffnungszeiten pro Standort und Tag werden angezeigt
- [ ] Nur Standorte mit `active=true` in `location_schedule` werden angezeigt
- [ ] Wochentagsanzeige erfolgt benutzerfreundlich (z.B. "Montag, Mittwoch, Freitag")
- [ ] Standorte mit Adresse werden klar dargestellt

**Datengrundlage**: `location`, `location_schedule` Tabellen

---

### REQ-002: Reservierung erstellen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-002

**Beschreibung**:
Kunden müssen eine Reservierung für Hähnchen und Pommes erstellen können mit Angabe von Kundenname, optionaler E-Mail, Anzahl Produkte und optionaler Abholzeit. Reservierungen werden automatisch bestätigt solange Vorrat verfügbar ist.

**Akzeptanzkriterien**:
- [ ] Kunde kann Standort auswählen für HEUTE (nur Same-Day!)
- [ ] Kunde gibt Kundenname an (Pflichtfeld, max. 200 Zeichen)
- [ ] Kunde kann optional E-Mail-Adresse angeben (max. 255 Zeichen)
- [ ] Kunde wählt Anzahl Hähnchen (min. 0, max. 50)
- [ ] Kunde wählt Anzahl Pommes (min. 0, max. 50)
- [ ] Mindestens ein Produkt (Hähnchen oder Pommes) muss ausgewählt sein
- [ ] Kunde kann optional Abholzeit angeben (innerhalb Öffnungszeiten)
- [ ] System prüft ob genug Vorrat vorhanden ist
- [ ] System erstellt Reservierung mit Status `CONFIRMED` (automatisch!)
- [ ] System generiert eindeutigen Bestätigungscode (z.B. `HUHN-K4M7`)
- [ ] System reduziert verfügbaren Vorrat sofort
- [ ] System gibt Bestätigungscode zurück

**Business Rules**:
- E-Mail ist OPTIONAL (Design-Entscheidung: niedrige Hürde für Kunden)
- Abholzeit ist OPTIONAL (Kunde kann kommen wann er will)
- **Status bei Erstellung: `CONFIRMED`** (automatisch, kein PENDING!)
- Nur Same-Day-Reservierungen möglich (kein Vorausbuchen)
- Reservierung nur wenn: Vorrat (daily_inventory) - Reserviert >= chickenCount
- `chicken_count + fries_count` muss > 0 sein

**Datengrundlage**: `reservation`, `daily_inventory` Tabellen

---

### REQ-003: Reservierungsbestätigung erhalten
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-002, REQ-002

**Beschreibung**:
Kunden erhalten nach erfolgreicher Reservierung einen Bestätigungscode, mit dem sie ihre Reservierung verwalten können. Falls E-Mail angegeben wurde, wird zusätzlich eine Bestätigungsmail versendet.

**Akzeptanzkriterien**:
- [ ] System zeigt Bestätigungsseite mit allen Reservierungsdetails an
- [ ] **Bestätigungscode wird GROSS und PROMINENT angezeigt** (z.B. `HUHN-K4M7`)
- [ ] Hinweis: "Bitte notieren Sie diesen Code für Änderungen/Stornierung"
- [ ] Falls E-Mail vorhanden: Bestätigungsmail mit Code wird versendet
- [ ] Bestätigungsmail enthält: Bestätigungscode, Standort, Abholzeit (falls angegeben), Produktanzahl, Kundenname
- [ ] Kunde kann Bestätigungsseite ausdrucken/speichern

**Technische Hinweise**:
- Bestätigungscode: 8 Zeichen, alphanumerisch, unique
- E-Mail-Versand nur wenn `customer_email IS NOT NULL`
- E-Mail-Template mit allen relevanten Infos und Code

### REQ-003a: Reservierung per Code nachschlagen (NEU)
**Priorität**: Hoch
**Phase**: MVP

**Beschreibung**:
Kunden können ihre Reservierung mit dem Bestätigungscode online nachschlagen.

**Akzeptanzkriterien**:
- [ ] Eingabefeld für Bestätigungscode auf der Website
- [ ] Bei gültigem Code: Reservierungsdetails anzeigen
- [ ] Bei ungültigem Code: Fehlermeldung

### REQ-003b: Reservierung stornieren (NEU)
**Priorität**: Hoch
**Phase**: MVP

**Beschreibung**:
Kunden können ihre Reservierung jederzeit mit dem Bestätigungscode stornieren.

**Akzeptanzkriterien**:
- [ ] Stornieren-Button bei Reservierungsansicht
- [ ] Bestätigung: "Möchten Sie wirklich stornieren?"
- [ ] Bei Stornierung: Status auf CANCELLED setzen
- [ ] Vorrat wird wieder freigegeben
- [ ] Stornierung ist jederzeit möglich (keine Frist)

---

### REQ-004: Verfügbare Standorte und Zeiten sehen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-001, REQ-001

**Beschreibung**:
Kunden sehen für HEUTE welche Standorte geöffnet sind und wie viele Hähnchen noch verfügbar sind (Live-Anzeige).

**Akzeptanzkriterien**:
- [ ] Anzeige der heute geöffneten Standorte
- [ ] Öffnungszeiten pro Standort werden angezeigt
- [ ] **Live-Verfügbarkeit**: "Noch X Hähnchen verfügbar" (aktualisiert sich)
- [ ] Falls kein Vorrat eingetragen: "Noch nicht verfügbar - bitte später wiederkommen"
- [ ] Falls Vorrat = 0: "Ausverkauft" deutlich markiert
- [ ] Mobile-optimierte Darstellung

**Business Rules**:
- **Kapazität basiert auf `daily_inventory.total_chickens`** (vom Mitarbeiter eingetragen!)
- Verfügbar = `total_chickens - SUM(reservierte Hähnchen)`
- Nur Standorte mit Vorratseintrag für heute werden angezeigt
- Nur `active=true` Standorte anzeigen
- **Nur Same-Day**: Keine Anzeige für andere Tage

---

## Reservierungsverwaltung (Mitarbeiter)

### REQ-005: Login für Mitarbeiter
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-011

**Beschreibung**:
Mitarbeiter müssen sich authentifizieren können, um Zugriff auf das Tablet-Interface zu erhalten.

**Akzeptanzkriterien**:
- [ ] Login-Formular mit Benutzername und Passwort
- [ ] Spring Security Basic Authentication (MVP)
- [ ] Session-basiertes Login
- [ ] Fehlermeldung bei falschen Credentials
- [ ] Automatischer Logout nach Inaktivität (configurable)
- [ ] Mitarbeiter wird einem Standort zugeordnet

**Technische Hinweise**:
- MVP: Spring Security mit Basic Auth
- Phase 2: JWT-basierte Authentifizierung geplant
- Rollen: `ROLE_STAFF`, `ROLE_ADMIN`

---

### REQ-006: Reservierungen des Tages am eigenen Standort sehen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-012

**Beschreibung**:
Mitarbeiter sehen alle Reservierungen des aktuellen Tages für ihren zugewiesenen Standort.

**Akzeptanzkriterien**:
- [ ] Anzeige aller Reservierungen des Standorts für heute
- [ ] Gruppierung nach Abholzeit (chronologisch sortiert)
- [ ] Anzeige von: Kundenname, Abholzeit, Produktanzahl, Status
- [ ] Farbliche Kennzeichnung nach Status (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)
- [ ] Filter nach Status möglich
- [ ] Suchfunktion nach Kundenname
- [ ] Auto-Refresh alle 30 Sekunden
- [ ] Mobile/Tablet-optimiert

**Business Rules**:
- Mitarbeiter sieht NUR Reservierungen seines Standorts
- Nur Reservierungen mit `pickup_time` am aktuellen Tag
- Sortierung: Aufsteigend nach `pickup_time`

**Datengrundlage**: `reservation` JOIN `location`

---

### REQ-007: ~~Reservierung bestätigen~~ ENTFÄLLT
**Priorität**: ~~Hoch~~ ENTFÄLLT
**Phase**: ~~MVP~~

**⚠️ ENTFÄLLT: Reservierungen werden automatisch bestätigt!**

Da der Workflow nun Auto-Accept ist, gibt es keinen PENDING-Status mehr.
Reservierungen werden sofort CONFIRMED wenn der Vorrat ausreicht.

Mitarbeiter müssen Reservierungen NICHT mehr manuell bestätigen.

---

### REQ-008: Reservierung stornieren
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-014

**Beschreibung**:
Mitarbeiter können Reservierungen stornieren (z.B. bei Kundenanruf, technischen Problemen, Kapazitätsüberschreitung).

**Akzeptanzkriterien**:
- [ ] Button "Stornieren" bei allen aktiven Reservierungen (PENDING, CONFIRMED)
- [ ] Status wird auf CANCELLED gesetzt
- [ ] Optional: Stornierungsgrund als Notiz hinterlegen
- [ ] `updated_at` wird aktualisiert
- [ ] Visuelles Feedback
- [ ] Falls E-Mail vorhanden: Optional Stornierungsmail senden
- [ ] Stornierte Reservierungen geben Kapazität wieder frei

**Business Rules**:
- PENDING → CANCELLED erlaubt
- CONFIRMED → CANCELLED erlaubt
- COMPLETED, NO_SHOW, CANCELLED → keine weitere Änderung

**Status-Transition**: `PENDING|CONFIRMED → CANCELLED`

---

### REQ-009: Reservierung abschließen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-015

**Beschreibung**:
Mitarbeiter können Reservierungen als abgeholt (COMPLETED) oder nicht erschienen (NO_SHOW) markieren.

**Akzeptanzkriterien**:
- [ ] Button "Abgeschlossen" bei CONFIRMED Reservierungen → Status COMPLETED
- [ ] Button "Nicht erschienen" bei CONFIRMED Reservierungen → Status NO_SHOW
- [ ] `updated_at` wird aktualisiert
- [ ] Visuelles Feedback
- [ ] Abgeschlossene Reservierungen werden ausgegraut/archiviert
- [ ] NO_SHOW Reservierungen geben Kapazität wieder frei

**Business Rules**:
- Nur CONFIRMED → COMPLETED erlaubt
- Nur CONFIRMED → NO_SHOW erlaubt
- Nach COMPLETED/NO_SHOW keine weitere Änderung

**Status-Transitions**:
- `CONFIRMED → COMPLETED`
- `CONFIRMED → NO_SHOW`

---

### REQ-010: Verfügbare Kapazität sehen
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-016

**Beschreibung**:
Mitarbeiter sehen auf einen Blick den Vorrat und die verfügbare Kapazität ihres Standorts für den aktuellen Tag.

**Akzeptanzkriterien**:
- [ ] Dashboard zeigt eingetragenen Vorrat (aus `daily_inventory.total_chickens`)
- [ ] Dashboard zeigt reservierte Menge (Summe aller CONFIRMED Reservierungen)
- [ ] Dashboard zeigt verfügbare Restmenge
- [ ] Farbliche Kennzeichnung: Grün (>30%), Gelb (10-30%), Rot (<10%)
- [ ] Echtzeit-Update bei neuen Reservierungen/Stornierungen
- [ ] Falls kein Vorrat eingetragen: Warnung anzeigen

**Berechnung**:
```
Eingetragener Vorrat = daily_inventory.total_chickens (für heute)
Reserviert = SUM(chicken_count) WHERE status = 'CONFIRMED' AND reservation_date = TODAY
Verfügbar = Eingetragener Vorrat - Reserviert
```

### REQ-010a: Tagesvorrat eintragen (NEU)
**Priorität**: Hoch
**Phase**: MVP

**Beschreibung**:
Mitarbeiter können morgens den täglichen Hähnchen-Vorrat für ihren Standort eintragen.

**Akzeptanzkriterien**:
- [ ] Eingabefeld für Anzahl Hähnchen
- [ ] Speichern-Button
- [ ] Erfolgsbestätigung
- [ ] Vorrat kann jederzeit geändert werden (Nachschub, Ausfall)
- [ ] Änderung wird sofort wirksam (Live-Update für Kunden)
- [ ] Validierung: Vorrat >= 0

**Business Rules**:
- Ein Eintrag pro Standort pro Tag
- Ohne Vorratseintrag: Keine Reservierungen möglich
- Bei Änderung: Bestehende Reservierungen bleiben gültig

---

## Standortverwaltung (Admin)

### REQ-011: Login für Admin
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: REQ-005

**Beschreibung**:
Administratoren müssen sich authentifizieren können mit erweiterten Rechten (Zugriff auf alle Standorte, Konfiguration).

**Akzeptanzkriterien**:
- [ ] Gleicher Login-Mechanismus wie Mitarbeiter (REQ-005)
- [ ] Rolle: `ROLE_ADMIN`
- [ ] Admin hat Zugriff auf alle Standorte
- [ ] Admin hat Zugriff auf Systemkonfiguration

**Business Rules**:
- Admin hat alle Rechte von Mitarbeiter + zusätzliche Admin-Rechte
- Admin kann nicht standortbeschränkt werden

---

### REQ-012: Standorte verwalten (CRUD)
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-021

**Beschreibung**:
Administratoren können Standorte erstellen, bearbeiten, deaktivieren und löschen.

**Akzeptanzkriterien**:
- [ ] Standort erstellen: Name, Adresse, initial `active=true`
- [ ] Standort bearbeiten: Name, Adresse ändern
- [ ] Standort deaktivieren: `active=false` setzen (Soft Delete)
- [ ] Standort löschen: Nur wenn keine verknüpften Reservierungen existieren
- [ ] Liste aller Standorte anzeigen (aktiv + inaktiv)
- [ ] Validierung: Name max. 200 Zeichen, Adresse max. 500 Zeichen

**Business Rules**:
- Standorte mit Reservierungen können nicht gelöscht werden (nur deaktiviert)
- UUID als Primary Key

**Datengrundlage**: `location` Tabelle

---

### REQ-013: Wochentags-Schedule konfigurieren
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-021

**Beschreibung**:
Administratoren können für jeden Standort konfigurieren, an welchen Wochentagen er geöffnet ist, mit Öffnungszeiten und Kapazität.

**Akzeptanzkriterien**:
- [ ] Für jeden Standort: Wochentage 1-7 (Montag-Sonntag) konfigurierbar
- [ ] Pro Wochentag: Öffnungszeit, Schließzeit, Kapazität, aktiv/inaktiv
- [ ] Validierung: `opening_time < closing_time`
- [ ] Validierung: `daily_capacity > 0`
- [ ] Schedule erstellen, bearbeiten, deaktivieren
- [ ] Visuelle Darstellung: Wochenkalender

**Business Rules**:
- Ein Standort kann mehrere Wochentagseinträge haben
- `day_of_week`: 1=Montag, 7=Sonntag (ISO 8601)
- Kapazität bezieht sich auf Hähnchen (Pommes unbegrenzt im MVP)

**Datengrundlage**: `location_schedule` Tabelle

---

### REQ-014: Kapazität pro Standort/Tag konfigurieren
**Priorität**: Mittel
**Phase**: MVP
**Verknüpfung**: REQ-013

**Beschreibung**:
Administratoren können die Standard-Kapazität pro Standort und Tag konfigurieren (via REQ-013). Zukünftig: Tagesspezifische Überschreibung.

**Akzeptanzkriterien**:
- [ ] Standard-Kapazität in `location_schedule.daily_capacity`
- [ ] Änderung gilt für alle zukünftigen Tage dieses Wochentags
- [ ] Validierung: Kapazität > 0
- [ ] Visuelle Warnung, wenn Änderung bestehende Reservierungen betrifft

**Phase 2 (zukünftig)**:
- Tagesspezifische Überschreibung (z.B. "Nur am 2026-02-14 mehr Kapazität")

**Datengrundlage**: `location_schedule.daily_capacity`

---

### REQ-015: Alle Reservierungen sehen (beide Standorte)
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: US-022

**Beschreibung**:
Administratoren haben eine Gesamtübersicht aller Reservierungen über alle Standorte hinweg.

**Akzeptanzkriterien**:
- [ ] Liste aller Reservierungen mit Standort-Spalte
- [ ] Filter nach: Standort, Status, Datum
- [ ] Suchfunktion nach Kundenname, E-Mail, Reservierungs-ID
- [ ] Export als CSV/Excel
- [ ] Paginierung (20 Einträge pro Seite)
- [ ] Sortierung nach Abholzeit, Erstellungszeitpunkt

**Datengrundlage**: `reservation` JOIN `location`

---

### REQ-016: Statistiken sehen
**Priorität**: Mittel
**Phase**: MVP
**Verknüpfung**: US-023

**Beschreibung**:
Administratoren können Auslastungsstatistiken pro Standort einsehen zur Kapazitätsplanung.

**Akzeptanzkriterien**:
- [ ] Auslastung pro Standort (prozentual)
- [ ] Anzahl Reservierungen pro Tag/Woche
- [ ] Durchschnittliche Bestellgröße (Hähnchen/Pommes)
- [ ] No-Show-Rate
- [ ] Zeitraumfilter (letzte 7 Tage, 30 Tage, custom)
- [ ] Diagramme: Balkendiagramm, Liniendiagramm

**Metriken**:
- Auslastung = (Reservierte Kapazität / Gesamtkapazität) * 100
- No-Show-Rate = (NO_SHOW / CONFIRMED) * 100

**Phase 2 Erweiterung**:
- Umsatzprognose
- Beliebtheits-Ranking Standorte

---

## Validierung & Business Rules

### REQ-017: Zeitvalidierung
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: REQ-002

**Beschreibung**:
Das System validiert Reservierungen für den aktuellen Tag. Abholzeit ist optional.

**Akzeptanzkriterien**:
- [ ] Reservierung nur für HEUTE möglich (Same-Day only)
- [ ] Standort muss heute geöffnet sein (`location_schedule.active=true`)
- [ ] Falls Abholzeit angegeben: Muss innerhalb Öffnungszeiten liegen
- [ ] Falls Abholzeit angegeben: Muss in der Zukunft liegen
- [ ] Fehlermeldungen sind klar und benutzerfreundlich

**Validierungsregel**:
```
reservation_date = TODAY (Pflicht)
IF pickup_time IS NOT NULL:
    pickup_time > NOW()
    pickup_time BETWEEN opening_time AND closing_time
day_of_week(TODAY) = location_schedule.day_of_week
location_schedule.active = true
```

---

### REQ-018: Kapazitätsprüfung
**Priorität**: Hoch
**Phase**: MVP
**Verknüpfung**: REQ-002

**Beschreibung**:
Das System verhindert Überbuchungen durch Vorratsprüfung bei Reservierungserstellung.

**Akzeptanzkriterien**:
- [ ] Bei Reservierungserstellung: Prüfung ob genug Vorrat vorhanden
- [ ] Fehlermeldung: "Leider nicht genug verfügbar, nur noch X Hähnchen da"
- [ ] Falls Vorrat = 0: "Ausverkauft"
- [ ] Falls kein Vorrat eingetragen: "Reservierung aktuell nicht möglich"
- [ ] Pommes-Kapazität wird im MVP nicht geprüft (unbegrenzt)

**Validierungsregel**:
```
Vorrat = daily_inventory.total_chickens (für heute)
Reserviert = SUM(chicken_count WHERE status = 'CONFIRMED' AND reservation_date = TODAY)
Verfügbar = Vorrat - Reserviert
Neue Reservierung erlaubt nur wenn: chicken_count <= Verfügbar
```

**Race Condition Handling**:
- Pessimistic Locking beim Prüfen und Speichern
- Falls Race Condition: Fehlermeldung und Retry-Hinweis

---

### REQ-019: Doppelbuchungen vermeiden
**Priorität**: Mittel
**Phase**: MVP
**Verknüpfung**: REQ-002

**Beschreibung**:
Das System warnt vor möglichen Doppelbuchungen, wenn derselbe Kunde mehrfach zur gleichen Zeit bucht.

**Akzeptanzkriterien**:
- [ ] Prüfung: Gleicher `customer_name` + gleiche `pickup_time` (±15 Minuten)
- [ ] Falls E-Mail vorhanden: Zusätzliche Prüfung auf gleiche `customer_email`
- [ ] Warnung anzeigen: "Es existiert bereits eine Reservierung für diesen Namen zur ähnlichen Zeit"
- [ ] Kunde kann Warnung ignorieren und trotzdem buchen
- [ ] Keine harte Blockierung (nur Warnung)

**Validierungsregel**:
```
Warnung wenn existiert:
  customer_name = [input_name]
  AND pickup_time BETWEEN [input_time - 15min] AND [input_time + 15min]
  AND status IN ('PENDING', 'CONFIRMED')
```

**Business Rule**:
- Nur Warnung, keine Blockierung (Familien können gemeinsam buchen)

---

## Verknüpfungen

### Verwandte Dokumente
- **User Stories**: `/docs/02-requirements/user-stories.md`
- **API Contracts**: `/docs/02-requirements/api-contracts.md`
- **Data Model**: `/docs/01-architecture/data-model.md`
- **Vision**: `/docs/00-meta/vision.md`

### Tasks
- **TASK-001**: Project Foundation Documentation

---

## Änderungshistorie

| Datum | Änderung | REQ-IDs | Grund |
|-------|----------|---------|-------|
| 2026-01-19 | Initial erstellt | REQ-001 bis REQ-019 | MVP-Requirements definiert |

---

## Offene Punkte

**Geklärt** (Stand 2026-02-14):
- [x] Stornierung: Jederzeit möglich (keine Frist)
- [x] Reservierungszeitraum: Nur Same-Day
- [x] Abholzeit: Optional
- [x] Workflow: Auto-Accept (kein PENDING)
- [x] Kapazität: Täglicher Vorrat vom Mitarbeiter

**Offen für Phase 2**:
- [ ] Benachrichtigungssystem bei Statusänderungen
- [ ] Multi-Tenant-Support falls weiterer Foodtruck hinzukommt

---

## 🔴 Wichtig für LLM

Diese Datei wird benötigt bei:
- Feature-Implementierung (Controller, Service, Repository)
- Test-Erstellung (jede REQ braucht Tests)
- API-Design (Endpoints basieren auf Requirements)
- User-Story-Entwicklung (US → REQ Mapping)

**Immer zusammen mit:**
- `/docs/03-prompts/mandatory-context.md`
- `/docs/03-prompts/forbidden-actions.md`

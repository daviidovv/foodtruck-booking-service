# User Stories - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-11

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Feature-Implementierung (fachlicher Kontext)
- Bei UI/UX-Design (Nutzer-Perspektive)
- Bei Akzeptanztest-Erstellung
- Bei Sprint-Planung (Story Points schätzen)
- Bei Priorisierung von Features

**Prompt-Beispiel:**
```
Implementiere US-002 (Reservierung erstellen) basierend auf:
- /docs/02-requirements/user-stories.md (Akzeptanzkriterien)
- /docs/02-requirements/api-contracts.md (API-Spezifikation)
- /docs/03-prompts/mandatory-context.md (Tech-Stack)
```

---

## Rollen-Definitionen

| Rolle | Beschreibung | Authentifizierung |
|-------|--------------|-------------------|
| **Kunde** | Endnutzer, der Reservierungen erstellt | Keine (anonym) |
| **Mitarbeiter** | Personal im Foodtruck, verwaltet Reservierungen | Basic Auth (ROLE_STAFF) |
| **Administrator** | Betreiber, verwaltet System und Standorte | Basic Auth (ROLE_ADMIN) |

---

## Epic 1: Kundenreservierung

**Beschreibung:** Kunden können online Hähnchen und Pommes reservieren, ohne anrufen zu müssen.

**Business Value:** Reduzierung telefonischer Reservierungen um 80%+, höhere Kundenzufriedenheit

---

### US-001: Standort-Übersicht anzeigen

**Als** Kunde
**möchte ich** sehen, welcher Foodtruck wann an welchem Standort ist,
**damit** ich den richtigen Standort für meine Reservierung wählen kann.

**Akzeptanzkriterien:**
- [ ] Kalender-Ansicht zeigt Standorte pro Wochentag
- [ ] Für jeden Standort werden Name und Adresse angezeigt
- [ ] Öffnungszeiten pro Standort und Tag sind sichtbar
- [ ] Nur aktive Standorte (active=true) werden angezeigt
- [ ] Vergangene Tage sind ausgegraut/nicht auswählbar
- [ ] Ansicht ist mobile-optimiert (Touch-freundlich)

**Verknüpfungen:**
- Requirements: REQ-001, REQ-004
- API: `GET /api/v1/locations`, `GET /api/v1/locations/{id}/schedule`

**Mockup-Hinweise:**
- Wochenansicht mit Karten pro Standort
- Standort-Karten zeigen: Name, Adresse, Öffnungszeiten
- "Reservieren"-Button pro Standort/Tag

---

### US-002: Reservierung erstellen

**Als** Kunde
**möchte ich** eine Reservierung für Hähnchen und Pommes erstellen,
**damit** mein Essen bei Ankunft bereit ist.

**Akzeptanzkriterien:**
- [ ] Formular mit folgenden Feldern:
  - Kundenname (Pflicht, max. 200 Zeichen)
  - E-Mail (Optional, validiert auf korrektes Format)
  - Anzahl Hähnchen (0-50, Spinner/Dropdown)
  - Anzahl Pommes (0-50, Spinner/Dropdown)
  - Abholzeit (Time Picker, nur innerhalb Öffnungszeiten)
  - Notizen (Optional, max. 500 Zeichen)
- [ ] Mindestens 1 Produkt (Hähnchen oder Pommes) muss ausgewählt sein
- [ ] Abholzeit muss mindestens 30 Minuten in der Zukunft liegen
- [ ] Verfügbare Kapazität wird angezeigt
- [ ] Bei Kapazitätsüberschreitung: Fehlermeldung mit verfügbarer Restkapazität
- [ ] Validierungsfehler werden inline am Feld angezeigt
- [ ] Submit-Button deaktiviert während Verarbeitung (Loading State)

**Verknüpfungen:**
- Requirements: REQ-002, REQ-017, REQ-018, REQ-019
- API: `POST /api/v1/reservations`, `GET /api/v1/locations/{id}/availability`

**Mockup-Hinweise:**
- Mehrstufiges Formular oder Single-Page-Form
- Kapazitätsanzeige als Fortschrittsbalken
- "+"/"-" Buttons für Produktanzahl

---

### US-003: Reservierungsbestätigung erhalten

**Als** Kunde
**möchte ich** nach erfolgreicher Reservierung eine Bestätigung erhalten,
**damit** ich weiß, dass meine Reservierung eingegangen ist.

**Akzeptanzkriterien:**
- [ ] Bestätigungsseite wird nach erfolgreicher Reservierung angezeigt
- [ ] Folgende Infos werden angezeigt:
  - Reservierungs-ID (groß und prominent)
  - Standort mit Adresse
  - Datum und Abholzeit
  - Bestellte Produkte (Anzahl Hähnchen, Pommes)
  - Kundenname
- [ ] Hinweis: "Bitte notieren Sie sich Ihre Reservierungs-ID"
- [ ] Falls E-Mail angegeben: Bestätigungsmail wird versendet
- [ ] Falls keine E-Mail: Deutlicher Hinweis, ID zu notieren
- [ ] "Neue Reservierung"-Button verfügbar
- [ ] Seite kann gedruckt werden (Print-CSS)

**Verknüpfungen:**
- Requirements: REQ-003
- API: Response von `POST /api/v1/reservations`

**Mockup-Hinweise:**
- Grüner Erfolgs-Banner/Checkmark
- Reservierungs-ID in großer Schrift
- QR-Code mit ID (Nice-to-have)

---

### US-004: Verfügbarkeit prüfen

**Als** Kunde
**möchte ich** vor der Reservierung die Verfügbarkeit prüfen,
**damit** ich weiß, ob noch Kapazität vorhanden ist.

**Akzeptanzkriterien:**
- [ ] Verfügbare Kapazität wird pro Standort/Tag angezeigt
- [ ] Ampel-System: Grün (>30%), Gelb (10-30%), Rot (<10%)
- [ ] Ausgebuchte Zeiträume sind nicht auswählbar
- [ ] Echtzeit-Update bei Änderung (Auto-Refresh oder manuell)
- [ ] "Fast ausgebucht"-Badge bei <10% Kapazität

**Verknüpfungen:**
- Requirements: REQ-004, REQ-018
- API: `GET /api/v1/locations/{id}/availability?date=YYYY-MM-DD`

---

### US-005: Reservierung nachschlagen

**Als** Kunde
**möchte ich** meine Reservierung mit der ID nachschlagen können,
**damit** ich die Details meiner Reservierung überprüfen kann.

**Akzeptanzkriterien:**
- [ ] Eingabefeld für Reservierungs-ID auf Startseite
- [ ] Bei gültiger ID: Reservierungsdetails anzeigen
- [ ] Bei ungültiger ID: Fehlermeldung "Reservierung nicht gefunden"
- [ ] Anzeige von: Status, Standort, Abholzeit, Produkte

**Verknüpfungen:**
- Requirements: REQ-003
- API: `GET /api/v1/reservations/{id}`

---

## Epic 2: Mitarbeiter-Verwaltung

**Beschreibung:** Mitarbeiter können Reservierungen ihres Standorts effizient verwalten.

**Business Value:** Echtzeit-Überblick statt Zettelwirtschaft, schnellere Walk-in-Entscheidungen

---

### US-011: Mitarbeiter-Login

**Als** Mitarbeiter
**möchte ich** mich sicher anmelden können,
**damit** ich Zugriff auf die Reservierungsverwaltung erhalte.

**Akzeptanzkriterien:**
- [ ] Login-Formular mit Benutzername und Passwort
- [ ] Fehlermeldung bei falschen Credentials
- [ ] Nach Login: Weiterleitung zum Dashboard
- [ ] Session-Timeout nach 30 Minuten Inaktivität (konfigurierbar)
- [ ] Logout-Button sichtbar
- [ ] Mitarbeiter ist automatisch seinem Standort zugeordnet

**Verknüpfungen:**
- Requirements: REQ-005
- API: `POST /api/v1/auth/login`

**Mockup-Hinweise:**
- Einfaches Login-Formular
- "Angemeldet bleiben"-Checkbox (Phase 2)
- Foodtruck-Logo

---

### US-012: Tagesübersicht anzeigen

**Als** Mitarbeiter
**möchte ich** alle Reservierungen des heutigen Tages für meinen Standort sehen,
**damit** ich den Überblick über ausstehende Bestellungen habe.

**Akzeptanzkriterien:**
- [ ] Liste aller Reservierungen des Tages (Standard: heute)
- [ ] Sortierung nach Abholzeit (aufsteigend)
- [ ] Pro Reservierung anzeigen:
  - Abholzeit (prominent)
  - Kundenname
  - Anzahl Hähnchen / Pommes
  - Status (farbcodiert)
  - Notizen (falls vorhanden)
- [ ] Farbcodierung nach Status:
  - PENDING: Orange/Gelb
  - CONFIRMED: Grün
  - COMPLETED: Grau
  - NO_SHOW: Rot
  - CANCELLED: Durchgestrichen/Grau
- [ ] Filter nach Status möglich
- [ ] Suchfeld für Kundenname
- [ ] Auto-Refresh alle 30 Sekunden
- [ ] Manueller Refresh-Button
- [ ] Pull-to-Refresh auf Tablet

**Verknüpfungen:**
- Requirements: REQ-006
- API: `GET /api/v1/staff/reservations?date=YYYY-MM-DD`

**Mockup-Hinweise:**
- Card-basierte Liste
- Große Touch-Targets für Tablet
- Status als farbiger Badge
- Uhrzeit prominent links

---

### US-013: Reservierung bestätigen

**Als** Mitarbeiter
**möchte ich** eine eingehende Reservierung bestätigen können,
**damit** der Kunde weiß, dass seine Bestellung garantiert ist.

**Akzeptanzkriterien:**
- [ ] "Bestätigen"-Button bei PENDING-Reservierungen
- [ ] Nach Klick: Status wird auf CONFIRMED gesetzt
- [ ] Visuelles Feedback (Toast-Nachricht, Farbwechsel)
- [ ] Optional: Notiz hinzufügen können
- [ ] Kapazitätsprüfung vor Bestätigung (Race Condition vermeiden)
- [ ] Bei nicht ausreichender Kapazität: Fehlermeldung

**Verknüpfungen:**
- Requirements: REQ-007
- API: `PATCH /api/v1/staff/reservations/{id}/status`

**Mockup-Hinweise:**
- Grüner "Bestätigen"-Button
- Swipe-Geste alternativ (Tablet)
- Konfirmations-Animation

---

### US-014: Reservierung stornieren

**Als** Mitarbeiter
**möchte ich** eine Reservierung stornieren können,
**damit** die Kapazität wieder freigegeben wird.

**Akzeptanzkriterien:**
- [ ] "Stornieren"-Button bei PENDING und CONFIRMED Reservierungen
- [ ] Bestätigungsdialog vor Stornierung
- [ ] Optional: Stornierungsgrund eingeben
- [ ] Nach Stornierung: Status wird auf CANCELLED gesetzt
- [ ] Visuelles Feedback
- [ ] Stornierte Reservierungen bleiben sichtbar (durchgestrichen)
- [ ] Kapazität wird wieder freigegeben

**Verknüpfungen:**
- Requirements: REQ-008
- API: `PATCH /api/v1/staff/reservations/{id}/status`

**Mockup-Hinweise:**
- Roter "Stornieren"-Button (sekundär)
- Bestätigungsmodal mit optionalem Grund
- Durchgestrichene Darstellung nach Stornierung

---

### US-015: Reservierung abschließen

**Als** Mitarbeiter
**möchte ich** eine Reservierung als abgeholt oder nicht erschienen markieren,
**damit** der Tagesabschluss korrekt ist.

**Akzeptanzkriterien:**
- [ ] "Abgeschlossen"-Button bei CONFIRMED Reservierungen → COMPLETED
- [ ] "Nicht erschienen"-Button bei CONFIRMED → NO_SHOW
- [ ] Nach Abschluss: Reservierung wird ausgegraut/archiviert
- [ ] NO_SHOW gibt Kapazität wieder frei (für Statistik)
- [ ] Abgeschlossene Reservierungen können nicht mehr geändert werden

**Verknüpfungen:**
- Requirements: REQ-009
- API: `PATCH /api/v1/staff/reservations/{id}/status`

**Mockup-Hinweise:**
- Zwei Buttons: "Abgeholt" (grün), "Nicht erschienen" (rot)
- Ausgegraut nach Abschluss
- Optional: Swipe-Geste

---

### US-016: Kapazitäts-Dashboard

**Als** Mitarbeiter
**möchte ich** die aktuelle Kapazität auf einen Blick sehen,
**damit** ich Walk-in-Kunden schnell beraten kann.

**Akzeptanzkriterien:**
- [ ] Dashboard-Widget zeigt:
  - Gesamtkapazität des Tages
  - Bereits reservierte Menge (PENDING + CONFIRMED)
  - Verfügbare Restkapazität
  - Prozentuale Auslastung
- [ ] Farbliche Kennzeichnung:
  - Grün: >30% verfügbar
  - Gelb: 10-30% verfügbar
  - Rot: <10% verfügbar
- [ ] Echtzeit-Update
- [ ] Große, gut lesbare Zahlen (für schnelle Entscheidung)

**Verknüpfungen:**
- Requirements: REQ-010
- API: `GET /api/v1/staff/capacity?date=YYYY-MM-DD`

**Mockup-Hinweise:**
- Großes Widget oben auf Dashboard
- Donut-Chart oder Fortschrittsbalken
- "Noch X Hähnchen verfügbar" in großer Schrift

---

## Epic 3: Admin-Dashboard

**Beschreibung:** Administrator verwaltet Standorte, Kapazitäten und hat Gesamtüberblick.

**Business Value:** Zentrale Verwaltung, Kapazitätsplanung, Business Intelligence

---

### US-021: Standorte verwalten

**Als** Administrator
**möchte ich** Standorte erstellen und bearbeiten können,
**damit** neue Standorte konfiguriert werden können.

**Akzeptanzkriterien:**
- [ ] Liste aller Standorte (aktiv und inaktiv)
- [ ] Standort erstellen: Name, Adresse
- [ ] Standort bearbeiten: Name, Adresse ändern
- [ ] Standort deaktivieren (Soft Delete)
- [ ] Standort nur löschen wenn keine Reservierungen existieren
- [ ] Validierung: Name eindeutig, max. 200 Zeichen

**Verknüpfungen:**
- Requirements: REQ-012
- API: `POST/PUT/DELETE /api/v1/admin/locations`

---

### US-022: Wochenplan konfigurieren

**Als** Administrator
**möchte ich** den Wochenplan pro Standort konfigurieren,
**damit** Kunden wissen, wann der Foodtruck wo ist.

**Akzeptanzkriterien:**
- [ ] Pro Standort: Wochentage 1-7 konfigurierbar
- [ ] Pro Wochentag:
  - Öffnungszeit
  - Schließzeit
  - Tägliche Kapazität (Hähnchen)
  - Aktiv/Inaktiv-Toggle
- [ ] Validierung: Öffnungszeit < Schließzeit
- [ ] Validierung: Kapazität > 0
- [ ] Visuelle Wochenansicht als Kalender/Tabelle
- [ ] Bulk-Edit: "Diesen Zeitplan auf andere Tage kopieren"

**Verknüpfungen:**
- Requirements: REQ-013, REQ-014
- API: `POST /api/v1/admin/locations/{id}/schedule`

**Mockup-Hinweise:**
- Tabelle: Zeilen = Wochentage, Spalten = Felder
- Toggle pro Zeile für aktiv/inaktiv
- Time Picker für Öffnungszeiten

---

### US-023: Alle Reservierungen einsehen

**Als** Administrator
**möchte ich** alle Reservierungen aller Standorte einsehen können,
**damit** ich einen Gesamtüberblick habe.

**Akzeptanzkriterien:**
- [ ] Liste aller Reservierungen mit Standort-Spalte
- [ ] Filter nach:
  - Standort
  - Status
  - Datumsbereich (von/bis)
- [ ] Suchfunktion: Kundenname, E-Mail, Reservierungs-ID
- [ ] Sortierung nach: Abholzeit, Erstellungsdatum
- [ ] Paginierung (20 pro Seite)
- [ ] Export als CSV

**Verknüpfungen:**
- Requirements: REQ-015
- API: `GET /api/v1/admin/reservations`

---

### US-024: Statistiken einsehen

**Als** Administrator
**möchte ich** Auslastungsstatistiken einsehen,
**damit** ich die Kapazität besser planen kann.

**Akzeptanzkriterien:**
- [ ] Dashboard mit:
  - Gesamtanzahl Reservierungen (Zeitraum wählbar)
  - Auslastung pro Standort (prozentual)
  - Durchschnittliche Bestellgröße
  - No-Show-Rate
  - Stornierungsrate
- [ ] Zeitraumfilter: Letzte 7 Tage, 30 Tage, Custom
- [ ] Diagramme: Balkendiagramm (Reservierungen pro Tag)
- [ ] Vergleich zwischen Standorten

**Verknüpfungen:**
- Requirements: REQ-016
- API: `GET /api/v1/admin/statistics`

**Mockup-Hinweise:**
- Dashboard-Karten mit KPIs
- Balkendiagramm für zeitlichen Verlauf
- Standort-Vergleich als Tabelle

---

## Story Map Übersicht

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              CUSTOMER JOURNEY                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  [Standort wählen] → [Verfügbarkeit] → [Reservieren] → [Bestätigung]       │
│      US-001            US-004           US-002           US-003            │
│                                                                             │
│                                    [Reservierung nachschlagen]              │
│                                           US-005                            │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                              STAFF WORKFLOW                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  [Login] → [Tagesübersicht] → [Bestätigen/Stornieren] → [Abschließen]      │
│  US-011       US-012              US-013/US-014           US-015            │
│                                                                             │
│                     [Kapazität prüfen]                                      │
│                         US-016                                              │
│                                                                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                              ADMIN WORKFLOW                                  │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  [Standorte verwalten] → [Wochenplan] → [Reservierungen] → [Statistiken]   │
│        US-021              US-022          US-023            US-024         │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Priorisierung (MoSCoW)

### Must Have (MVP)

| Story | Begründung |
|-------|------------|
| US-001 | Basis für Standortauswahl |
| US-002 | Kernfunktion: Reservierung erstellen |
| US-003 | Feedback für Kunde |
| US-011 | Zugang für Mitarbeiter |
| US-012 | Kernfunktion: Tagesübersicht |
| US-013 | Bestätigung von Reservierungen |
| US-016 | Walk-in-Entscheidung ermöglichen |

### Should Have (MVP)

| Story | Begründung |
|-------|------------|
| US-004 | Verbesserte UX, Kapazitätsanzeige |
| US-014 | Stornierungsmöglichkeit |
| US-015 | Tagesabschluss |
| US-021 | Standortverwaltung |
| US-022 | Kapazitätskonfiguration |

### Could Have (MVP oder Phase 2)

| Story | Begründung |
|-------|------------|
| US-005 | Nice-to-have für Kunden |
| US-023 | Admin-Übersicht |
| US-024 | Statistiken (Business Value für Planung) |

### Won't Have (Phase 2+)

- Kundenstornierung
- Push-Benachrichtigungen
- Mobile App
- Payment Integration

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initiale User Stories erstellt |

---

## 🔴 Wichtig für LLM

**Diese Datei beschreibt Features aus Nutzersicht.**

**Verwenden zusammen mit:**
- `/docs/02-requirements/functional-requirements.md` (REQ-Mapping)
- `/docs/02-requirements/api-contracts.md` (API für Stories)
- `/docs/00-meta/glossary.md` (Fachbegriffe)

**Bei Implementation:**
- Akzeptanzkriterien als Testfälle verwenden
- API-Endpoints aus api-contracts.md referenzieren
- Mockup-Hinweise für UI-Design beachten

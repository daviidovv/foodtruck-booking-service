# Projekt-Vision: Foodtruck Reservierungssystem

Letzte Aktualisierung: 2026-02-14

## Geschäftsziel

Digitales Reservierungssystem für zwei Foodtrucks an verschiedenen Standorten, das die manuelle, telefonische Reservierungsannahme ersetzt und Mitarbeitern einen Echtzeit-Überblick über verfügbare Kapazitäten ermöglicht.

### Kernproblem

Aktuell müssen Foodtruck-Mitarbeiter:
- Reservierungen telefonisch entgegennehmen
- Diese handschriftlich auf Zettel notieren
- Bei Walk-in-Kunden manuell zählen:
  - Wie viele Reservierungen existieren
  - Wie viele Hähnchen noch am Grill sind
  - Ob ohne Reservierung noch verkauft werden kann

Dies führt zu:
- Zeitaufwand und Fehleranfälligkeit
- Unübersichtlichkeit bei hohem Andrang
- Schwieriger Kapazitätsplanung am Ende des Tages

### Lösung

Ein zweiteiliges System:
1. **Kunden-Website**: Online-Reservierung von Hähnchen und Pommes mit Standortauswahl
2. **Mitarbeiter-Tablet**: Echtzeit-Übersicht aller Reservierungen im jeweiligen Foodtruck

**Besonderheit: Zwei Standorte**
- Es existieren zwei Foodtrucks, die an verschiedenen Wochentagen an unterschiedlichen Standorten sind
- Kunden wählen bei der Reservierung den gewünschten Standort basierend auf dem Wochentag
- Jeder Standort hat eigene Kapazität und eigene Reservierungsliste

Mitarbeiter sehen auf einen Blick:
- Alle Reservierungen des Tages für ihren Standort
- Verfügbare Kapazität am jeweiligen Standort
- Können Reservierungen bestätigen/ablehnen

## Zielgruppe

### Primäre Nutzer

**Endkunden**:
- Personen, die Hähnchen und Pommes im Foodtruck reservieren möchten
- Möchten sicherstellen, dass Essen verfügbar ist
- Bevorzugen Online-Buchung statt Anruf

**Foodtruck-Mitarbeiter**:
- Arbeiten im operativen Tagesbetrieb
- Benötigen schnellen Überblick über Reservierungen
- Nutzen Tablet zur Verwaltung

**Administratoren**:
- Betreiber des Foodtrucks
- Benötigen Übersicht über alle Reservierungen
- Verwalten Systemeinstellungen

### Sekundäre Nutzer

**Walk-in-Kunden** (ohne Reservierung):
- Mitarbeiter müssen schnell prüfen können, ob noch Kapazität vorhanden ist

## Erfolgskriterien

### Funktional
- Reservierungsprozess dauert maximal 2 Minuten
- Mitarbeiter sehen Reservierungen in Echtzeit (< 5 Sekunden Verzögerung)
- Walk-in-Entscheidung kann in unter 30 Sekunden getroffen werden

### Betrieblich
- Reduzierung telefonischer Reservierungen um 80%+
- Keine handschriftlichen Zettel mehr notwendig
- Fehlerrate bei Reservierungen sinkt (kein Verzählen mehr)

### Technisch
- System ist verfügbar: 99% Uptime
- Mobile-optimiert (Tablet + Smartphone)
- DSGVO-konform nach deutschem Recht

## Funktionsumfang

### Kunde (Web-Interface)

**Muss**:
- Standort auswählen für HEUTE (nur Same-Day-Reservierungen)
- Sehen wie viele Hähnchen noch verfügbar sind (Live-Anzeige)
- Reservierung erstellen (Name, optional E-Mail, Anzahl Hähnchen/Pommes, optional Abholzeit)
- Bestätigungscode erhalten (z.B. `HUHN-K4M7`)
- Mit Bestätigungscode Reservierung nachschlagen
- Mit Bestätigungscode Reservierung stornieren (jederzeit möglich)

**Bestätigungsprozess**:
- Reservierung wird AUTOMATISCH bestätigt (solange Vorrat > 0)
- Falls E-Mail angegeben → Bestätigungsmail mit Code
- Immer: Bestätigungscode auf Website anzeigen (zum Aufschreiben/Screenshot)

### Mitarbeiter (Tablet-Interface)

**Muss**:
- **Tagesvorrat eintragen**: Morgens eingeben wie viele Hähnchen heute da sind
- **Vorrat anpassen**: Jederzeit ändern (Lieferung, Ausfall, Nachschub)
- Alle Reservierungen des Tages sehen
- Live-Übersicht: Eingetragener Vorrat vs. Reserviert vs. Verfügbar
- Reservierung als abgeholt markieren (COMPLETED)
- Reservierung als nicht erschienen markieren (NO_SHOW)
- Reservierung stornieren (CANCELLED)
- Nach Abholzeit sortierbar

**Nicht mehr nötig** (wegen Auto-Accept):
- ~~Reservierungen manuell bestätigen~~ (automatisch)

**Nice-to-have**:
- Filter nach Status
- Push-Benachrichtigung bei neuer Reservierung

### Administrator (Web-Interface)

**Muss**:
- Übersicht aller Reservierungen (beide Standorte)
- Standortverwaltung (Wochentags-Zuordnung konfigurieren)
- Öffnungszeiten pro Standort/Wochentag konfigurieren
- Statistiken (Auslastung pro Standort, etc.)
- Systemverwaltung

**Hinweis**: Die tägliche Kapazität (Vorrat) wird NICHT vom Admin vorkonfiguriert, sondern vom Mitarbeiter morgens eingetragen.

## Nicht-Ziele

**Explizit NICHT im Scope**:

### Phase 1 (MVP)
- ❌ Keine Zahlungsabwicklung
  - Zahlung erfolgt bei Abholung im Foodtruck
- ❌ Keine Kundenverwaltung mit Accounts
  - Keine Registrierung/Login für Kunden notwendig
- ❌ Keine Speisekartenverwaltung
  - Nur Hähnchen und Pommes (fest)
- ~~❌ Keine Multi-Foodtruck-Fähigkeit~~
  - ✅ **KORREKTUR**: System unterstützt ZWEI Foodtrucks an verschiedenen Standorten (wochentagabhängig)
- ❌ Keine mobile Apps (iOS/Android)
  - Nur Web-basiert (mobile-optimiert)
- ❌ Keine Benachrichtigungen für Kunden
  - (außer Bestätigungsmail, falls E-Mail-Pflicht)
- ❌ Keine Bewertungen/Reviews
- ❌ Keine Treueprogramme/Rabatte

### Zukünftig (Phase 2+)
- **Admin-Statistik-Dashboard** (Verkaufszahlen, Wetter-Korrelation, Standort-Vergleiche)
- Automatische Kapazitätsberechnung (KI-basiert)
- Integration mit Bestandsverwaltung
- Kundenfeedback-System

## Technische Rahmenbedingungen

### Backend
- Java Spring Boot REST API
- PostgreSQL Datenbank

### Frontend
- Kunden-Website: Responsive Web (Mobile First)
- Mitarbeiter-Interface: Tablet-optimiert (Web-basiert)

### Rechtliches
- DSGVO-konform
- Einhaltung deutscher Internetgesetze
- Datenschutzerklärung
- Impressum

## Entschiedene Punkte (Stand: 2026-02-14)

### 1. Identifikation der Kunden ✅
**Entscheidung**: Beides möglich - E-Mail OPTIONAL

- **Mit E-Mail**: Bestätigungsmail wird versendet
- **Nur Name**: Bestätigungscode wird angezeigt (z.B. `HUHN-K4M7`)
- Kunde kann mit Bestätigungscode seine Reservierung online nachschlagen und stornieren

### 2. Kapazitätsmanagement ✅
**Entscheidung**: Täglicher Vorrat durch Mitarbeiter

- Mitarbeiter trägt morgens ein: "Heute haben wir X Hähnchen"
- Kann jederzeit angepasst werden (Lieferung, Ausfall)
- Live-Anzeige für Kunden: "Noch X Hähnchen verfügbar"
- Reservierungen werden automatisch akzeptiert solange Vorrat > 0
- Vorrat wird sofort bei Reservierung reduziert

### 3. Stornierungen ✅
**Entscheidung**: Jederzeit möglich

- Kunde kann jederzeit stornieren (über Bestätigungscode)
- Keine Stornierungsfrist
- Bei Stornierung wird Vorrat wieder freigegeben

### 4. Reservierungsfenster ✅
**Entscheidung**: Nur am gleichen Tag

- Reservierungen nur für den aktuellen Tag möglich
- Keine Vorausreservierung (keine "morgen" Buchungen)

### 5. Abholzeit ✅
**Entscheidung**: Optional

- Kunde kann Abholzeit angeben, muss aber nicht
- Ohne Abholzeit: "Ich komme wann ich will"
- Foodtruck ist meist bis ca. 17:30 da (nicht kritisch für System)

### 6. Workflow ✅
**Entscheidung**: Automatische Bestätigung

- Reservierung wird sofort CONFIRMED (kein PENDING-Status mehr im Kundenprozess)
- Solange Vorrat > 0 → automatisch akzeptiert
- Kein manuelles Bestätigen durch Mitarbeiter nötig

## Zeitrahmen

### MVP (Minimum Viable Product)
**Ziel**: Q2 2026

**Must-have für MVP**:
- Kunden können online reservieren
- Mitarbeiter sehen Reservierungen auf Tablet
- Administrator-Übersicht
- DSGVO-konform

**Erfolg bedeutet**:
- System läuft stabil im Tagesbetrieb
- Mitarbeiter nutzen es statt Telefon+Zettel
- Kunden nehmen Angebot an

### Phase 2
**Zeitpunkt**: Nach erfolgreichem MVP-Betrieb (Q3-Q4 2026)

**Geplante Erweiterungen**:

#### Admin-Statistik-Dashboard (TASK-007)
Umfassende Übersicht für Betreiber mit:
- **Verkaufsstatistiken**: Wie viele Hähnchen wurden wann und wo verkauft
- **Auslastung**: Vorrat vs. Verkauft in Prozent pro Standort
- **Wetter-Korrelation**: Automatische Wetter-Speicherung via OpenWeatherMap API
- **Zeiträume**: Tag/Woche/Monat + freie Datumsauswahl
- **Zugriff**: Nur Admin

**Entschiedene Details:**
- Kein Umsatz-Tracking (nur Mengen)
- Wetter automatisch via API (Koordinaten pro Standort)
- Kein Export (nur Dashboard-Anzeige)

**Weitere mögliche Erweiterungen**:
- Erweiterte Kapazitätsplanung
- Push-Benachrichtigungen für Mitarbeiter
- Multi-Standort (falls weiterer Foodtruck hinzukommt)

## Stakeholder

**Primär**:
- Foodtruck-Betreiber (Entscheidungsträger)
- Foodtruck-Mitarbeiter (tägliche Nutzer)

**Sekundär**:
- Endkunden (Reservierende)

## Risiken

### Technisch
- ❗ Tablet muss im Foodtruck Internet-Zugang haben (WLAN/Mobilfunk)
- ❗ System muss auch bei schlechter Verbindung nutzbar sein

### Betrieblich
- ❗ Mitarbeiter-Schulung notwendig
- ❗ Parallelbetrieb Telefon+System während Einführung

### Rechtlich
- ❗ DSGVO-Anforderungen (Datenschutzerklärung, Einwilligungen)
- ❗ Impressumspflicht
- ❗ Informationspflichten bei personenbezogenen Daten

## Abgrenzung zu Wettbewerbern

**Unterschied zu Standard-Reservierungssystemen**:
- ✅ Spezialisiert auf Foodtruck-Szenario (nicht Restaurant)
- ✅ Fokus auf Kapazitätsmanagement (begrenzte Hähnchen)
- ✅ Einfache, schnelle Bedienung (keine Accounts notwendig)
- ✅ Walk-in-Unterstützung (wichtig für Foodtruck)

**Kein klassisches Restaurant-Reservierungssystem**:
- Keine Tischreservierungen
- Keine Menükarten
- Keine Kellner-Verwaltung
- Reine Abholung (Take-away)

---

## Standort-Konzept

### Wochentagsbasierte Standortzuordnung

**Foodtruck 1**:
- Standort A (z.B. Innenstadt): Montag, Mittwoch, Freitag
- Standort B (z.B. Gewerbegebiet): Dienstag, Donnerstag

**Foodtruck 2**:
- Standort B: Montag, Mittwoch
- Standort A: Dienstag, Donnerstag, Samstag

(Genaue Zuordnung durch Administrator konfigurierbar)

### Technische Implikationen

- Reservierungen sind standortbezogen, nicht foodtruckbezogen
- Kunde wählt Standort für HEUTE (nur Same-Day)
- **Täglicher Vorrat** wird vom Mitarbeiter eingetragen (nicht vorkonfiguriert)
- **Bestätigungscode** (6-8 Zeichen) für jede Reservierung
- **Auto-Accept**: Reservierung sofort CONFIRMED wenn Vorrat > 0
- **Live-Verfügbarkeit**: Eingetragener Vorrat - Reservierte Menge = Verfügbar
- Mitarbeiter-Login erfolgt pro Standort

---

## Zusammenfassung

**In einem Satz**: Online-Reservierungssystem für zwei Foodtrucks mit Live-Verfügbarkeitsanzeige und automatischer Bestätigung, das telefonische Bestellungen und Zettelwirtschaft ersetzt.

**Hauptnutzen**:
- **Kunden**: Schnelle Same-Day-Reservierung, sehen live wie viele Hähnchen noch da sind, Bestätigungscode für Verwaltung/Stornierung
- **Mitarbeiter**: Morgens Vorrat eintragen, Live-Übersicht aller Reservierungen, kein manuelles Bestätigen nötig
- **Betreiber**: Bessere Übersicht, weniger Telefonanrufe, automatisierter Prozess

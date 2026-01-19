# Projekt-Vision: Foodtruck Reservierungssystem

Letzte Aktualisierung: 2026-01-19

## Geschäftsziel

Digitales Reservierungssystem für einen Foodtruck, das die manuelle, telefonische Reservierungsannahme ersetzt und Mitarbeitern einen Echtzeit-Überblick über verfügbare Kapazitäten ermöglicht.

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
1. **Kunden-Website**: Online-Reservierung von Hähnchen und Pommes
2. **Mitarbeiter-Tablet**: Echtzeit-Übersicht aller Reservierungen im Foodtruck

Mitarbeiter sehen auf einen Blick:
- Alle Reservierungen des Tages
- Verfügbare Kapazität
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
- Reservierung erstellen (Anzahl Hähnchen, Pommes, Abholzeit)
- Reservierungsbestätigung erhalten

**Optional** (Design-Entscheidung offen):
- E-Mail-basierte Reservierung (mit Bestätigungsmail)
- ODER Name-basierte Reservierung (ohne E-Mail, aber schwierige Bestätigung)

### Mitarbeiter (Tablet-Interface)

**Muss**:
- Alle Reservierungen des Tages sehen
- Reservierungen bestätigen/ablehnen
- Verfügbare Kapazität auf einen Blick
- Nach Abholzeit sortierbar

**Nice-to-have**:
- Filter nach Status (bestätigt, ausstehend, abgeschlossen)
- Push-Benachrichtigung bei neuer Reservierung

### Administrator (Web-Interface)

**Muss**:
- Übersicht aller Reservierungen
- Statistiken (Auslastung, etc.)
- Systemverwaltung

## Nicht-Ziele

**Explizit NICHT im Scope**:

### Phase 1 (MVP)
- ❌ Keine Zahlungsabwicklung
  - Zahlung erfolgt bei Abholung im Foodtruck
- ❌ Keine Kundenverwaltung mit Accounts
  - Keine Registrierung/Login für Kunden notwendig
- ❌ Keine Speisekartenverwaltung
  - Nur Hähnchen und Pommes (fest)
- ❌ Keine Multi-Foodtruck-Fähigkeit
  - System nur für EINEN Foodtruck
- ❌ Keine mobile Apps (iOS/Android)
  - Nur Web-basiert (mobile-optimiert)
- ❌ Keine Benachrichtigungen für Kunden
  - (außer Bestätigungsmail, falls E-Mail-Pflicht)
- ❌ Keine Bewertungen/Reviews
- ❌ Keine Treueprogramme/Rabatte

### Zukünftig (Phase 2+)
- Automatische Kapazitätsberechnung (KI-basiert)
- Integration mit Bestandsverwaltung
- Kundenfeedback-System
- Analytics Dashboard

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

## Offene Fragen

Diese Fragen müssen vor der Implementierung geklärt werden:

### 1. Identifikation der Kunden
**Frage**: E-Mail-Pflicht oder nur Name?

**Option A: E-Mail erforderlich**
- ✅ Pro: Bestätigungsmail möglich, eindeutige Identifikation
- ❌ Contra: Höhere Hürde für Kunden

**Option B: Nur Name**
- ✅ Pro: Schnellere Reservierung, niedrigere Hürde
- ❌ Contra: Keine Bestätigungsmail, Dubletten-Problem bei gleichem Namen

**Empfehlung**: Sollte mit Betreiber geklärt werden basierend auf Zielgruppe

### 2. Kapazitätsmanagement
**Frage**: Wie wird tägliche Kapazität festgelegt?

**Optionen**:
- Administrator gibt täglich Anzahl verfügbarer Hähnchen ein
- System hat Standardwert, überschreibbar
- Automatische Berechnung basierend auf historischen Daten

### 3. Stornierungen
**Frage**: Können Kunden stornieren?

**Zu klären**:
- Stornierung möglich? Bis wann?
- Mit/ohne E-Mail schwierig bei Name-only-Reservierungen

### 4. Reservierungsfenster
**Frage**: Wie lange im Voraus kann reserviert werden?

**Zu klären**:
- Nur am gleichen Tag?
- Bis zu X Tage im Voraus?
- Cut-off-Zeit (z.B. keine Reservierung < 1 Stunde vor Abholung)?

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

**Mögliche Erweiterungen**:
- Analytics/Reporting
- Erweiterte Kapazitätsplanung
- Benachrichtigungen
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

## Zusammenfassung

**In einem Satz**: Online-Reservierungssystem für einen Foodtruck, das telefonische Bestellungen und Zettelwirtschaft durch digitales Kapazitätsmanagement ersetzt.

**Hauptnutzen**:
- Kunden: Bequeme Online-Reservierung
- Mitarbeiter: Echtzeit-Überblick statt Zettel-Chaos
- Betreiber: Bessere Kapazitätsplanung und Übersicht

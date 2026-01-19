# Prompt Templates

Wiederverwendbare Vorlagen für Standard-Tasks.

## Template: Feature Implementation

```markdown
# Feature: [Feature-Name]

## Kontext
- Aktueller Stand: [Beschreibung]
- Requirement: [REQ-ID] aus /docs/02-requirements/functional-requirements.md
- User Story: [US-ID] aus /docs/02-requirements/user-stories.md

## Ziel
Implementiere [konkrete Beschreibung] mit folgenden Komponenten:
- [ ] Entity/Repository (falls nötig)
- [ ] Service-Layer
- [ ] Controller
- [ ] DTOs
- [ ] Validation
- [ ] Tests

## Technische Details
**Package**: org.example.foodtruckbookingservice.[subpackage]
**Betroffene Dateien**:
- [Liste der zu erstellenden/ändernden Dateien]

## API-Contract
```json
[Aus /docs/02-requirements/api-contracts.md kopieren]
```

## Akzeptanzkriterien
- [ ] [Kriterium 1]
- [ ] [Kriterium 2]
- [ ] Alle Tests grün
- [ ] Code-Coverage >= 80%

## Mandatory Context
Siehe /docs/03-prompts/mandatory-context.md

## Forbidden Actions
Siehe /docs/03-prompts/forbidden-actions.md

## Definition of Done
Siehe /docs/05-quality/definition-of-done.md
```

---

## Template: Bugfix

```markdown
# Bugfix: [Kurze Beschreibung]

## Problem
**Symptom**: [Was geht schief]
**Reproduktion**:
1. [Schritt 1]
2. [Schritt 2]
3. [Fehler tritt auf]

**Erwartetes Verhalten**: [Was sollte passieren]
**Aktuelles Verhalten**: [Was passiert stattdessen]

## Root Cause
[Falls bekannt, sonst "Analyse erforderlich"]

## Betroffene Komponenten
- Klasse: [ClassName]
- Methode: [methodName]
- Zeile: [ca. Zeilennummer]

## Fix-Strategie
[Beschreibung der Lösung]

## Tests
- [ ] Reproduzier-Test schreiben (schlägt vor Fix fehl)
- [ ] Test muss nach Fix grün sein
- [ ] Regression-Tests für ähnliche Szenarien
- [ ] Bestehende Tests dürfen nicht brechen

## Mandatory Context
Siehe /docs/03-prompts/mandatory-context.md
```

---

## Template: Database Migration

```markdown
# Database Migration: [Beschreibung]

## Ziel
[Was soll in der DB geändert werden]

## Änderungen
- [ ] Neue Tabelle: [Name]
- [ ] Neue Spalte in [Tabelle]: [Spaltenname]
- [ ] Index auf [Tabelle].[Spalte]
- [ ] Constraint: [Beschreibung]

## Migration
**Datei**: `src/main/resources/db/migration/V[X]__[description].sql`
**Vorgänger**: V[X-1]__[previous].sql

## Rückwärtskompatibilität
- [ ] Bestehende Daten migrieren
- [ ] Keine Breaking Changes für laufende App-Instanzen
- [ ] Rollback-Strategie dokumentiert

## Entity-Änderungen
**Betroffene Entity**: [ClassName]
**Änderungen**: [Felder hinzufügen/ändern]

## Tests
- [ ] Flyway-Migration läuft erfolgreich durch
- [ ] Testcontainer-Tests mit neuer Migration
- [ ] Bestehende Entity-Tests anpassen

## Constraints
- PostgreSQL 16 Syntax
- Keine proprietären Features
- Performance-Impact bei großen Datenmengen bedenken
```

---

## Template: Refactoring

```markdown
# Refactoring: [Komponente]

## Motivation
[Warum ist Refactoring notwendig]

## Scope
**Betroffene Dateien**:
- [Datei 1]
- [Datei 2]

## Ziel
[Was soll verbessert werden]
- [ ] Code-Duplikation reduzieren
- [ ] Lesbarkeit verbessern
- [ ] Performance optimieren
- [ ] [Sonstiges]

## Constraints
**Darf sich NICHT ändern**:
- Public APIs
- Datenbankschema
- Externe Schnittstellen
- Verhalten aus Nutzersicht

## Strategie
[Beschreibung der Refactoring-Schritte]

## Tests
- [ ] Alle bestehenden Tests müssen grün bleiben
- [ ] Keine neuen Tests notwendig (nur bei neuem Verhalten)
- [ ] Test-Coverage darf nicht sinken

## Forbidden
Siehe /docs/03-prompts/forbidden-actions.md
- Insbesondere: Keine Breaking Changes ohne Freigabe
```

---

## Template: Test Implementation

```markdown
# Tests für: [Komponente]

## Scope
**Zu testende Klasse**: [ClassName]
**Testtyp**: [Unit / Integration / E2E]

## Test-Szenarien
### Happy Path
- [ ] [Szenario 1]
- [ ] [Szenario 2]

### Edge Cases
- [ ] Leere Eingabe
- [ ] Null-Werte
- [ ] Grenzwerte

### Error Cases
- [ ] [Fehlerfall 1]
- [ ] [Fehlerfall 2]

## Test-Setup
**Framework**: JUnit 5
**Mocking**: Mockito (falls nötig)
**Testcontainers**: Ja/Nein

## Akzeptanzkriterien
- [ ] Test-Coverage >= 80%
- [ ] Alle Tests grün
- [ ] Keine flaky Tests
- [ ] Aussagekräftige Test-Namen
- [ ] Arrange-Act-Assert-Pattern

## Constraints
- Tests müssen schnell laufen (< 5 Sek pro Unit-Test)
- Integration-Tests mit Testcontainers isoliert
```

---

## Template: Architecture Decision

```markdown
# Architecture Decision: [Thema]

## Problem
[Welche Fragestellung muss beantwortet werden]

## Kontext
[Relevante Hintergrundinformationen]

## Betrachtete Optionen
1. **Option A**: [Beschreibung]
   - Pro: [Vorteile]
   - Contra: [Nachteile]

2. **Option B**: [Beschreibung]
   - Pro: [Vorteile]
   - Contra: [Nachteile]

3. **Option C**: [Beschreibung]
   - Pro: [Vorteile]
   - Contra: [Nachteile]

## Entscheidungskriterien
- [Kriterium 1]
- [Kriterium 2]
- [Kriterium 3]

## Empfehlung
[Falls vorhanden]

## Output
Erstelle ADR nach /docs/06-decisions/adr-template.md
```

---

## Nutzung der Templates

1. Template kopieren
2. Platzhalter [in Klammern] ersetzen
3. Relevante Sections mit Kontext füllen
4. Mandatory/Forbidden-Dateien referenzieren
5. Prompt an LLM senden

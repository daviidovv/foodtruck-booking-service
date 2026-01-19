# Functional Requirements

## Zweck dieser Datei
Definiert WAS das System können muss (nicht WIE). Enthält alle funktionalen Anforderungen.

## Verwendung
- **Wann ausfüllen**: Zu Projektbeginn + bei neuen Features
- **Wann referenzieren**: Bei jeder Feature-Implementierung
- **Wann aktualisieren**: Bei neuen Features oder geänderten Anforderungen

## Was du mit dem LLM machst
```
Prompt: "Schreibe funktionale Requirements in /docs/02-requirements/functional-requirements.md für folgende Features:

Feature 1: [Beschreibung was das System können soll]
Feature 2: [...]

Nutze das Format mit REQ-IDs."
```

## Format

Jedes Requirement bekommt eine eindeutige ID: **REQ-001**, **REQ-002**, etc.

```markdown
### REQ-XXX: [Titel]
**Priorität**: Hoch / Mittel / Niedrig
**Beschreibung**: Was muss das System können?

**Akzeptanzkriterien**:
- Kriterium 1
- Kriterium 2
- etc.
```

## Struktur

Gruppiere Requirements nach Features/Modulen:

```markdown
## [Feature-Gruppe] (z.B. Benutzerverwaltung, Buchungssystem)

### REQ-001: [Funktionalität]
**Priorität**: Hoch
**Beschreibung**: Das System muss...

**Akzeptanzkriterien**:
- [ ] Kriterium 1
- [ ] Kriterium 2

### REQ-002: [Funktionalität]
...
```

## Tipps für gute Requirements

✅ **Gut**:
- Konkret und messbar
- Fokus auf WAS, nicht WIE
- Akzeptanzkriterien sind überprüfbar
- Unabhängig von Technologie

❌ **Schlecht**:
- Vage ("Das System sollte schnell sein")
- Technologie-fokussiert ("Nutze Redis für...")
- Nicht testbar

**Beispiel Gut**:
```markdown
### REQ-001: Benutzer registrieren
**Priorität**: Hoch
**Beschreibung**: Ein neuer Benutzer muss sich mit E-Mail und Passwort registrieren können.

**Akzeptanzkriterien**:
- E-Mail-Format wird validiert
- Passwort muss mindestens 8 Zeichen haben
- Bestätigungs-E-Mail wird versendet
- Doppelte E-Mails werden abgelehnt
```

## Versionierung

Bei Änderungen:
- Alte Version durchstreichen, aber nicht löschen
- Neue Version darunter mit Datum
- Begründung angeben

```markdown
### REQ-001: Titel
~~**Alt** (bis 2026-01-14): Alte Beschreibung~~

**Neu** (ab 2026-01-15): Neue Beschreibung
**Grund**: [Warum geändert]
```

## Verknüpfungen

Referenziere verwandte Dokumente:
- User Stories: US-XXX
- ADRs: ADR-XXX
- Tasks: TASK-XXX

---

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Features implementiert werden
- User Stories geschrieben werden
- Tests erstellt werden
- API-Design gemacht wird

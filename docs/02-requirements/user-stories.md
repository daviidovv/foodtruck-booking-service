# User Stories

## Zweck dieser Datei
Beschreibt Features aus Nutzersicht. Format: "Als [Rolle] möchte ich [Funktionalität], damit [Nutzen]"

## Verwendung
- **Wann ausfüllen**: Bei Feature-Planung, vor der Implementierung
- **Wann referenzieren**: Bei Feature-Implementierung, Task-Erstellung
- **Wann aktualisieren**: Bei neuen Features

## Was du mit dem LLM machst
```
Prompt: "Schreibe User Stories in /docs/02-requirements/user-stories.md:

Feature: [Beschreibung]
Rollen: [Benutzer, Admin, ...]
Was soll möglich sein: [...]"
```

## Format

```markdown
### US-XXX: [Titel]
**Als** [Rolle]
**möchte ich** [Funktionalität]
**damit** [Nutzen/Ziel]

**Akzeptanzkriterien**:
- [ ] Kriterium 1
- [ ] Kriterium 2

**Verknüpfungen**:
- Requirements: REQ-XXX, REQ-YYY
- Tasks: TASK-XXX
```

## Struktur

Gruppiere nach Epics (große Feature-Gruppen):

```markdown
## Epic: [Name des großen Features]

### US-001: [Story-Titel]
**Als** Benutzer
**möchte ich** mich registrieren können
**damit** ich das System nutzen kann

**Akzeptanzkriterien**:
- [ ] E-Mail und Passwort-Eingabe möglich
- [ ] Bestätigungs-E-Mail wird versendet
- [ ] Nach Registrierung automatisch eingeloggt

**Verknüpfungen**:
- Requirements: REQ-001, REQ-002
```

## Tipps für gute User Stories

✅ **Gut**:
- Fokus auf Nutzer-Nutzen
- Verständlich für Nicht-Techniker
- Klein genug für eine Iteration
- Testbar (Akzeptanzkriterien klar)

❌ **Schlecht**:
- Technische Details ("Als Entwickler möchte ich Redis verwenden...")
- Zu groß/vage
- Keine Akzeptanzkriterien

## Rollen-Beispiele

Definiere zuerst deine Rollen:
- **Endbenutzer**: Normale Nutzer der Anwendung
- **Administrator**: Verwaltung und Konfiguration
- **System**: Automatisierte Prozesse
- [Deine spezifischen Rollen]

## INVEST-Kriterien

Gute User Stories sind:
- **I**ndependent: Unabhängig von anderen
- **N**egotiable: Details verhandelbar
- **V**aluable: Wert für Nutzer
- **E**stimable: Aufwand schätzbar
- **S**mall: Klein genug
- **T**estable: Testbare Akzeptanzkriterien

---

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Features implementiert werden
- Tasks erstellt werden
- Akzeptanzkriterien geprüft werden
- Fachliche Diskussionen stattfinden

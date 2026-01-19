# Session Start Guide

## Zweck dieser Datei
Quick-Start für jede neue LLM-Session. Spart dir Zeit beim Kontext-Aufbau.

## Verwendung

### Zu Beginn JEDER neuen Session:

Kopiere diesen Prompt und passe den "Aktuellen Stand" an:

```
Hi! Wir arbeiten an folgendem Projekt:

**Projekt**: [Projektname]
**Typ**: [Java Spring Boot REST API / ...]
**Ziel**: [1-Satz-Beschreibung aus vision.md]

**Kontext-Dateien für dich**:
1. /docs/00-meta/vision.md - Projektvision
2. /docs/03-prompts/mandatory-context.md - Technischer Kontext (IMMER bei Code)
3. /docs/03-prompts/forbidden-actions.md - Was du nicht tun darfst

**Aktueller Stand**: [Aktualisiere das regelmäßig!]
- [x] Projekt-Setup fertig
- [x] Datenbank läuft
- [ ] API-Implementation in Arbeit
- [ ] Tests werden geschrieben

**Nächster Task**: TASK-XXX ([Kurzbeschreibung])
```

---

## Wartung

### Aktualisiere "Aktuellen Stand" regelmäßig!

**Nach jedem Major Feature**:
- Was ist jetzt fertig?
- Was ist der nächste Schritt?

**Beispiel**:
```markdown
**Aktueller Stand** (2026-01-20):
- [x] Booking-API vollständig implementiert
- [x] Unit-Tests für Service-Layer
- [ ] Integration-Tests ausstehend
- [ ] Security-Konfiguration TODO

**Nächster Task**: TASK-005 - Integration Tests für Booking API
```

---

## Alternative: Projekt-Snapshot

Wenn du nur 1-2 Dateien schicken willst:

```
Prompt: "Hier ist unser Projekt-Context:

**Vision**: [Aus vision.md]
**Tech-Stack**: [Aus mandatory-context.md]
**Aktuelle Aufgabe**: [Task-Beschreibung]

Bei Code-Generierung referenziere:
- /docs/03-prompts/mandatory-context.md
- /docs/03-prompts/forbidden-actions.md
```

---

## Tipp: Session-Wiederaufnahme

Wenn eine Session unterbrochen wurde:

```
Prompt: "Wir arbeiten weiter an [Projektname].

Letzter Stand der Session:
- Feature [X] wurde implementiert
- Jetzt arbeiten wir an [Y]

Kontext siehe:
- /docs/03-prompts/mandatory-context.md
- /docs/04-tasks/active/TASK-XXX-[name].md"
```

---

## 🔴 Best Practice

Statt JEDES Mal alles zu erklären:
1. Diese Datei aktuell halten
2. Zu Beginn jeder Session den Quick-Start-Prompt verwenden
3. Bei Code-Generation IMMER mandatory-context.md + forbidden-actions.md referenzieren

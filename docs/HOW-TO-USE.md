# HOW TO USE: Dokumentationsstruktur

## Das Wichtigste

**Claude Code liest `CLAUDE.md` im Root automatisch.** Das ist dein Einstiegspunkt.

Du musst nicht mehr manuell Dateien mitgeben - sag einfach:
- "Erstelle Task für X" → Claude fragt nach, erstellt Task
- "Implementiere TASK-XXX" → Claude liest Kontext, implementiert, testet

## Wann welche Docs relevant sind

### Immer automatisch (CLAUDE.md)
- Projekt-Überblick
- Workflows
- Wichtigste Regeln
- Befehle

### Bei Bedarf (Referenz)
| Situation | Datei |
|-----------|-------|
| Tech-Stack Details | `docs/03-prompts/mandatory-context.md` |
| Was ist verboten | `docs/03-prompts/forbidden-actions.md` |
| Task fertig? | `docs/05-quality/definition-of-done.md` |
| Coding Details | `docs/05-quality/code-standards.md` |
| API Spezifikation | `docs/02-requirements/api-contracts.md` |
| DB Schema | `docs/01-architecture/data-model.md` |

## Workflow-Beispiele

### Neues Feature

```
Du: "Erstelle Task für Suchfunktion bei Reservierungen"
Claude: Fragt nach Details → Erstellt Task in docs/04-tasks/backlog/

Du: "Implementiere TASK-014"
Claude: Liest Task → Implementiert → Schreibt Tests → Führt mvn test aus → Testet App
```

### Architekturentscheidung

```
Du: "Wir müssen entscheiden ob wir Redis für Caching nutzen"
Claude: Erstellt ADR in docs/06-decisions/ mit Optionen und Empfehlung
```

## Wartung

**Nach größeren Änderungen:**
- `mandatory-context.md` aktualisieren wenn sich Tech-Stack ändert
- `forbidden-actions.md` erweitern wenn neue Probleme auftreten

**Regelmäßig:**
- Abgeschlossene Tasks nach `completed/` verschieben (macht Claude automatisch wenn du sagst "Task ist fertig")

---

Letzte Aktualisierung: 2026-02-16

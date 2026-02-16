# Projekt-Dokumentation

## Einstiegspunkt

**Für die Entwicklung mit Claude Code: lies `CLAUDE.md` im Root-Verzeichnis.**

Die CLAUDE.md wird automatisch gelesen und enthält alle Workflows und wichtigen Informationen.

## Ordnerstruktur

```
/docs
├── /00-meta              # Projekt-Vision, Glossar, Changelog
├── /01-architecture      # Systemdesign, Tech-Stack, Datenmodell
├── /02-requirements      # Anforderungen, User Stories, API-Contracts
├── /03-prompts           # Tech-Kontext, Verbote (Referenz-Docs)
├── /04-tasks             # Task-Management (active/, backlog/, completed/)
├── /05-quality           # Code-Standards, Definition of Done
└── /06-decisions         # Architecture Decision Records (ADRs)
```

## Wichtige Referenz-Dateien

| Datei | Inhalt |
|-------|--------|
| `03-prompts/mandatory-context.md` | Tech-Stack, Package-Struktur, Coding-Constraints |
| `03-prompts/forbidden-actions.md` | Was ist verboten (Anti-Patterns, Architektur-Regeln) |
| `05-quality/definition-of-done.md` | Wann ist ein Task fertig |
| `05-quality/code-standards.md` | Detaillierte Coding-Conventions |

## Workflows

Alle Workflows sind in `CLAUDE.md` dokumentiert:
- Task erstellen
- Task implementieren
- ADR erstellen

---

Letzte Aktualisierung: 2026-02-16

# TASK-013: Dokumentations-Workflow Optimierung

## Status
- [x] Abgeschlossen (2026-02-16)

## Ziel

Dokumentationsstruktur konsolidieren und CLAUDE.md als zentrale Steuerung für den Entwicklungs-Workflow optimieren.

## Akzeptanzkriterien

### Konsolidierung
- [ ] `mandatory-context.md` und `tech-stack.md` zusammenführen (Duplikate entfernen)
- [ ] `prompt-guidelines.md` und `prompt-templates.md` evaluieren - wenn obsolet durch CLAUDE.md → entfernen oder in CLAUDE.md integrieren
- [ ] `session-start.md` obsolet durch CLAUDE.md? → entfernen wenn ja
- [ ] `HOW-TO-USE.md` anpassen auf neuen CLAUDE.md-basierten Workflow
- [ ] Leere/ungenutzte Dateien markieren oder entfernen

### CLAUDE.md Verbesserung
- [ ] Templates (task, adr, review) in CLAUDE.md integrieren statt separate Dateien
- [ ] Kompaktes Task-Format definieren (Ziel + Akzeptanzkriterien + Offene Fragen)
- [ ] Klare Anweisungen welche Docs bei welchem Workflow gelesen werden sollen
- [ ] Aktuelle Projekt-Status-Sektion hinzufügen

### Qualitätsdocs
- [ ] `definition-of-done.md`, `code-standards.md`, `anti-patterns.md`, `review-checklist.md` auf Aktualität prüfen
- [ ] Redundanzen zwischen diesen Dateien entfernen

### Ergebnis
- [ ] Weniger Dateien, klarere Struktur
- [ ] CLAUDE.md ist die einzige Datei die man kennen muss
- [ ] Workflow: "Erstelle Task" und "Implementiere Task" funktionieren reibungslos

## Entscheidungen

- Abgeschlossene Tasks (TASK-001 bis TASK-009) → nach `completed/` verschieben
- `docs/README.md` → behalten und vereinfachen (verweist auf CLAUDE.md)

## Betroffene Dateien

**Potenziell zu entfernen/konsolidieren:**
- `docs/03-prompts/prompt-guidelines.md`
- `docs/03-prompts/prompt-templates.md`
- `docs/00-meta/session-start.md`
- `docs/01-architecture/tech-stack.md` (→ merge in mandatory-context.md)

**Zu aktualisieren:**
- `CLAUDE.md`
- `docs/03-prompts/mandatory-context.md`
- `docs/HOW-TO-USE.md`
- `docs/04-tasks/task-template.md` (oder entfernen)

**Unverändert lassen:**
- `docs/02-requirements/*` (Requirements bleiben)
- `docs/01-architecture/data-model.md`, `system-overview.md`
- `docs/06-decisions/*` (ADRs bleiben)
- `docs/05-quality/*` (nur prüfen, nicht großartig ändern)

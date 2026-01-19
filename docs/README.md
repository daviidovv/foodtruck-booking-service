# Projekt-Dokumentation

Diese Dokumentationsstruktur ist speziell für LLM-gestützte Softwareentwicklung konzipiert.

## Überblick

Die Dokumentation ist in thematische Ordner strukturiert, die den gesamten Entwicklungszyklus abdecken.

## Ordnerstruktur

```
/docs
├── /00-meta              # Projekt-Metadaten
├── /01-architecture      # Architektur & Systemdesign
├── /02-requirements      # Anforderungen & User Stories
├── /03-prompts          # LLM-Prompting-Guidelines
├── /04-tasks            # Task-Management (aktiv/abgeschlossen)
├── /05-quality          # Qualitätsstandards & Checklisten
├── /06-decisions        # Architecture Decision Records
└── /07-reviews          # Code & Architektur Reviews
```

## Verwendung

### Für neue Features

1. **Requirement definieren**: Eintrag in `/02-requirements/`
2. **Task erstellen**: Kopiere `/04-tasks/task-template.md` nach `/04-tasks/active/`
3. **Prompt vorbereiten**: Nutze Templates aus `/03-prompts/prompt-templates.md`
4. **Mandatory Context einbinden**: Immer `/03-prompts/mandatory-context.md` und `/03-prompts/forbidden-actions.md` referenzieren
5. **Implementierung**: Mit LLM arbeiten
6. **Quality Check**: Checklisten aus `/05-quality/` durchgehen
7. **Task abschließen**: Nach `/04-tasks/completed/` verschieben

### Für Architekturentscheidungen

1. **ADR erstellen**: Template aus `/06-decisions/adr-template.md` nutzen
2. **Nummerierung**: Fortlaufend nummerieren (ADR-001, ADR-002, ...)
3. **Verlinken**: In `/01-architecture/architecture-decisions.md` eintragen

### Für Code-Reviews

1. **Review durchführen**: Template aus `/07-reviews/review-template.md` nutzen
2. **Speichern**: Als `YYYY-MM-DD-[komponente]-review.md`
3. **Action Items**: In Tasks überführen

## Wichtige Dateien für jeden LLM-Prompt

Diese Dateien sollten bei JEDEM Code-generierenden Prompt referenziert werden:

1. `/docs/03-prompts/mandatory-context.md` - Pflichtkontext
2. `/docs/03-prompts/forbidden-actions.md` - Verbote
3. `/docs/05-quality/definition-of-done.md` - Fertigstellungskriterien
4. Relevante Requirements aus `/docs/02-requirements/`

## Wartung

### Regelmäßige Updates

**Wöchentlich**:
- `/00-meta/changelog.md` bei wichtigen Änderungen aktualisieren
- Abgeschlossene Tasks archivieren

**Monatlich**:
- Review durchführen (siehe `/07-reviews/review-template.md`)
- Tech Debt bewerten
- Anti-Patterns Dokument aktualisieren (falls neue gefunden)

**Bei Bedarf**:
- ADRs bei Architekturentscheidungen erstellen
- Requirements bei neuen Features ergänzen
- Glossar bei neuen Begriffen erweitern

## Prinzipien

### Konsistenz
Alle Prompts folgen den gleichen Guidelines aus `/03-prompts/prompt-guidelines.md`.

### Nachvollziehbarkeit
Jede Entscheidung wird dokumentiert (ADRs), jeder Task wird getrackt.

### Qualität
Definition of Done und Review-Checklisten erzwingen Qualitätsstandards.

### Langfristigkeit
Struktur ist auf Monate/Jahre Projektlaufzeit ausgelegt.

## Onboarding

Neue Entwickler oder LLM-Sessions sollten folgende Dateien lesen:

1. `/docs/00-meta/vision.md` - Projektziel verstehen
2. `/docs/01-architecture/system-overview.md` - Architektur verstehen
3. `/docs/03-prompts/prompt-guidelines.md` - Wie mit LLMs arbeiten
4. `/docs/05-quality/code-standards.md` - Coding Conventions

## Support

Bei Fragen zur Dokumentationsstruktur oder Unklarheiten:
- Prüfe `/docs/03-prompts/prompt-guidelines.md`
- Konsultiere relevante Templates in `/docs/03-prompts/prompt-templates.md`
- Im Zweifel: Dokumentiere das Problem und hole Feedback ein

---

Letzte Aktualisierung: 2026-01-14

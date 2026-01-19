# Changelog

## Zweck dieser Datei
Chronik der wichtigen Projektänderungen. Gibt Überblick über die Entwicklung.

## Verwendung
- **Wann ausfüllen**: Nach jedem Major Milestone, Feature oder wichtiger Änderung
- **Wann referenzieren**: Bei Reviews, Retrospektiven, Status-Updates
- **Wann aktualisieren**: Wöchentlich oder nach größeren Changes

## Was du mit dem LLM machst
```
Prompt: "Aktualisiere den Changelog in /docs/00-meta/changelog.md mit folgenden Änderungen:

[Beschreibung was passiert ist]"
```

## Format

Gruppiere nach Datum und Kategorie:

```markdown
## YYYY-MM-DD

### [Kategorie] (z.B. Features, Datenbank, Architektur, Bugfixes)
- [Änderung 1]
- [Änderung 2]

### [Kategorie 2]
- [Änderung 3]
```

## Kategorien-Vorschläge
- **Features**: Neue Funktionalität
- **Datenbank**: Schema-Änderungen
- **Architektur**: Strukturelle Änderungen
- **Bugfixes**: Behobene Fehler
- **Refactoring**: Code-Verbesserungen
- **Dependencies**: Library-Updates
- **Dokumentation**: Doku-Updates
- **Tests**: Test-Verbesserungen
- **Performance**: Performance-Optimierungen
- **Security**: Sicherheits-Verbesserungen

## Was gehört rein?
✅ **JA**:
- Neue Features
- Breaking Changes
- Wichtige Bugfixes
- Architekturänderungen
- Major Refactorings
- Dependency-Updates

❌ **NEIN**:
- Typo-Fixes
- Code-Formatierung
- Triviale Änderungen

---

## Start-Eintrag (Beispiel)

## 2026-01-14

### Projekt-Setup
- Projekt initialisiert
- Dokumentationsstruktur erstellt
- Git-Repository aufgesetzt

---

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Status-Updates erstellt werden
- Reviews durchgeführt werden
- Onboarding neuer Entwickler

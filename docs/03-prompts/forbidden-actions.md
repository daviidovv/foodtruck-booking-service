# Forbidden Actions

## 🔴 KRITISCH 🔴
Diese Datei MUSS bei JEDEM Code-generierenden Prompt mitgegeben werden!

## Zweck dieser Datei
Explizite Liste von Dingen, die das LLM NIEMALS tun darf. Das sind deine "Sicherheitsleitplanken".

Das LLM darf diese Aktionen nur ausführen, wenn du es EXPLIZIT erlaubst.

## Verwendung

### Bei JEDEM Prompt für Code-Generierung:
```
Prompt: "Implementiere [Feature].

Mandatory Context: Siehe /docs/03-prompts/mandatory-context.md
Forbidden Actions: Siehe /docs/03-prompts/forbidden-actions.md

[Restlicher Prompt]"
```

---

## Was reingehört

### Code-Änderungen
Dinge, die nicht ohne Erlaubnis geändert werden dürfen.

```markdown
## Code-Änderungen (VERBOTEN ohne Erlaubnis)
- Umbenennung von Packages
- Änderung von API-Endpunkten (Breaking Changes)
- Refactoring von mehr als [X] Klassen gleichzeitig
- Hinzufügen neuer Dependencies
- Änderung der DB-Migration-History
- Löschen von bestehendem Code
```

### Architektur
Strukturelle Änderungen.

```markdown
## Architektur (VERBOTEN ohne ADR)
- Wechsel von REST zu GraphQL
- Einführung neuer Layer
- Änderung der Authentifizierungsstrategie
- Änderung des Datenmodells ohne Migration
- Einführung neuer Architektur-Patterns
```

### Qualität
Qualitäts-Verletzungen.

```markdown
## Qualität (NIEMALS)
- Entfernung von Tests
- Abschwächung von Validierungen
- Deaktivierung von Security-Features
- Ignorieren von Compiler-Warnings
- Code ohne Tests committen
```

### Datenbank
DB-Operationen.

```markdown
## Datenbank (VERBOTEN ohne Erlaubnis)
- Änderung bestehender Flyway-Migrationen
- Breaking Changes im Schema
- Löschen von Daten
- Direktes SQL statt JPA (außer Performance-kritisch)
```

### Anti-Patterns
Bekannte Probleme, die vermieden werden müssen.

```markdown
## Anti-Patterns (NIEMALS verwenden)
- God Classes (> [X] Zeilen)
- Field Injection (@Autowired auf Feldern)
- Business Logic in Controllern
- Business Logic in Entities
- Entities direkt nach außen exponieren
- Magic Numbers
- Hardcoded Credentials
- Checked Exceptions ohne guten Grund
```

---

## Template zum Ausfüllen

Fülle diese Kategorien aus:

```markdown
# Forbidden Actions - [Projektname]

Letzte Aktualisierung: [Datum]

## Code-Änderungen
VERBOTEN ohne explizite Erlaubnis:
- [Aktion 1]
- [Aktion 2]
- [...]

## Architektur
VERBOTEN ohne ADR:
- [Aktion 1]
- [Aktion 2]
- [...]

## Qualität
NIEMALS:
- [Aktion 1]
- [Aktion 2]
- [...]

## Datenbank
VERBOTEN ohne Erlaubnis:
- [Aktion 1]
- [Aktion 2]
- [...]

## Dokumentation
NIEMALS:
- ADRs oder Requirements löschen
- Abgeschlossene Tasks ändern
- [...]

## Anti-Patterns
NIEMALS verwenden:
- [Pattern 1]: [Warum verboten]
- [Pattern 2]: [Warum verboten]
- [...]

## Projektspezifische Verbote
[Füge hier projektspezifische Regeln hinzu, die aus Erfahrung entstehen]
```

---

## 🔴 Erweitern bei Problemen!

**Wenn das LLM etwas Falsches macht:**
1. Füge es hier als verbotene Aktion hinzu
2. Erkläre WARUM es verboten ist
3. Das verhindert, dass es wieder passiert

**Beispiel**:
```markdown
### Lombok @SneakyThrows
**Verboten weil**: Versteckt Exceptions, macht Debugging schwer.
**Problem aufgetreten am**: 2026-01-15
**Stattdessen**: Explizites Exception Handling
```

---

## 🔴 Bei JEDEM Code-Prompt verwenden!

Diese Datei ist deine Versicherung gegen LLM-Fehler!

# Datenmodell

## Zweck dieser Datei
Dokumentation des kompletten Datenbank-Schemas. Zeigt alle Tabellen, Beziehungen und Constraints.

## Verwendung
- **Wann ausfüllen**: Nach der ersten DB-Migration + bei jeder Schema-Änderung
- **Wann referenzieren**: Bei DB-Migrationen, Entity-Erstellung, Queries
- **Wann aktualisieren**: Nach JEDER Flyway-Migration

## Was du mit dem LLM machst
```
Prompt: "Aktualisiere das Datenmodell in /docs/01-architecture/data-model.md:

Neue Tabelle [name] mit Spalten: [...]
Beziehung zu [andere Tabelle]: [...]"
```

## Struktur

### ER-Diagramm (optional aber empfohlen)
ASCII- oder Mermaid-Diagramm der Tabellen und Beziehungen.

**Beispiel ASCII**:
```
┌─────────────┐       ┌─────────────┐
│   User      │───┐   │   Order     │
├─────────────┤   └──<├─────────────┤
│ id (PK)     │       │ id (PK)     │
│ name        │       │ user_id (FK)│
└─────────────┘       └─────────────┘
```

### Tabellen-Beschreibung

Für JEDE Tabelle:

```markdown
### [tabellenname]

**Zweck**: Was wird hier gespeichert?

**Spalten**:
- `id` (UUID/BIGINT, PK): Primärschlüssel
- `name` (VARCHAR(200), NOT NULL): Beschreibung
- `created_at` (TIMESTAMP, NOT NULL): Erstellungszeitpunkt
- `updated_at` (TIMESTAMP, NOT NULL): Letzte Änderung
- etc.

**Indizes**:
- `idx_[tabelle]_[spalte]`: Zweck des Index

**Constraints**:
- Primary Key: id
- Foreign Keys: [Beschreibung]
- Unique: [Spalten]
- Check: [Validierungen]

**Beziehungen**:
- 1:N zu [andere Tabelle]: [Beschreibung]
```

### Enum-Werte / Status-Felder

Wenn es Status-Felder mit festen Werten gibt:

```markdown
## Status-Werte

### [tabelle].[spalte]
Erlaubte Werte:
- `VALUE_1`: Beschreibung
- `VALUE_2`: Beschreibung
```

### Zukünftige Erweiterungen
Geplante Tabellen/Änderungen (optional):

```markdown
## Geplante Erweiterungen
- Tabelle `[name]` für [Zweck] (Phase 2)
- Spalte `[name]` in `[tabelle]` (Feature X)
```

---

## ⚠️ KRITISCH
Diese Datei MUSS nach jeder Flyway-Migration aktualisiert werden!

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Neue Entities erstellt werden
- DB-Migrationen geschrieben werden
- Queries implementiert werden
- JPA-Repositories erstellt werden

# ADR-001: PostgreSQL als Datenbank

Datum: 2026-01-14
Status: Accepted

## Kontext
Für das Foodtruck-Booking-System wird eine relationale Datenbank benötigt. Die Wahl der Datenbank beeinflusst Performance, Wartbarkeit, Kosten und Entwicklererfahrung.

## Betrachtete Optionen
1. PostgreSQL
2. MySQL
3. H2 (für Production)

## Entscheidung
Wir haben uns für PostgreSQL 16 entschieden.

## Begründung
### Pro
- Native JSON/JSONB-Support für flexible Attribute (zukünftige Erweiterungen)
- Exzellente Concurrent-Write-Performance (wichtig für Buchungssystem)
- Starke Community und Langzeit-Support
- Kostenlos und Open Source
- Hervorragende Dokumentation
- Spring Boot und Flyway haben exzellente PostgreSQL-Integration
- ACID-Compliant und hochzuverlässig
- Fortgeschrittene Features (Window Functions, CTEs, Full-Text-Search)

### Contra
- Komplexerer Setup als MySQL (akzeptiert: Docker Compose löst dies)
- Etwas höherer Ressourcenverbrauch als MySQL (akzeptabel für unsere Anforderungen)

## Konsequenzen
### Positiv
- JSONB-Felder können in Zukunft für flexible Attribute genutzt werden
- Robuste Transaktionsunterstützung für Buchungen
- Bessere Performance bei komplexen Queries
- Gute Skalierbarkeit

### Negativ
- Windows-Entwicklung erfordert Docker (oder WSL2)
- Etwas komplexeres Operations-Setup als MySQL

### Neutral
- Flyway wird für Schema-Migrationen eingesetzt
- Docker Compose für lokale Entwicklung
- Testcontainers für Integration-Tests

## Alternativen

**MySQL**: Gute Alternative mit ähnlichen Features, aber:
- Weniger fortgeschrittene Features
- Schwächerer JSON-Support
- Kein spezifischer Vorteil für unseren Use Case

**H2 für Production**: Nicht geeignet für Production:
- In-Memory-Datenbank, keine Persistenz
- Fehlende Enterprise-Features
- Keine echte Concurrent-Write-Unterstützung

## Verknüpfungen
- NFR-002: Performance-Anforderungen
- NFR-003: Database Performance
- NFR-006: Horizontal Scaling

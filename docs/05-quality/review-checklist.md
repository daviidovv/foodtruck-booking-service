# Review Checklist

Prüfpunkte für Code-Reviews. Diese Checkliste nach jeder Task-Fertigstellung durchgehen.

## Funktionalität

- [ ] Code erfüllt alle Akzeptanzkriterien des Tasks
- [ ] Alle User Stories sind implementiert
- [ ] Edge Cases werden behandelt
- [ ] Error Cases werden korrekt behandelt
- [ ] Business-Logik ist korrekt implementiert

## Code-Qualität

### Lesbarkeit
- [ ] Code ist selbsterklärend
- [ ] Variablen haben aussagekräftige Namen
- [ ] Methoden haben klare Single Responsibility
- [ ] Keine Magic Numbers (Konstanten verwenden)
- [ ] Keine Abkürzungen außer Standards (id, dto, etc.)

### Struktur
- [ ] Angemessene Methodenlänge (< 50 Zeilen)
- [ ] Angemessene Klassenlänge (< 500 Zeilen)
- [ ] Keine God Classes
- [ ] Keine Code-Duplikation
- [ ] DRY-Prinzip eingehalten
- [ ] SOLID-Prinzipien beachtet

### Complexity
- [ ] Cyclomatic Complexity akzeptabel
- [ ] Keine übermäßig verschachtelte Logik (max 3 Ebenen)
- [ ] Komplexe Logik ist kommentiert
- [ ] Keine übermäßigen if-else-Ketten

## Architecture

- [ ] Layer-Separation eingehalten (Controller -> Service -> Repository)
- [ ] Business-Logik in Service-Layer
- [ ] Keine Business-Logik in Controllern
- [ ] Keine Business-Logik in Entities
- [ ] DTOs für externe Schnittstellen
- [ ] Entities nicht nach außen exponiert
- [ ] Package-Struktur eingehalten

## Spring Boot Best Practices

- [ ] Dependency Injection via Constructor
- [ ] @Transactional korrekt eingesetzt
- [ ] readOnly=true für lesende Transaktionen
- [ ] @Validated auf Controllern
- [ ] Bean Validation auf DTOs
- [ ] Exception Handling via @RestControllerAdvice
- [ ] Kein Field Injection (@Autowired auf Feldern)

## Datenbank

- [ ] JPA-Entities korrekt annotiert
- [ ] Indizes auf häufig abgefragten Spalten
- [ ] N+1-Problem vermieden (Eager/Lazy Loading richtig)
- [ ] Transaktionen sinnvoll abgegrenzt
- [ ] Keine Raw SQL (außer Performance-kritisch)
- [ ] Flyway-Migration vorhanden (bei Schema-Änderungen)
- [ ] Migration ist rückwärtskompatibel

## Security

- [ ] Keine SQL-Injection möglich (Prepared Statements)
- [ ] Keine sensiblen Daten in Logs
- [ ] Keine Credentials im Code
- [ ] Input-Validierung vorhanden
- [ ] Output-Encoding (bei Web-Output)
- [ ] Keine unsicheren Deserialisierung
- [ ] CSRF-Protection (falls stateful)

## Performance

- [ ] Keine offensichtlichen Performance-Probleme
- [ ] DB-Queries optimiert
- [ ] Keine unnötigen DB-Zugriffe
- [ ] Pagination bei großen Datenmengen
- [ ] Lazy Loading wo sinnvoll
- [ ] Keine Memory Leaks

## Error Handling

- [ ] Exceptions sinnvoll verwendet
- [ ] Custom Exceptions für Business Errors
- [ ] Fehlermeldungen sind verständlich
- [ ] Keine leeren catch-Blocks
- [ ] Keine übermäßige Exception-Verwendung
- [ ] Try-with-resources für AutoCloseable

## Testing

- [ ] Test-Coverage >= 80%
- [ ] Unit-Tests für Business-Logik
- [ ] Integration-Tests für Endpoints
- [ ] Negative Tests vorhanden
- [ ] Alle Tests sind grün
- [ ] Tests sind wartbar
- [ ] Test-Namen sind aussagekräftig
- [ ] Arrange-Act-Assert Pattern
- [ ] Keine flaky Tests
- [ ] Tests laufen schnell

## Dokumentation

- [ ] JavaDoc für öffentliche APIs
- [ ] Komplexe Logik ist kommentiert
- [ ] API-Endpoints dokumentiert (OpenAPI)
- [ ] README aktualisiert (falls nötig)
- [ ] ADR erstellt (bei Architekturentscheidungen)
- [ ] Keine veralteten Kommentare

## Logging

- [ ] Angemessenes Log-Level verwendet
- [ ] Wichtige Business-Events geloggt
- [ ] Errors werden geloggt
- [ ] Keine sensiblen Daten in Logs
- [ ] Strukturiertes Logging
- [ ] Nicht zu viel Logging (Performance)

## Dependencies

- [ ] Neue Dependencies sind notwendig
- [ ] Dependencies sind aktuell
- [ ] Keine Sicherheitslücken (mvn dependency:check)
- [ ] Keine Lizenz-Konflikte
- [ ] Dependencies in pom.xml dokumentiert

## REST API

- [ ] RESTful Best Practices
- [ ] Korrekte HTTP-Verben
- [ ] Sinnvolle Status-Codes
- [ ] Konsistente URL-Struktur
- [ ] JSON-Format
- [ ] Error-Responses strukturiert

## Wartbarkeit

- [ ] Code ist erweiterbar
- [ ] Keine Tight Coupling
- [ ] Interfaces wo sinnvoll
- [ ] Keine technische Schulden eingeführt
- [ ] Keine Workarounds ohne Dokumentation
- [ ] Kein auskommentierter Code
- [ ] Keine TODOs ohne Ticket-Referenz

## Compliance

- [ ] DSGVO-konform (falls personenbezogene Daten)
- [ ] Keine verbotenen Patterns (siehe anti-patterns.md)
- [ ] Code Standards eingehalten (siehe code-standards.md)
- [ ] Forbidden Actions nicht verletzt (siehe forbidden-actions.md)

## Git

- [ ] Commit-Messages aussagekräftig
- [ ] Keine großen Binärdateien
- [ ] Keine sensiblen Daten committed
- [ ] .gitignore aktuell

## Build

- [ ] Maven Build erfolgreich
- [ ] Keine Compiler-Warnings
- [ ] Alle Tests laufen durch
- [ ] Keine Breaking Changes (oder dokumentiert)

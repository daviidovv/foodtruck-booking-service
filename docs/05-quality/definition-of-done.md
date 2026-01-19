# Definition of Done

Ein Task gilt nur dann als abgeschlossen, wenn ALLE Punkte erfüllt sind.

## Code

- [ ] Code kompiliert ohne Warnings
- [ ] Code folgt projektspezifischen Conventions (siehe code-standards.md)
- [ ] Keine hartcodierten Werte (außer Konstanten)
- [ ] Kein auskommentierter Code
- [ ] Keine TODO-Kommentare ohne Issue-Referenz
- [ ] Keine Code-Duplikation
- [ ] Methoden haben maximal 50 Zeilen
- [ ] Klassen haben maximal 500 Zeilen

## Tests

- [ ] Unit-Tests für Business-Logik (min. 80% Coverage)
- [ ] Integration-Tests für API-Endpoints
- [ ] Testcontainers für DB-Tests verwendet
- [ ] Negative Test-Cases abgedeckt
- [ ] Alle Tests sind grün
- [ ] Keine flaky Tests
- [ ] Tests sind wartbar und verständlich

## Dokumentation

- [ ] API-Endpunkte in OpenAPI dokumentiert (falls zutreffend)
- [ ] JavaDoc für öffentliche APIs
- [ ] README aktualisiert (falls nötig)
- [ ] ADR erstellt (bei Architekturentscheidungen)
- [ ] Task-Dokumentation vollständig ausgefüllt
- [ ] Komplexe Logik inline kommentiert

## Review

- [ ] Selbst-Review mit review-checklist.md durchgeführt
- [ ] Code von zweiter Person geprüft (falls Team)
- [ ] Security-Aspekte geprüft (SQL-Injection, XSS, etc.)
- [ ] Performance-Impact bewertet
- [ ] Anti-Patterns geprüft (siehe anti-patterns.md)

## Deployment-Readiness

- [ ] DB-Migration getestet (Up & Down)
- [ ] Umgebungsvariablen dokumentiert
- [ ] Keine Breaking Changes ohne Versionierung
- [ ] Backwards-kompatibel (oder Migration-Plan vorhanden)
- [ ] Rollback-Strategie dokumentiert (bei kritischen Änderungen)

## Integration

- [ ] Code baut erfolgreich mit Maven
- [ ] Keine neuen Dependencies ohne Begründung
- [ ] Keine Konflikte mit bestehendem Code
- [ ] Flyway-Migrationen laufen durch

## Qualitätskriterien

- [ ] Keine Verletzung von SOLID-Prinzipien
- [ ] DRY-Prinzip eingehalten
- [ ] YAGNI beachtet (keine unnötigen Features)
- [ ] Code ist selbsterklärend

# CLAUDE.md - Foodtruck Booking Service

## Projekt

**Was:** Webanwendung für Hähnchen-Foodtruck-Reservierungen (Standort-basiert, nicht Foodtruck-basiert)

**Tech-Stack:** Java 25, Spring Boot 4, PostgreSQL 16, React 18, TypeScript, Vite, Tailwind CSS

**Architektur:** Monolith mit Schichtentrennung (Controller → Service → Repository)

## Wichtige Pfade

```
src/main/java/org/example/foodtruckbookingservice/  # Backend
src/main/resources/db/migration/                     # Flyway
frontend/src/                                        # React Frontend
docs/04-tasks/                                       # Tasks (active/, backlog/, completed/)
```

## Workflows

### Task erstellen

Wenn der User sagt "Erstelle Task für X":

1. **Kontext sammeln** - relevante Dateien lesen um aktuellen Stand zu verstehen
2. **Fragen stellen** - bei Unklarheiten IMMER nachfragen:
   - Was genau soll es können?
   - Welche Edge Cases?
   - UI/UX-Präferenzen?
3. **Task erstellen** im Format unten
4. **Speichern** in `docs/04-tasks/backlog/TASK-XXX-[name].md`

**Task-Format (kompakt):**
```markdown
# TASK-XXX: [Titel]

## Status
- [ ] Neu / In Bearbeitung / Review / Abgeschlossen

## Ziel
[Was soll erreicht werden - 1-3 Sätze]

## Akzeptanzkriterien
- [ ] Kriterium 1
- [ ] Kriterium 2
- [ ] Tests geschrieben

## Offene Fragen
- Frage 1?
```

### Task implementieren

Wenn der User sagt "Implementiere TASK-XXX":

**Vorher:**
- Lies den Task
- Bei größeren Features: lies `docs/03-prompts/mandatory-context.md` für Tech-Details
- Bei Unklarheiten zu Verboten: lies `docs/03-prompts/forbidden-actions.md`

**Während:**
- Schreibe **immer** Tests (Unit + Integration wo sinnvoll)
- Folge bestehenden Patterns im Code
- Bei Unklarheiten: Fragen stellen

**Nachher:**
- `mvn test` - alle Tests müssen grün sein
- `mvn spring-boot:run` - App starten und manuell testen
- Frontend prüfen falls betroffen: `cd frontend && npm run dev`

### ADR erstellen (Architecture Decision Record)

Wenn eine Architekturentscheidung getroffen werden muss:

```markdown
# ADR-XXX: [Titel]

**Datum:** YYYY-MM-DD
**Status:** Proposed / Accepted / Deprecated

## Kontext
[Warum diese Entscheidung?]

## Optionen
1. Option A - Pro: ..., Contra: ...
2. Option B - Pro: ..., Contra: ...

## Entscheidung
[Welche Option und warum]

## Konsequenzen
[Was ändert sich dadurch]
```

Speichern in: `docs/06-decisions/ADR-XXX-[name].md`

## Coding-Standards (Kurzfassung)

**Backend:**
- Constructor Injection (KEIN @Autowired auf Feldern)
- @Transactional(readOnly=true) auf Service-Klasse, @Transactional auf schreibenden Methoden
- Entities NIE direkt nach außen → immer DTOs
- UUID als ID-Typ

**Frontend:**
- Functional Components mit TypeScript
- React Query für Server-State (KEIN Redux)
- Tailwind CSS für Styling

**Tests:**
- Naming: `methodName_scenario_expectedBehavior()`
- Arrange-Act-Assert Pattern
- Testcontainers für DB-Tests

## Projektspezifische Regeln

- **Standort-basiert:** Kunden wählen Standort + Tag, NICHT Foodtruck
- **E-Mail optional:** customer_email MUSS nullable bleiben
- **Kapazität:** Kommt aus location_schedule.daily_capacity
- **Kein Payment:** Zahlung bei Abholung (MVP)
- **Keine User-Accounts:** Kunden brauchen keine Registration

## Verbote (wichtigste)

- Package `org.example.foodtruckbookingservice` NICHT umbenennen
- Bestehende Flyway-Migrationen NICHT ändern
- Tests NICHT löschen
- Breaking API-Changes NICHT ohne Ankündigung

Vollständige Liste: `docs/03-prompts/forbidden-actions.md`

## Verhalten

- **Fragen stellen:** Bei Unklarheiten IMMER nachfragen
- **Mitdenken:** Prüfe ob Anforderungen sinnvoll sind
- **Tests sind Pflicht:** Kein Feature ohne Tests
- **Verifizieren:** Nach Implementation App starten und testen
- **Sprache:** Dokumentation Deutsch, Code/Kommentare Englisch

## Befehle

```bash
mvn spring-boot:run          # Backend starten
mvn test                     # Backend Tests
cd frontend && npm run dev   # Frontend starten
cd frontend && npm test      # Frontend Tests
docker compose up -d         # PostgreSQL starten
```

## Referenz-Dokumentation

| Datei | Wann lesen |
|-------|------------|
| `docs/03-prompts/mandatory-context.md` | Tech-Stack Details, Package-Struktur |
| `docs/03-prompts/forbidden-actions.md` | Was ist verboten |
| `docs/05-quality/definition-of-done.md` | Wann ist ein Task fertig |
| `docs/05-quality/code-standards.md` | Detaillierte Coding-Conventions |
| `docs/02-requirements/api-contracts.md` | API-Spezifikationen |
| `docs/01-architecture/data-model.md` | Datenbank-Schema |

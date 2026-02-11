# Forbidden Actions - Foodtruck Booking Service

Letzte Aktualisierung: 2026-01-19

## 🔴 KRITISCH 🔴
Diese Datei MUSS bei JEDEM Code-generierenden Prompt mitgegeben werden!

---

## Code-Änderungen

**VERBOTEN ohne explizite Erlaubnis:**

- ❌ **Package-Umbenennung**: `org.example.foodtruckbookingservice` darf NICHT geändert werden
- ❌ **API-Breaking-Changes**: Bestehende REST-Endpoints dürfen nicht geändert/gelöscht werden (nur neue hinzufügen oder deprecaten)
- ❌ **Dependencies hinzufügen**: Neue Maven/npm Dependencies nur nach Genehmigung
- ❌ **Dependencies aktualisieren**: Major-Version-Updates (z.B. Spring Boot 4.x → 5.x) nur mit ADR
- ❌ **Refactoring > 3 Klassen**: Größere Refactorings müssen angekündigt werden
- ❌ **Löschen von bestehendem Code**: Keine Code-Deletion ohne Begründung
- ❌ **Java-Version ändern**: Java 25 ist festgelegt
- ❌ **Spring Boot Version ändern**: 4.0.1 ist festgelegt (Patches erlaubt)

---

## Architektur

**VERBOTEN ohne ADR (Architecture Decision Record):**

- ❌ **REST → GraphQL**: API-Style darf nicht geändert werden
- ❌ **Neue Layer einführen**: Layered Architecture (Controller→Service→Repository) darf nicht erweitert werden
- ❌ **Authentifizierung ändern**: Wechsel von Basic Auth zu JWT nur mit ADR
- ❌ **Datenbank wechseln**: PostgreSQL 16 ist gesetzt
- ❌ **ORM wechseln**: JPA/Hibernate darf nicht durch andere ORMs ersetzt werden
- ❌ **Event-Driven Architecture**: Einführung von Message Queues (Kafka, RabbitMQ) nur mit ADR
- ❌ **Microservices**: System ist monolithisch, keine Aufteilung ohne ADR
- ❌ **CQRS/Event Sourcing**: Nur mit ADR
- ❌ **Frontend-Framework ändern**: React 18 ist gesetzt

---

## Qualität

**NIEMALS:**

- ❌ **Tests löschen**: Bestehende Tests dürfen NICHT entfernt werden
- ❌ **Test-Coverage senken**: Min. 80% (Backend) / 70% (Frontend) muss gehalten werden
- ❌ **Validierungen entfernen**: Bean Validation darf nicht geschwächt werden
- ❌ **Security-Features deaktivieren**: Spring Security darf nicht umgangen werden
- ❌ **Compiler-Warnings ignorieren**: Alle Warnings müssen behoben werden
- ❌ **Code ohne Tests committen**: Jede Änderung braucht Tests
- ❌ **SonarLint-Issues ignorieren**: Critical/Major Issues müssen gefixt werden
- ❌ **ESLint disabled**: `/* eslint-disable */` ist verboten
- ❌ **TypeScript `any` übermäßig nutzen**: Max. 5% any-Types erlaubt

---

## Datenbank

**VERBOTEN ohne Erlaubnis:**

- ❌ **Bestehende Flyway-Migrationen ändern**: V1__*, V2__* etc. sind immutable
- ❌ **Breaking Schema Changes**: Spalten/Tabellen löschen nur mit Migration-Strategie (Deprecation → Deletion)
- ❌ **Daten löschen**: DELETE-Statements nur mit expliziter Genehmigung
- ❌ **Native SQL statt JPA**: Nur bei bewiesenen Performance-Problemen erlaubt
- ❌ **Schema-Änderungen ohne Flyway**: Alle DB-Changes müssen über Flyway-Migrationen laufen
- ❌ **ID-Typ ändern**: UUID ist gesetzt, keine Long/Integer IDs
- ❌ **Indizes löschen**: Bestehende Indizes dürfen nicht entfernt werden
- ❌ **Foreign Keys entfernen**: Referentielle Integrität muss gewahrt bleiben

---

## Anti-Patterns

**NIEMALS verwenden:**

### Backend (Java/Spring Boot)

- ❌ **Field Injection**: `@Autowired` auf Feldern ist VERBOTEN
  - **Warum**: Macht Tests schwer, versteckt Dependencies
  - **Stattdessen**: Constructor Injection IMMER verwenden

- ❌ **Entities nach außen exponieren**: Entities dürfen NIEMALS in Controller-Signaturen erscheinen
  - **Warum**: Coupling, Jackson-Probleme, Lazy-Loading-Issues
  - **Stattdessen**: DTOs (Request/Response) verwenden

- ❌ **Business Logic in Controllern**: Controller nur für HTTP-Handling
  - **Warum**: Testability, Separation of Concerns
  - **Stattdessen**: Business Logic in Services

- ❌ **Business Logic in Entities**: Entities sind reine Datencontainer
  - **Warum**: Anemic Domain Model ist hier OK, komplexe Logik gehört in Services
  - **Stattdessen**: Services für Business Logic

- ❌ **God Classes**: Klassen > 500 Zeilen
  - **Warum**: Unmaintainable, schwer zu testen
  - **Stattdessen**: Aufteilen in kleinere Klassen

- ❌ **Lombok @SneakyThrows**: Versteckt Exceptions
  - **Warum**: Debugging wird unmöglich
  - **Stattdessen**: Explizites Exception Handling

- ❌ **Lombok @Cleanup**: Ressourcen-Management-Probleme
  - **Warum**: Try-with-resources ist besser
  - **Stattdessen**: try-with-resources verwenden

- ❌ **@Transactional in Controllern**: Transactions gehören in Services
  - **Warum**: Wrong Layer of Abstraction
  - **Stattdessen**: @Transactional auf Service-Ebene

- ❌ **Magic Numbers**: Hardcoded Zahlen im Code
  - **Warum**: Keine Erklärung, schwer zu ändern
  - **Stattdessen**: Constants mit aussagekräftigen Namen

- ❌ **Hardcoded Credentials**: Passwörter/Keys im Code
  - **Warum**: Security-Risiko
  - **Stattdessen**: application.yml mit Environment Variables

- ❌ **Checked Exceptions für Business-Fehler**: Custom checked exceptions
  - **Warum**: Boilerplate, schwer zu handhaben
  - **Stattdessen**: RuntimeExceptions für Business-Fehler

- ❌ **Optional als Parameter**: Optional.of(x) als Method-Parameter
  - **Warum**: Anti-Pattern, macht API unklar
  - **Stattdessen**: Nullable Parameter oder Overloading

### Frontend (React/TypeScript)

- ❌ **`any` Type überall**: TypeScript-Vorteile werden zunichte gemacht
  - **Warum**: Kein Type Safety
  - **Stattdessen**: Explizite Interfaces/Types

- ❌ **Prop Drilling > 3 Levels**: Props durch viele Komponenten durchreichen
  - **Warum**: Unmaintainable, fragil
  - **Stattdessen**: Context API oder React Query

- ❌ **Redux für alles**: Redux ist zu komplex für dieses Projekt
  - **Warum**: Overhead, Boilerplate
  - **Stattdessen**: React Query (Server State) + useState (UI State)

- ❌ **Inline Styles**: style={{ ... }} überall
  - **Warum**: Inkonsistent, schwer wartbar
  - **Stattdessen**: Tailwind CSS Classes

- ❌ **Class Components**: Nur Functional Components
  - **Warum**: Hooks sind moderner, weniger Boilerplate
  - **Stattdessen**: Functional Components mit Hooks

- ❌ **useEffect ohne Dependency Array**: Führt zu Bugs
  - **Warum**: Infinite Loops, Memory Leaks
  - **Stattdessen**: Immer Dependency Array angeben

- ❌ **Fetch API direkt**: Keine rohen fetch()-Calls
  - **Warum**: Error Handling, Loading States, Caching fehlt
  - **Stattdessen**: Axios + React Query

- ❌ **Globale CSS-Klassen**: Naming Conflicts
  - **Warum**: CSS Pollution
  - **Stattdessen**: Tailwind Utilities oder CSS Modules

---

## Dokumentation

**NIEMALS:**

- ❌ **ADRs löschen**: Architecture Decision Records sind Historie, dürfen nicht gelöscht werden
- ❌ **Abgeschlossene Tasks ändern**: `/docs/04-tasks/completed/` ist immutable
- ❌ **Requirements ohne Ticket ändern**: Functional Requirements nur mit Task-Verknüpfung ändern
- ❌ **mandatory-context.md veraltet lassen**: Muss nach jedem größeren Change aktualisiert werden
- ❌ **Definition of Done ignorieren**: Alle Tasks müssen DoD erfüllen
- ❌ **Changelog nicht pflegen**: Jeder größere Change muss in changelog.md
- ❌ **API-Contracts nach Implementation ändern**: Contracts sind Pre-Implementation, nachträgliche Änderungen müssen dokumentiert werden

---

## Projektspezifische Verbote

### Reservierungssystem-spezifisch

- ❌ **E-Mail als Pflichtfeld**: `customer_email` MUSS nullable bleiben (Design-Entscheidung aus vision.md)
  - **Warum**: Niedrige Hürde für Kunden, E-Mail ist optional
  - **Stand**: 2026-01-19

- ❌ **Zahlungsintegration in MVP**: Kein Payment-Gateway (Zahlung bei Abholung)
  - **Warum**: Out of Scope für MVP
  - **Stand**: 2026-01-19

- ❌ **Kundenverwaltung/Accounts**: Keine User-Registration für Kunden
  - **Warum**: MVP-Scope, Kunden brauchen keine Accounts
  - **Stand**: 2026-01-19

- ❌ **Speisekarten-Verwaltung**: Nur Hähnchen & Pommes (hardcoded)
  - **Warum**: MVP-Scope, keine dynamische Menükarte
  - **Stand**: 2026-01-19

- ❌ **Standort-Konzept ignorieren**: Reservierungen sind standortbezogen, NICHT foodtruckbezogen
  - **Warum**: Kern-Business-Logic, zwei Foodtrucks rotieren zwischen Standorten
  - **Stand**: 2026-01-19

- ❌ **Kapazität ohne location_schedule**: Kapazität MUSS aus `location_schedule.daily_capacity` kommen
  - **Wahem**: Definierte Business-Rule
  - **Stand**: 2026-01-19

### Multi-Standort-spezifisch

- ❌ **Foodtruck-Entitäten erstellen**: System verwaltet STANDORTE, nicht Foodtrucks
  - **Warum**: Kunden wählen Standort+Tag, nicht Foodtruck
  - **Stand**: 2026-01-19

- ❌ **Wochentags-Logik ignorieren**: `location_schedule.day_of_week` (1=Montag, 7=Sonntag) ist zentral
  - **Warum**: Kern-Feature für Multi-Standort
  - **Stand**: 2026-01-19

---

## 🔴 Erweitern bei Problemen!

**Wenn das LLM etwas Falsches macht:**
1. Füge es hier als verbotene Aktion hinzu
2. Erkläre WARUM es verboten ist
3. Füge Datum hinzu wann Problem auftrat
4. Das verhindert, dass es wieder passiert

**Format:**
```markdown
- ❌ **[Aktion]**: [Kurzbeschreibung]
  - **Warum**: [Begründung]
  - **Problem aufgetreten am**: [Datum]
  - **Stattdessen**: [Alternative]
```

---

## 🔴 Bei JEDEM Code-Prompt verwenden!

Referenziere IMMER zusammen mit:
- `/docs/03-prompts/mandatory-context.md`
- `/docs/05-quality/definition-of-done.md`

Diese drei Dateien sind deine Versicherung gegen LLM-Fehler!

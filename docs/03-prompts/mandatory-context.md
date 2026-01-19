# Mandatory Context

## 🔴 KRITISCH 🔴
Diese Datei MUSS bei JEDEM Code-generierenden Prompt mitgegeben werden!

## Zweck dieser Datei
Enthält ALLE Informationen, die das LLM IMMER wissen muss, um korrekten Code zu schreiben.

Das ist dein "Projektgedächtnis" - hier steht alles, was sich das LLM nicht selbst erarbeiten kann.

## Verwendung

### Bei JEDEM Prompt für Code-Generierung:
```
Prompt: "Implementiere [Feature].

Mandatory Context: Siehe /docs/03-prompts/mandatory-context.md
Forbidden Actions: Siehe /docs/03-prompts/forbidden-actions.md

[Restlicher Prompt]"
```

## Was reingehört

### Tech-Stack (MUSS ausgefüllt sein!)
Liste ALLE verwendeten Technologien mit EXAKTEN Versionen:

```markdown
## Tech-Stack
- **Java**: [Version]
- **Spring Boot**: [Version]
- **Datenbank**: [PostgreSQL/MySQL/...] [Version]
- **Build**: Maven / Gradle
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Migration**: Flyway / Liquibase
```

### Aktuelle Package-Struktur
Wo liegt was? Damit das LLM weiß, wo Code hin muss.

```markdown
## Package-Struktur
- Base Package: `com.example.projektname`
- Controller: `com.example.projektname.controller`
- Service: `com.example.projektname.service`
- Repository: `com.example.projektname.repository`
- Entity: `com.example.projektname.entity`
- DTO: `com.example.projektname.dto`
```

### Coding-Constraints
Projektspezifische Regeln.

```markdown
## Coding-Constraints
- **Lombok**: Nur @Data, @Builder für DTOs, kein @SneakyThrows
- **Validation**: Controller müssen @Validated verwenden
- **Transactions**: @Transactional(readOnly=true) auf Service-Klasse
- **Entities**: Immer Audit-Felder (createdAt, updatedAt)
- **DTOs**: Nie Entities direkt nach außen geben
- **Dependency Injection**: Constructor Injection, kein Field Injection
```

### Aktuelle Datenbankstruktur
Welche Tabellen existieren? (Kurz-Übersicht)

```markdown
## Aktuelle Datenbank-Struktur
- Tabelle `[name]`: [Spalten-Übersicht]
- Tabelle `[name]`: [Spalten-Übersicht]

Siehe /docs/01-architecture/data-model.md für Details.
```

### API-Standard
Wie sollen APIs aussehen?

```markdown
## API-Standard
- Base URL: `/api/v1`
- Format: JSON
- HTTP-Status-Codes: [Standard verwenden]
- Error Format: [Dein Format]
- Pagination: [Spring Data Pageable / Custom]
```

### Qualitäts-Gates
Was muss erfüllt sein?

```markdown
## Qualitäts-Gates
- Test-Coverage: Min. X%
- Keine Compiler-Warnings
- Flyway-Migrationen rückwärtskompatibel
- OpenAPI/Swagger-Dokumentation automatisch generiert
```

### Aktueller Projekt-Status
Was ist schon da? Was fehlt noch?

```markdown
## Projekt-Status
- [x] Datenbank-Schema erstellt
- [x] Entities implementiert
- [ ] Controller in Arbeit
- [ ] Security konfiguriert (aktuell: Default)
```

---

## 🔴 WICHTIG: Aktualität

Diese Datei MUSS aktuell gehalten werden!

**Wann aktualisieren?**
- Nach jedem Tech-Stack-Update
- Nach jeder Datenbank-Migration
- Bei geänderten Coding-Conventions
- Bei neuen Constraints

**Wie aktualisieren?**
```
Prompt: "Aktualisiere /docs/03-prompts/mandatory-context.md:

[Was hat sich geändert]"
```

---

## Template zum Ausfüllen

Kopiere dieses Template und fülle es aus:

```markdown
# Mandatory Context - [Projektname]

Letzte Aktualisierung: [Datum]

## Tech-Stack
- **Java**: [Version]
- **Spring Boot**: [Version]
- **Datenbank**: [Typ + Version]
- **Build**: Maven / Gradle
- **Testing**: [Tools]

## Spring Boot Module
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- [weitere...]

## Package-Struktur
- Base: `[Package]`
- Controller: `[Package].controller`
- Service: `[Package].service`
- Repository: `[Package].repository`
- Entity: `[Package].entity`
- DTO: `[Package].dto`

## Coding-Constraints
- Lombok: [Regeln]
- Validation: [Regeln]
- Transactions: [Regeln]
- [weitere...]

## Aktuelle Datenbank-Struktur
- Tabelle `[name]`: [Spalten]
- [weitere...]

## API-Standard
- Base URL: `/api/v1`
- Format: JSON
- Status-Codes: [Standard]

## Qualitäts-Gates
- Test-Coverage: Min. X%
- [weitere...]

## Dokumentation
- JavaDoc für öffentliche APIs
- Komplexe Logik kommentieren
- Keine überflüssigen Kommentare
```

---

## 🔴 Bei JEDEM Code-Prompt verwenden!

Ohne diese Datei generiert das LLM inkonsistenten Code!

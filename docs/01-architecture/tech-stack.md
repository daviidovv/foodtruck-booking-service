# Tech Stack - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-11

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Code-Generierung (korrekte Imports, Versionen)
- Bei neuen Dependencies (Kompatibilitätsprüfung)
- Bei Onboarding neuer Entwickler
- Bei Version-Updates (Impact-Analyse)
- Bei Tooling-Setup (IDE, Docker)

**Prompt-Beispiel:**
```
Generiere Code für [Feature] mit korrekten Versionen aus:
- /docs/01-architecture/tech-stack.md (Versionen)
- /docs/03-prompts/mandatory-context.md (Coding-Standards)
```

**Hinweis:** Versionen sind **fixiert**. Änderungen nur mit ADR!

---

## Backend

### Kern-Technologien

| Technologie | Version | Begründung |
|-------------|---------|------------|
| **Java** | 25 | Neueste LTS-Version, Records, Pattern Matching, Virtual Threads |
| **Spring Boot** | 4.0.1 | Modernes Java-Framework, Convention over Configuration |
| **Maven** | 3.9+ | Build-Tool, Dependency-Management |
| **PostgreSQL** | 16 | Robuste relationale DB, JSON-Support, Volltextsuche |

### Spring Boot Module

```xml
<!-- Produktive Dependencies -->
<dependencies>
    <!-- REST API -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- JPA / Hibernate -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Bean Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- Actuator (Health Checks, Metrics) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Flyway (DB Migrations) -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>

    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- OpenAPI / Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.3.0</version>
    </dependency>
</dependencies>
```

### Test Dependencies

```xml
<dependencies>
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Security Test -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- Testcontainers -->
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>testcontainers</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Backend Testing-Stack

| Tool | Version | Verwendung |
|------|---------|------------|
| **JUnit 5** | 5.10+ | Unit-Tests, Test-Framework |
| **Mockito** | 5+ | Mocking von Dependencies |
| **AssertJ** | 3.24+ | Fluent Assertions |
| **Testcontainers** | 1.19+ | PostgreSQL-Container für Integration Tests |
| **JaCoCo** | 0.8+ | Test-Coverage-Reports |

---

## Frontend

### Kern-Technologien

| Technologie | Version | Begründung |
|-------------|---------|------------|
| **React** | 18.3+ | Komponentenbasiert, großes Ecosystem |
| **TypeScript** | 5.3+ | Type Safety, bessere IDE-Unterstützung |
| **Vite** | 5+ | Schneller Build, Hot Module Replacement |
| **npm** | 10+ | Package Manager |

### UI & Styling

| Library | Version | Verwendung |
|---------|---------|------------|
| **Tailwind CSS** | 3.4+ | Utility-First CSS, Responsive Design |
| **shadcn/ui** | latest | UI-Komponenten (Radix UI + Tailwind) |
| **Radix UI** | latest | Accessible, unstyled Primitives |

### State & Data Management

| Library | Version | Verwendung |
|---------|---------|------------|
| **React Query** | 5+ (TanStack Query) | Server State, Caching, Synchronization |
| **React Router** | 6+ | Client-side Routing |
| **Axios** | 1.6+ | HTTP Client mit Interceptors |

### Form & Validation

| Library | Version | Verwendung |
|---------|---------|------------|
| **React Hook Form** | 7+ | Performante Formulare |
| **Zod** | 3+ | Schema Validation, TypeScript Integration |

### Utilities

| Library | Version | Verwendung |
|---------|---------|------------|
| **date-fns** | 3+ | Datum/Zeit-Manipulation |
| **clsx** | 2+ | Conditional CSS Classes |

### Frontend Testing-Stack

| Tool | Version | Verwendung |
|------|---------|------------|
| **Vitest** | 1+ | Unit Tests, Jest-kompatibel |
| **React Testing Library** | 14+ | Component Tests |
| **MSW** | 2+ (Mock Service Worker) | API Mocking |

---

## DevOps & Tooling

### Containerisierung

| Tool | Version | Verwendung |
|------|---------|------------|
| **Docker** | 24+ | Container Runtime |
| **Docker Compose** | 2.20+ | Multi-Container Development |

### Code Quality

| Tool | Verwendung |
|------|------------|
| **SonarLint** | Code-Analyse im IDE |
| **ESLint** | JavaScript/TypeScript Linting |
| **Prettier** | Code Formatting |
| **Husky** | Git Hooks (Pre-Commit) |

### API Documentation

| Tool | Verwendung |
|------|------------|
| **SpringDoc OpenAPI** | Automatische API-Docs aus Code |
| **Swagger UI** | Interaktive API-Dokumentation unter `/swagger-ui.html` |

### Versionskontrolle

| Tool | Verwendung |
|------|------------|
| **Git** | Versionskontrolle |
| **GitHub** | Repository Hosting, Issues, PRs |

---

## Deployment (Geplant)

### Lokal / Development

```yaml
# docker-compose.yml
services:
  postgres:
    image: postgres:16
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: foodtruck
      POSTGRES_USER: foodtruck
      POSTGRES_PASSWORD: foodtruck
```

### Produktion (Phase 2+)

| Komponente | Geplante Lösung |
|------------|-----------------|
| **Cloud Provider** | TBD (AWS / Hetzner Cloud) |
| **Container** | Docker |
| **CI/CD** | GitHub Actions |
| **Monitoring** | Spring Actuator + Prometheus (geplant) |
| **Logging** | Strukturiertes JSON-Logging |

---

## IDE-Setup

### IntelliJ IDEA (Empfohlen)

**Plugins:**
- Lombok
- SonarLint
- GitToolBox
- Prettier

**Settings:**
- Enable Annotation Processing (für Lombok)
- Java Version: 25
- Maven Auto-Import

### VS Code (Frontend)

**Extensions:**
- ESLint
- Prettier
- Tailwind CSS IntelliSense
- TypeScript Vue Plugin (Volar)

---

## Version-Constraints

### Fixierte Versionen (Änderung nur mit ADR)

| Komponente | Version | Änderung erlaubt? |
|------------|---------|-------------------|
| Java | 25 | NEIN - Major festgelegt |
| Spring Boot | 4.0.x | Patches OK, Minor mit Review |
| PostgreSQL | 16 | NEIN - DB-Version festgelegt |
| React | 18 | NEIN - Major festgelegt |
| TypeScript | 5.x | Patches OK |

### Flexible Versionen

| Komponente | Regel |
|------------|-------|
| Testcontainers | Neueste kompatible |
| Dev-Tools (ESLint, etc.) | Neueste stable |
| UI-Libraries | Neueste stable |

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initial aus mandatory-context.md übernommen |

---

## 🔴 Wichtig für LLM

**Verwenden zusammen mit:**
- `/docs/03-prompts/mandatory-context.md` (Coding-Standards)
- `/docs/03-prompts/forbidden-actions.md` (Version-Verbote)

**Niemals:**
- Java-Version von 25 ändern
- Spring Boot Major-Version ändern
- PostgreSQL durch andere DB ersetzen
- React durch anderes Framework ersetzen
- Redux statt React Query verwenden

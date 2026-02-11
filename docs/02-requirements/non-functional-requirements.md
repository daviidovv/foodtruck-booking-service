# Non-Functional Requirements (NFRs) - Foodtruck Booking Service

Letzte Aktualisierung: 2026-02-11

---

## 📋 Zweck & Verwendung beim Prompting

**Wann diese Datei verwenden:**
- Bei Architekturentscheidungen (ADRs)
- Bei Performance-Optimierungen
- Bei Security-Implementierung
- Bei Load-Test-Planung
- Bei Code Reviews (Quality Gates prüfen)

**Prompt-Beispiel:**
```
Optimiere [Komponente] unter Berücksichtigung von:
- /docs/02-requirements/non-functional-requirements.md (NFRs)
- /docs/03-prompts/mandatory-context.md (Quality Gates)
```

**Wichtig:** Alle NFRs müssen **messbar** sein!

---

## Performance

### NFR-001: API Response Time

**Ziel:** 95% aller API-Requests werden in < 200ms beantwortet

**Messung:**
- Spring Actuator Metrics (`http.server.requests`)
- Log-Analyse (Response Time Logging)

**Akzeptanzkriterium:**
- p95 Latency < 200ms
- p99 Latency < 500ms
- Maximale Response Time < 2s (Ausnahmen: Statistik-Berechnungen)

**Betroffene Endpoints:**
- Alle `/api/v1/*` Endpoints

---

### NFR-002: Concurrent Users

**Ziel:** System unterstützt mindestens 100 gleichzeitige Benutzer

**Messung:**
- Load Testing mit k6/JMeter
- Gleichzeitige Requests simulieren

**Akzeptanzkriterium:**
- 100 concurrent Users ohne Fehler
- Response Time bleibt unter p95 < 200ms
- Keine 5xx Errors unter Last

**Szenarien:**
- 50 Kunden erstellen gleichzeitig Reservierungen
- 10 Mitarbeiter aktualisieren gleichzeitig Status
- 5 Admins rufen gleichzeitig Statistiken ab

---

### NFR-003: Database Query Performance

**Ziel:** Datenbankabfragen dauern < 50ms

**Messung:**
- Hibernate Statistics
- PostgreSQL `pg_stat_statements`
- Slow Query Log (> 100ms)

**Akzeptanzkriterium:**
- Durchschnittliche Query-Zeit < 50ms
- Keine Queries > 500ms
- Indizes für alle häufigen WHERE-Klauseln

**Kritische Queries:**
- Reservierungen nach Location + Datum
- Kapazitätsberechnung
- Statistik-Aggregationen

---

### NFR-004: Frontend Load Time

**Ziel:** Initial Page Load < 3 Sekunden (First Contentful Paint)

**Messung:**
- Lighthouse Performance Score
- Chrome DevTools Network Tab

**Akzeptanzkriterium:**
- First Contentful Paint (FCP) < 1.5s
- Largest Contentful Paint (LCP) < 2.5s
- Time to Interactive (TTI) < 3s
- Lighthouse Performance Score > 90

**Maßnahmen:**
- Code Splitting
- Lazy Loading
- Optimierte Bilder (WebP)
- Gzip/Brotli Compression

---

## Verfügbarkeit (Availability)

### NFR-005: System Uptime

**Ziel:** 99% Verfügbarkeit während Geschäftszeiten

**Messung:**
- Uptime Monitoring (z.B. UptimeRobot)
- `/actuator/health` Endpoint

**Akzeptanzkriterium:**
- Max. 7 Stunden Downtime pro Monat
- Geplante Wartung außerhalb Geschäftszeiten (vor 10:00 oder nach 21:00)
- Health Check antwortet mit 200 OK

**Geschäftszeiten:**
- Mo-Sa: 10:00 - 21:00 Uhr

---

### NFR-006: Error Recovery

**Ziel:** System erholt sich automatisch von transienten Fehlern

**Messung:**
- Error-Logs analysieren
- Retry-Counter monitoren

**Akzeptanzkriterium:**
- Automatische DB-Reconnection bei Verbindungsverlust
- Graceful Degradation bei externen Service-Ausfällen
- Keine Datenverluste bei Crash

---

## Skalierbarkeit (Scalability)

### NFR-007: Data Volume

**Ziel:** System performant mit 100.000+ Reservierungen

**Messung:**
- Performance Tests mit Testdaten
- Query-Analyse bei großen Datenmengen

**Akzeptanzkriterium:**
- Keine Performance-Degradation bis 100.000 Reservierungen
- Paginierung für alle Listen-Endpoints
- Archivierungsstrategie für alte Daten (Phase 2)

**Datenvolumen-Schätzung (1 Jahr):**
- ~50 Reservierungen/Tag × 300 Tage = 15.000 Reservierungen/Jahr
- Ziel: 10 Jahre Daten = 150.000 Reservierungen

---

### NFR-008: Horizontal Scalability (Phase 2)

**Ziel:** Backend kann horizontal skaliert werden

**Akzeptanzkriterium:**
- Stateless Design (kein Server-State)
- Session-Daten in DB oder Cache
- Load Balancer-kompatibel

**MVP:** Single-Instance ist ausreichend

---

## Security

### NFR-009: Authentication & Authorization

**Ziel:** Sichere Authentifizierung für Mitarbeiter/Admins

**Messung:**
- Security Audit
- Penetration Testing (Phase 2)

**Akzeptanzkriterium (MVP):**
- Basic Auth über HTTPS
- Passwörter mindestens 8 Zeichen
- Session-Timeout nach 30 Minuten Inaktivität
- Rollenbasierte Zugriffskontrolle (RBAC)

**Akzeptanzkriterium (Phase 2):**
- JWT-basierte Authentication
- Refresh Token Rotation
- Rate Limiting für Login-Endpoint

---

### NFR-010: OWASP Top 10

**Ziel:** Keine OWASP Top 10 Vulnerabilities

**Messung:**
- OWASP ZAP Scan
- Code Review (Security Checklist)
- Dependency Check (CVEs)

**Akzeptanzkriterium:**
- Keine SQL Injection (Parameterized Queries via JPA)
- Keine XSS (React escapes by default + CSP Header)
- Keine CSRF (Tokens oder SameSite Cookies)
- Keine Broken Authentication
- Sichere Dependencies (keine bekannten CVEs)

**Maßnahmen:**
- Input Validation (Bean Validation)
- Output Encoding
- Security Headers (HSTS, CSP, X-Frame-Options)
- Prepared Statements (JPA)

---

### NFR-011: Data Protection (DSGVO)

**Ziel:** DSGVO-konforme Verarbeitung personenbezogener Daten

**Messung:**
- DSGVO-Checkliste
- Datenschutz-Audit

**Akzeptanzkriterium:**
- Datenschutzerklärung auf Website
- Einwilligung bei Datenverarbeitung (E-Mail-Reservierung)
- Recht auf Löschung implementierbar
- Keine Datenweitergabe an Dritte
- Verschlüsselte Übertragung (HTTPS)
- Personenbezogene Daten: `customer_name`, `customer_email`

**Personenbezogene Daten im System:**

| Feld | Zweck | Aufbewahrung |
|------|-------|--------------|
| `customer_name` | Identifikation bei Abholung | 30 Tage nach Reservierung |
| `customer_email` | Bestätigungsmail (optional) | 30 Tage nach Reservierung |

---

### NFR-012: Secure Communication

**Ziel:** Alle Kommunikation verschlüsselt

**Messung:**
- SSL Labs Test (Note A+)
- Security Headers Check

**Akzeptanzkriterium:**
- TLS 1.2+ für alle Verbindungen
- HSTS Header aktiviert
- Keine Mixed Content Warnings
- Sichere Cookies (HttpOnly, Secure, SameSite)

---

## Wartbarkeit (Maintainability)

### NFR-013: Test Coverage

**Ziel:** Hohe Testabdeckung für zuverlässige Änderungen

**Messung:**
- JaCoCo (Backend)
- Vitest Coverage (Frontend)

**Akzeptanzkriterium:**

| Komponente | Minimum Coverage |
|------------|------------------|
| Backend (gesamt) | 80% |
| Backend (Service Layer) | 90% |
| Frontend (gesamt) | 70% |

**Test-Typen:**
- Unit Tests: Isolierte Komponenten
- Integration Tests: DB + API
- E2E Tests: Kritische User Flows (Phase 2)

---

### NFR-014: Code Quality

**Ziel:** Konsistente, wartbare Codebasis

**Messung:**
- SonarQube/SonarLint Analyse
- ESLint (Frontend)
- Code Review

**Akzeptanzkriterium:**
- Keine Critical/Major SonarLint Issues
- Keine ESLint Errors (Warnings minimieren)
- Keine TypeScript Errors (`strict: true`)
- Keine Compiler Warnings
- Alle PRs mit Code Review

**Metriken:**
- Cyclomatic Complexity < 10 pro Methode
- Keine Duplizierten Code-Blöcke > 10 Zeilen
- Methoden < 30 Zeilen (Ausnahmen dokumentiert)

---

### NFR-015: Documentation

**Ziel:** Vollständige technische Dokumentation

**Messung:**
- Dokumentations-Review
- Swagger UI verfügbar

**Akzeptanzkriterium:**
- API-Dokumentation automatisch generiert (OpenAPI)
- Swagger UI unter `/swagger-ui.html`
- README mit Setup-Anleitung
- ADRs für alle Architekturentscheidungen
- Inline-Kommentare für komplexe Logik

---

## Usability

### NFR-016: API Consistency

**Ziel:** Konsistente, vorhersagbare API

**Messung:**
- API Review gegen Standards
- Consumer Feedback

**Akzeptanzkriterium:**
- RESTful Best Practices (HTTP Verbs, Status Codes)
- Konsistentes Naming (camelCase)
- Einheitliches Error-Format (RFC 7807)
- Pagination für alle Listen
- Versionierung (`/api/v1`)

---

### NFR-017: Mobile Responsiveness

**Ziel:** Alle UIs sind mobile-optimiert

**Messung:**
- Lighthouse Accessibility Score
- Manuelle Tests auf verschiedenen Geräten

**Akzeptanzkriterium:**
- Responsive Design (Mobile First)
- Touch-optimierte Bedienung
- Mindestens: iPhone SE (375px), iPad (768px), Desktop (1024px+)
- Lighthouse Accessibility Score > 90

**Priorität nach Gerät:**
- Kunden-UI: Smartphone-optimiert
- Mitarbeiter-UI: Tablet-optimiert
- Admin-UI: Desktop-optimiert (Tablet sekundär)

---

### NFR-018: Accessibility (A11y)

**Ziel:** Basis-Barrierefreiheit

**Messung:**
- Lighthouse Accessibility
- axe DevTools

**Akzeptanzkriterium:**
- Lighthouse Accessibility Score > 90
- Keyboard Navigation möglich
- Sufficient Color Contrast (WCAG AA)
- Alt-Texte für Bilder
- Aria Labels für interaktive Elemente

---

## Compliance

### NFR-019: Impressum & Datenschutz

**Ziel:** Rechtskonforme Website

**Akzeptanzkriterium:**
- Impressum vorhanden und korrekt
- Datenschutzerklärung vorhanden
- Cookie-Banner (falls Tracking verwendet)
- Links im Footer sichtbar

---

### NFR-020: Open Source Licenses

**Ziel:** Nur kompatible Lizenzen

**Messung:**
- License Checker Plugin
- Dependency Review

**Akzeptanzkriterium:**
- Keine GPL-Lizenzen (außer LGPL)
- Alle Lizenzen dokumentiert
- License Audit bei neuen Dependencies

---

## Zusammenfassung

### Kritische NFRs (Must Have für MVP)

| NFR | Kategorie | Ziel |
|-----|-----------|------|
| NFR-001 | Performance | Response Time < 200ms (p95) |
| NFR-005 | Availability | 99% Uptime |
| NFR-009 | Security | Sichere Authentication |
| NFR-011 | Security | DSGVO-Konformität |
| NFR-013 | Maintainability | 80% Test Coverage (Backend) |
| NFR-017 | Usability | Mobile Responsive |

### Wichtige NFRs (Should Have)

| NFR | Kategorie | Ziel |
|-----|-----------|------|
| NFR-002 | Performance | 100 Concurrent Users |
| NFR-010 | Security | OWASP Top 10 |
| NFR-014 | Maintainability | Code Quality |
| NFR-016 | Usability | API Consistency |

### Nice-to-Have (Phase 2)

| NFR | Kategorie | Ziel |
|-----|-----------|------|
| NFR-008 | Scalability | Horizontal Scaling |
| NFR-018 | Usability | WCAG AA Compliance |

---

## Änderungshistorie

| Datum | Version | Änderung |
|-------|---------|----------|
| 2026-02-11 | 1.0 | Initiale NFRs definiert |

---

## 🔴 Wichtig für LLM

**Diese Datei definiert WIE GUT das System sein muss.**

**Verwenden zusammen mit:**
- `/docs/03-prompts/mandatory-context.md` (Quality Gates)
- `/docs/03-prompts/forbidden-actions.md` (Verbote)
- `/docs/05-quality/definition-of-done.md` (DoD)

**Bei Implementation beachten:**
- Performance: Response Time < 200ms
- Security: OWASP Top 10, DSGVO
- Quality: 80% Test Coverage
- Usability: Mobile First, Lighthouse > 90

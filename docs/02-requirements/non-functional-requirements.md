# Non-Functional Requirements (NFRs)

## Zweck dieser Datei
Definiert WIE GUT das System funktionieren muss: Performance, Security, Skalierung, etc.

## Verwendung
- **Wann ausfüllen**: Zu Projektbeginn
- **Wann referenzieren**: Bei Architekturentscheidungen, Performance-Optimierungen
- **Wann aktualisieren**: Bei geänderten Quality-Standards

## Was du mit dem LLM machst
```
Prompt: "Definiere Non-Functional Requirements in /docs/02-requirements/non-functional-requirements.md:

Performance: [Response Time < Xms, ...]
Security: [DSGVO-konform, ...]
Availability: [99.X% Uptime]
etc."
```

## Kategorien

### Performance
Geschwindigkeit und Effizienz.

```markdown
### NFR-001: Response Time
**Ziel**: 95% aller API-Requests < Xms
**Messung**: APM Tool / Actuator Metrics

### NFR-002: Throughput
**Ziel**: X Requests/Sekunde
**Messung**: Load Testing
```

### Verfügbarkeit (Availability)
Uptime und Ausfallsicherheit.

```markdown
### NFR-XXX: Uptime
**Ziel**: 99.X% Verfügbarkeit
**Messung**: Uptime Monitoring
```

### Skalierbarkeit (Scalability)
Wachstums-Anforderungen.

```markdown
### NFR-XXX: Concurrent Users
**Ziel**: X gleichzeitige Benutzer
**Messung**: Load Testing

### NFR-XXX: Data Growth
**Ziel**: System funktioniert mit X Millionen Datensätzen
**Messung**: Performance-Tests mit Testdaten
```

### Security
Sicherheitsanforderungen.

```markdown
### NFR-XXX: Authentication
**Ziel**: Sichere Authentifizierung mit [OAuth2/JWT/...]
**Standard**: OWASP Top 10 vermeiden

### NFR-XXX: Data Protection
**Ziel**: DSGVO-konform
**Maßnahmen**: Verschlüsselung, Audit-Logging, Recht auf Löschung
```

### Maintainability
Wartbarkeit des Codes.

```markdown
### NFR-XXX: Test Coverage
**Ziel**: Min. X% Code Coverage
**Messung**: JaCoCo

### NFR-XXX: Documentation
**Ziel**: Alle APIs dokumentiert (OpenAPI/Swagger)
**Prüfung**: Automatisch generiert
```

### Usability
Benutzerfreundlichkeit (wenn relevant).

```markdown
### NFR-XXX: API Consistency
**Ziel**: RESTful Best Practices
**Standards**: Korrekte HTTP-Verben, Status-Codes

### NFR-XXX: Error Messages
**Ziel**: Verständliche Fehlermeldungen
**Format**: RFC 7807 Problem Details
```

### Compliance
Regulatorische Anforderungen.

```markdown
### NFR-XXX: DSGVO
**Ziel**: Personenbezogene Daten DSGVO-konform
**Maßnahmen**: [Liste]

### NFR-XXX: Open Source Licenses
**Ziel**: Nur kompatible Lizenzen
**Check**: License Audit bei jeder Dependency
```

---

## Format pro NFR

```markdown
### NFR-XXX: [Titel]
**Ziel**: [Messbares Ziel]
**Messung**: [Wie wird geprüft?]
**Akzeptanzkriterium**: [Ab wann erfüllt?]
```

## Messbarkeit ist KRITISCH

NFRs MÜSSEN messbar sein!

❌ **Schlecht**: "Das System soll schnell sein"
✅ **Gut**: "95% aller API-Requests < 200ms (gemessen mit Spring Actuator)"

---

## ⚠️ Wichtig
Diese Datei wird dem LLM gegeben, wenn:
- Architekturentscheidungen getroffen werden (ADRs)
- Performance-Optimierungen gemacht werden
- Security-Features implementiert werden
- Load Tests geplant werden

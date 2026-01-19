# Prompt Guidelines

Regeln für effektive LLM-Prompts in diesem Projekt.

## Allgemeine Struktur

Jeder Prompt für Code-Generierung muss folgende Struktur haben:

```
1. Kontext: Was ist die aktuelle Situation?
2. Ziel: Was soll erreicht werden?
3. Constraints: Was muss beachtet werden?
4. Definition of Done: Wann ist es fertig?
```

## Pflicht-Includes

Bei JEDEM Code-Prompt müssen folgende Dateien referenziert werden:
- `/docs/03-prompts/mandatory-context.md`
- `/docs/03-prompts/forbidden-actions.md`
- Relevante Requirements aus `/docs/02-requirements/`
- Betroffene ADRs aus `/docs/06-decisions/`

## Best Practices

### Do's
- Konkretes Ziel definieren
- Akzeptanzkriterien angeben
- Beispiele für Input/Output geben
- Betroffene Dateien/Komponenten nennen
- Tech-Stack-Constraints explizit machen
- Testanforderungen spezifizieren

### Don'ts
- Vage Anfragen ("mach es besser")
- Mehrere unabhängige Tasks in einem Prompt
- Widersprüchliche Anforderungen
- Fehlende Validierungskriterien
- Unklare Performance-Erwartungen

## Prompt-Kategorien

### Feature-Implementierung
```
Kontext: [Beschreibung des aktuellen Zustands]
Ziel: Implementiere [Feature] basierend auf [Requirement-ID]
Technisch: [Java-Klassen, die betroffen sind]
Tests: [Welche Tests werden erwartet]
API: [Endpoint-Definition falls relevant]
DoD: [Checkliste aus definition-of-done.md]
```

### Bugfix
```
Kontext: [Beschreibung des Bugs]
Reproduktion: [Schritte zur Reproduktion]
Erwartetes Verhalten: [Was sollte passieren]
Aktuelles Verhalten: [Was passiert stattdessen]
Root Cause: [Falls bekannt]
Fix: [Was soll geändert werden]
Tests: [Regression-Test erforderlich]
```

### Refactoring
```
Kontext: [Warum ist Refactoring notwendig]
Scope: [Welche Klassen/Packages betroffen]
Ziel: [Was soll verbessert werden]
Constraints: [Was darf sich NICHT ändern - APIs, Tests]
Tests: [Bestehende Tests müssen grün bleiben]
```

### Architektur-Entscheidung
```
Problem: [Welches Problem wird gelöst]
Optionen: [2-4 betrachtete Alternativen]
Kriterien: [Entscheidungskriterien]
Empfehlung: [Falls vorhanden]
Output: ADR nach Template in /docs/06-decisions/adr-template.md
```

## Anti-Halluzinations-Regeln

### Für LLM
- Bei Unsicherheit explizit nachfragen
- Keine Dependencies erfinden
- Keine API-Endpoints erfinden
- Keine Features implementieren, die nicht angefordert wurden
- Bei fehlendem Kontext um Dateien bitten statt zu raten

### Für Entwickler
- Nach Code-Generierung IMMER Review durchführen
- Tests ausführen vor Commit
- Compiler-Output prüfen
- Generierte Dependencies in pom.xml verifizieren

## Qualitätskontrolle

Nach jeder Code-Generierung:
1. Führe `/docs/05-quality/review-checklist.md` durch
2. Prüfe `/docs/05-quality/definition-of-done.md`
3. Vergleiche mit `/docs/05-quality/code-standards.md`
4. Checke gegen `/docs/05-quality/anti-patterns.md`

## Beispiel-Prompt

```
Kontext:
- Datenmodell existiert (siehe /docs/01-architecture/data-model.md)
- DB-Migration ist vorhanden
- Keine API-Endpoints implementiert

Ziel:
Implementiere REST-API für Buchungsverwaltung gemäß REQ-001 bis REQ-005
(siehe /docs/02-requirements/functional-requirements.md)

Technische Anforderungen:
- Controller in org.example.foodtruckbookingservice.controller
- Service-Layer für Business-Logik
- DTOs für Request/Response (keine Entities nach außen)
- Bean Validation auf DTOs
- OpenAPI-Dokumentation via Annotations

API-Contract:
Siehe /docs/02-requirements/api-contracts.md für exakte Endpunkte

Tests:
- Controller-Tests mit @WebMvcTest
- Service-Tests mit Mockito
- Integration-Tests mit Testcontainers

Mandatory Context:
Siehe /docs/03-prompts/mandatory-context.md

Forbidden:
Siehe /docs/03-prompts/forbidden-actions.md

Definition of Done:
Siehe /docs/05-quality/definition-of-done.md
```

## Nacharbeit-Protokoll

Falls LLM etwas generiert, das nicht passt:
1. Dokumentiere das Problem in `/docs/05-quality/anti-patterns.md`
2. Präzisiere den Prompt
3. Referenziere das Anti-Pattern beim nächsten Mal

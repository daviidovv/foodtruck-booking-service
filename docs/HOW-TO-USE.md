# HOW TO USE: LLM-Dokumentationsstruktur

## 🎯 Das musst du verstehen

Diese Dokumentationsstruktur ist dein **Kontroll-System** für LLM-gestützte Entwicklung.

**Das Problem ohne Struktur:**
- LLM vergisst nach jeder Session alles
- Code wird inkonsistent
- Standards werden ignoriert
- Entscheidungen gehen verloren

**Die Lösung mit Struktur:**
- Alle Standards in Dateien → LLM bekommt sie bei jedem Prompt
- Alle Entscheidungen dokumentiert → Nachvollziehbar
- Klare Prozesse → Wiederholbar

---

## 📋 Quick Start: Die 3 Phasen

### Phase 1: Setup (JETZT - einmalig)

**Was:** Fülle die Grund-Dokumente aus

**Reihenfolge:**
1. **/docs/00-meta/vision.md** - Was ist dein Projekt?
2. **/docs/03-prompts/mandatory-context.md** - Tech-Stack + Regeln
3. **/docs/03-prompts/forbidden-actions.md** - Was darf das LLM NICHT?
4. **/docs/01-architecture/tech-stack.md** - Welche Technologien?
5. **/docs/05-quality/code-standards.md** - Wie soll Code aussehen?

**Wie?**
```
Prompt an LLM: "Ich erkläre dir mein Projekt. Schreibe die Vision in /docs/00-meta/vision.md:

[Deine Erklärung: Was ist das Projekt? Für wen? Was soll es können?]"
```

Dann nacheinander die anderen Dateien füllen lassen.

---

### Phase 2: Feature entwickeln (wiederkehrend)

**1. Requirements definieren**
```
Prompt: "Schreibe Requirements für Feature [X] in:
- /docs/02-requirements/functional-requirements.md
- /docs/02-requirements/user-stories.md

Feature: [Beschreibung]"
```

**2. API-Contract definieren (wenn API betroffen)**
```
Prompt: "Definiere API-Endpoints für Feature [X] in:
/docs/02-requirements/api-contracts.md

Endpoints: [Welche Endpoints]
Request/Response: [Schemas]"
```

**3. Task erstellen**
```
Prompt: "Erstelle Task-Datei in /docs/04-tasks/active/ basierend auf task-template.md:

Task: [Beschreibung]
Requirements: REQ-XXX
Akzeptanzkriterien: [Liste]"
```

**4. Feature implementieren** 🔴 WICHTIG
```
Prompt: "Implementiere Task TASK-XXX.

🔴 PFLICHT - Context Files:
- /docs/03-prompts/mandatory-context.md
- /docs/03-prompts/forbidden-actions.md
- /docs/05-quality/definition-of-done.md

Zusätzlich:
- /docs/04-tasks/active/TASK-XXX-[name].md
- /docs/02-requirements/api-contracts.md (falls API betroffen)
- /docs/05-quality/code-standards.md

Implementiere: [Details]"
```

**5. Review & Abschluss**
```
Prompt: "Führe Review durch mit:
/docs/05-quality/review-checklist.md

Prüfe alle Punkte!"
```

```
Prompt: "Task abschließen: Verschiebe nach /docs/04-tasks/completed/ und aktualisiere /docs/00-meta/changelog.md"
```

---

### Phase 3: Wartung (regelmäßig)

**Wöchentlich:**
- Changelog aktualisieren
- Tasks archivieren
- mandatory-context.md bei Änderungen aktualisieren

**Nach jedem Feature:**
- Definition of Done prüfen
- ADR erstellen (bei Architektur-Entscheidungen)
- Anti-Patterns erweitern (bei gefundenen Problemen)

**Monatlich:**
- Review durchführen (review-template.md)
- Tech Debt bewerten

---

## 🔴 Die 2 wichtigsten Dateien

### 1. mandatory-context.md
**Was:** Alles, was das LLM IMMER wissen muss
**Wann verwenden:** Bei JEDEM Code-Prompt
**Inhalt:**
- Tech-Stack (Java X, Spring Boot X, ...)
- Package-Struktur
- Coding-Constraints
- Aktuelle DB-Struktur
- API-Standards

**Aktualisieren nach:**
- Tech-Stack-Updates
- Datenbank-Migrationen
- Geänderten Conventions

### 2. forbidden-actions.md
**Was:** Explizite Verbote für das LLM
**Wann verwenden:** Bei JEDEM Code-Prompt
**Inhalt:**
- Verbotene Code-Änderungen
- Verbotene Architektur-Änderungen
- Anti-Patterns
- Projektspezifische Verbote

**Erweitern wenn:**
- LLM macht etwas Falsches
- Neues Problem entdeckt

---

## 📦 Welche Files wann mitgeben?

### IMMER bei Code (Pflicht!)
```
- /docs/03-prompts/mandatory-context.md
- /docs/03-prompts/forbidden-actions.md
- /docs/05-quality/definition-of-done.md
```

### Feature-Implementation
```
Pflicht-Files (siehe oben)
+ /docs/02-requirements/functional-requirements.md (relevante REQs)
+ /docs/02-requirements/api-contracts.md (wenn API)
+ /docs/04-tasks/active/TASK-XXX.md
+ /docs/05-quality/code-standards.md
```

### Bugfix
```
Pflicht-Files
+ Bug-Beschreibung
+ /docs/05-quality/anti-patterns.md
```

### Architektur-Entscheidung
```
- /docs/01-architecture/system-overview.md
+ /docs/06-decisions/adr-template.md
+ /docs/02-requirements/non-functional-requirements.md
```

---

## 💡 Best Practices

### ✅ Mach das

1. **Fülle mandatory-context.md als ERSTES aus**
   - Ohne diese Datei bringt der Rest nichts

2. **Referenziere IMMER die Pflicht-Dateien**
   - mandatory-context.md
   - forbidden-actions.md
   - definition-of-done.md

3. **Halte mandatory-context.md aktuell**
   - Nach jedem größeren Change aktualisieren

4. **Erweitere forbidden-actions.md bei Problemen**
   - LLM macht Fehler → in forbidden-actions.md eintragen

5. **Nutze session-start.md**
   - Spart Zeit bei neuen Sessions

6. **Schreibe API-Contracts VOR der Implementation**
   - Verhindert Missverständnisse

7. **Dokumentiere Entscheidungen in ADRs**
   - Warum wurde Technologie X gewählt?

### ❌ Mach das NICHT

1. **Dateien nicht ausfüllen**
   - Leere Struktur bringt nichts

2. **Pflicht-Files weglassen**
   - LLM generiert dann inkonsistenten Code

3. **mandatory-context.md veraltet lassen**
   - LLM arbeitet mit falschen Annahmen

4. **Keine Definition of Done prüfen**
   - Features sind dann nicht wirklich fertig

5. **Requirements direkt implementieren**
   - Erst Requirements schreiben, dann implementieren

---

## 🔄 Typischer Workflow: Feature "Benutzer-Registrierung"

### Schritt 1: Requirements (5 Min)
```
Prompt: "Schreibe Requirements für Benutzer-Registrierung:

/docs/02-requirements/functional-requirements.md:
- REQ-001: Benutzer kann sich mit E-Mail/Passwort registrieren
- Validierung: E-Mail-Format, Passwort min. 8 Zeichen
- Keine Duplikate

/docs/02-requirements/user-stories.md:
- US-001: Als neuer Benutzer möchte ich mich registrieren...
"
```

### Schritt 2: API-Contract (5 Min)
```
Prompt: "Definiere API für Registrierung in /docs/02-requirements/api-contracts.md:

POST /api/v1/users
Request: { email, password, name }
Response: { id, email, name, createdAt }
Errors: 400 (validation), 409 (duplicate)"
```

### Schritt 3: Task erstellen (2 Min)
```
Prompt: "Erstelle Task TASK-001 in /docs/04-tasks/active/ basierend auf task-template.md:

Titel: User Registration API
Requirements: REQ-001
Komponenten: Controller, Service, Repository, DTOs"
```

### Schritt 4: Implementieren (20 Min)
```
Prompt: "Implementiere TASK-001.

🔴 Context:
- /docs/03-prompts/mandatory-context.md
- /docs/03-prompts/forbidden-actions.md
- /docs/04-tasks/active/TASK-001-user-registration.md
- /docs/02-requirements/api-contracts.md
- /docs/05-quality/code-standards.md

Erstelle:
- Entity: User
- Repository: UserRepository
- Service: UserService
- Controller: UserController
- DTOs: UserRequest, UserResponse
- Tests: Unit + Integration

Definition of Done: /docs/05-quality/definition-of-done.md"
```

### Schritt 5: Review (5 Min)
```
Prompt: "Review den Code mit /docs/05-quality/review-checklist.md"
```

### Schritt 6: Abschluss (2 Min)
```
Prompt: "Task abschließen:
- Verschiebe TASK-001 nach /docs/04-tasks/completed/
- Update /docs/00-meta/changelog.md"
```

**Gesamt: ~40 Minuten für ein vollständig dokumentiertes, getestetes Feature**

---

## 🆘 Troubleshooting

### Problem: "LLM generiert inkonsistenten Code"
**Lösung:** Hast du mandatory-context.md + forbidden-actions.md referenziert?

### Problem: "LLM macht immer wieder den gleichen Fehler"
**Lösung:** Füge den Fehler in forbidden-actions.md ein

### Problem: "Ich weiß nicht, welche Dateien ich mitgeben soll"
**Lösung:** Siehe Abschnitt "Welche Files wann mitgeben?"

### Problem: "mandatory-context.md ist veraltet"
**Lösung:** Aktualisiere sie nach jedem größeren Change

### Problem: "Zu viele Dateien zum Mitgeben"
**Lösung:** Die 3 Pflicht-Files reichen für einfache Tasks. Mehr nur bei Bedarf.

---

## 📚 Datei-Übersicht

| Datei | Zweck | Wann verwenden |
|-------|-------|----------------|
| **00-meta/vision.md** | Projektziel | Onboarding, große Features |
| **00-meta/glossary.md** | Fachbegriffe | Fachliche Diskussionen |
| **00-meta/changelog.md** | Änderungshistorie | Reviews, Status-Updates |
| **00-meta/session-start.md** | Session-Quick-Start | Neue Session |
| **01-architecture/system-overview.md** | Architektur-Big-Picture | Architektur-Entscheidungen |
| **01-architecture/tech-stack.md** | Technologien | Onboarding, Dependencies |
| **01-architecture/data-model.md** | DB-Schema | DB-Migrationen, Entities |
| **02-requirements/functional-requirements.md** | Was System können muss | Feature-Implementation |
| **02-requirements/non-functional-requirements.md** | Performance, Security | Architektur-Entscheidungen |
| **02-requirements/user-stories.md** | Features aus Nutzersicht | Feature-Planung |
| **02-requirements/api-contracts.md** | API-Definitionen | Controller/DTO-Implementation |
| **🔴 03-prompts/mandatory-context.md** | **IMMER bei Code** | **JEDER Code-Prompt** |
| **🔴 03-prompts/forbidden-actions.md** | **IMMER bei Code** | **JEDER Code-Prompt** |
| **03-prompts/prompt-guidelines.md** | Wie gute Prompts schreiben | Als Referenz |
| **03-prompts/prompt-templates.md** | Prompt-Vorlagen | Standard-Tasks |
| **04-tasks/task-template.md** | Task-Vorlage | Neue Tasks |
| **🔴 05-quality/definition-of-done.md** | **Fertigstellungs-Kriterien** | **Ende jedes Tasks** |
| **05-quality/code-standards.md** | Coding-Conventions | Code-Generierung |
| **05-quality/review-checklist.md** | Review-Punkte | Nach jedem Task |
| **05-quality/anti-patterns.md** | Verbotene Patterns | Zur Vermeidung von Fehlern |
| **06-decisions/adr-template.md** | ADR-Vorlage | Architektur-Entscheidungen |
| **07-reviews/review-template.md** | Review-Vorlage | Monatliche Reviews |

---

## 🎓 Zusammenfassung

1. **Setup:** Fülle die 5 wichtigsten Dateien aus (vision, mandatory-context, forbidden-actions, tech-stack, code-standards)

2. **Feature-Entwicklung:**
   - Requirements → API-Contract → Task → Implementation (mit Pflicht-Files!) → Review → Abschluss

3. **Bei JEDEM Code-Prompt:**
   - mandatory-context.md
   - forbidden-actions.md
   - definition-of-done.md

4. **Wartung:**
   - mandatory-context.md aktuell halten
   - forbidden-actions.md erweitern bei Problemen
   - Changelog pflegen

**Das war's! Mit dieser Struktur hast du volle Kontrolle über LLM-generierten Code.**

---

## 🚀 Nächste Schritte

1. Fülle als ERSTES aus: `/docs/03-prompts/mandatory-context.md`
2. Dann: `/docs/03-prompts/forbidden-actions.md`
3. Dann: `/docs/00-meta/vision.md`
4. Starte mit deinem ersten Feature!

Bei Fragen: Lies diese Datei nochmal durch. Alles ist erklärt. 😊

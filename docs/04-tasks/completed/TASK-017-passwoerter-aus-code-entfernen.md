# TASK-017: Passwörter aus Code entfernen

## Status
- [x] Neu
- [x] In Bearbeitung
- [x] Erledigt

## Ziel

Die hardcodierten Passwörter aus `SecurityConfig.java` entfernen und stattdessen Environment-Variablen verwenden. So können Passwörter sicher konfiguriert werden ohne den Code zu ändern.

## Problem (aktuell)

```java
// SecurityConfig.java - UNSICHER!
.password(passwordEncoder.encode("wagen1"))   // Passwort im Code!
.password(passwordEncoder.encode("wagen2"))   // Passwort im Code!
.password(passwordEncoder.encode("admin123")) // Passwort im Code!
```

**Risiken:**
- Jeder mit Zugriff auf GitHub sieht die Passwörter
- Passwort-Änderung erfordert Code-Änderung + Deployment
- Gleiche Passwörter in Dev und Prod

## Lösung

### 1. Environment-Variablen definieren

```properties
# application.properties
app.security.staff.wagen1.password=${STAFF_WAGEN1_PASSWORD:changeme}
app.security.staff.wagen2.password=${STAFF_WAGEN2_PASSWORD:changeme}
app.security.admin.password=${ADMIN_PASSWORD:changeme}
```

### 2. SecurityConfig anpassen

```java
@Value("${app.security.staff.wagen1.password}")
private String wagen1Password;

@Value("${app.security.staff.wagen2.password}")
private String wagen2Password;

@Value("${app.security.admin.password}")
private String adminPassword;

@Bean
public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
    var wagen1 = User.builder()
            .username("wagen1")
            .password(passwordEncoder.encode(wagen1Password))
            .roles("STAFF")
            .build();
    // ... etc
}
```

### 3. Produktions-Konfiguration

In `docker-compose.deploy.yml` oder `.env` auf dem Server:

```yaml
environment:
  - STAFF_WAGEN1_PASSWORD=sicheres_passwort_hier
  - STAFF_WAGEN2_PASSWORD=anderes_sicheres_passwort
  - ADMIN_PASSWORD=sehr_sicheres_admin_passwort
```

## Akzeptanzkriterien

- [x] Keine Passwörter mehr im Java-Code
- [x] Passwörter werden aus Environment-Variablen gelesen
- [x] Default-Werte für lokale Entwicklung (z.B. `changeme`)
- [x] Dokumentation welche Env-Vars gesetzt werden müssen
- [x] docker-compose.deploy.yml mit Platzhaltern für Passwörter

## Testplan

1. Lokal starten ohne Env-Vars → Default-Passwörter funktionieren
2. Lokal starten mit Env-Vars → Neue Passwörter funktionieren
3. Login mit alten Passwörtern schlägt fehl
4. Login mit neuen Passwörtern funktioniert

## Hinweise

- Die Passwörter werden weiterhin mit BCrypt gehasht (das ist gut)
- Dies ist ein Zwischenschritt - Phase 2 wäre Passwörter in der Datenbank (TASK-018)
- ADMIN_PASSWORD sollte in Produktion SEHR sicher sein (min. 16 Zeichen)

# TASK-016: Security & DSGVO Audit

## Status
- [x] Neu

## Ziel

Analyse der aktuellen Sicherheitslücken und DSGVO-Anforderungen. Dieser Task dokumentiert was zu tun ist, bevor die Anwendung produktiv geht.

---

## Teil 1: Sicherheitsanalyse

### 1.1 Aktuelle Probleme (gefunden)

#### Kritisch: Hardcoded Passwörter
**Datei:** `src/main/java/.../config/SecurityConfig.java`

```java
// PROBLEM: Passwörter im Quellcode!
.password(passwordEncoder.encode("wagen1"))
.password(passwordEncoder.encode("wagen2"))
.password(passwordEncoder.encode("admin123"))
```

**Risiko:** Jeder mit Zugriff auf den Code kennt die Passwörter.

#### Kritisch: In-Memory User Store
```java
return new InMemoryUserDetailsManager(wagen1, wagen2, admin);
```

**Risiko:**
- Passwörter können nicht geändert werden ohne Code-Änderung
- Keine Audit-Logs wer sich einloggt
- Bei Neustart sind alle Sessions weg

#### Mittel: CSRF deaktiviert
```java
.csrf(AbstractHttpConfigurer::disable)
```

**Risiko:** Cross-Site Request Forgery Angriffe möglich (bei Browser-Nutzung relevant).

#### Mittel: Basic Auth statt JWT
```java
.httpBasic(withDefaults());
```

**Risiko:** Passwort wird bei jedem Request mitgeschickt (Base64 encoded, nicht verschlüsselt).

### 1.2 Empfohlene Maßnahmen

| Priorität | Maßnahme | Aufwand |
|-----------|----------|---------|
| **Hoch** | Passwörter in Datenbank speichern | Mittel |
| **Hoch** | Passwörter über Environment-Variablen | Gering |
| **Mittel** | JWT statt Basic Auth | Hoch |
| **Mittel** | Login-Versuche limitieren (Rate Limiting) | Mittel |
| **Niedrig** | CSRF für Browser-Sessions aktivieren | Gering |
| **Niedrig** | Password-Policy (Mindestlänge, etc.) | Gering |

---

## Teil 2: DSGVO-Anforderungen

### 2.1 Welche Daten werden gespeichert?

| Daten | Wo | Personenbezogen? |
|-------|-----|------------------|
| Name | Reservation.customerName | Ja |
| Telefon | Reservation.phoneNumber | Ja |
| Email | (nicht vorhanden?) | - |
| Reservierungscode | Reservation.confirmationCode | Nein |
| Bestelldetails | Reservation.chickenCount etc. | Nein |

### 2.2 DSGVO-Checkliste

| Anforderung | Status | Aktion nötig |
|-------------|--------|--------------|
| **Datenschutzerklärung** | ❌ Fehlt | Seite erstellen |
| **Impressum** | ❌ Fehlt | Seite erstellen |
| **Einwilligung bei Reservierung** | ❌ Fehlt | Checkbox hinzufügen |
| **Recht auf Löschung** | ⚠️ Teilweise | Kunden können Reservierung stornieren |
| **Recht auf Auskunft** | ❌ Fehlt | Export-Funktion? |
| **Daten-Löschfrist** | ❌ Fehlt | Automatische Löschung nach X Tagen |
| **Cookie-Banner** | ❓ Prüfen | Falls Cookies verwendet werden |
| **SSL/HTTPS** | ❓ Prüfen | Server-Konfiguration |

### 2.3 Empfohlene Maßnahmen

| Priorität | Maßnahme | Aufwand |
|-----------|----------|---------|
| **Pflicht** | Impressum erstellen | Gering |
| **Pflicht** | Datenschutzerklärung erstellen | Mittel |
| **Pflicht** | Einwilligungs-Checkbox bei Reservierung | Gering |
| **Empfohlen** | Automatische Löschung alter Reservierungen | Mittel |
| **Empfohlen** | Kunden-Datenexport (auf Anfrage) | Mittel |

---

## Teil 3: Konkrete Tasks (nach Priorisierung)

### Phase 1: Kritische Sicherheit (vor Go-Live)

- [ ] **TASK-017**: Passwörter aus Code entfernen → Environment-Variablen oder DB
- [ ] **TASK-018**: User/Passwörter in Datenbank speichern

### Phase 2: DSGVO Pflicht (vor Go-Live)

- [ ] **TASK-019**: Impressum & Datenschutzerklärung Seiten
- [ ] **TASK-020**: Einwilligungs-Checkbox bei Reservierungsformular

### Phase 3: Empfohlen (nach Go-Live)

- [ ] **TASK-021**: JWT-basierte Authentifizierung
- [ ] **TASK-022**: Automatische Löschung alter Reservierungen (z.B. nach 30 Tagen)
- [ ] **TASK-023**: Rate Limiting für Login-Versuche

---

## Notizen

- Das Frontend läuft auf `localhost:3000` (Entwicklung) oder eingebettet im Backend (Produktion)
- Staff-Login verwendet Basic Auth über `/api/v1/staff/**` Endpoints
- Admin hat derzeit Passwort `admin123` - MUSS geändert werden!

---

## Nächster Schritt

Entscheide welche Tasks zuerst umgesetzt werden sollen. Empfehlung:
1. TASK-017 (Passwörter aus Code) - **kritisch**
2. TASK-019 (Impressum/Datenschutz) - **Pflicht**

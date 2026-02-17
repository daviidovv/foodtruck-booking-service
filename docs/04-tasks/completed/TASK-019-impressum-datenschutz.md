# TASK-019: Impressum & Datenschutzerklärung

## Status
- [x] Neu
- [x] In Bearbeitung
- [x] Erledigt

## Ziel

Rechtlich notwendige Seiten für den Go-Live erstellen: Impressum und Datenschutzerklärung. Diese sind in Deutschland Pflicht für jede kommerzielle Website.

## Anforderungen

### 1. Impressum (Pflichtangaben nach § 5 TMG)

- Name des Unternehmens / Betreibers
- Anschrift (Straße, PLZ, Ort)
- Kontaktdaten (E-Mail, Telefon)
- Ggf. Handelsregisternummer
- Ggf. Umsatzsteuer-ID

### 2. Datenschutzerklärung (DSGVO)

Muss enthalten:
- Verantwortlicher (Name, Adresse, Kontakt)
- Welche Daten werden erhoben? (Name, Telefon bei Reservierung)
- Warum werden die Daten erhoben? (Reservierungsabwicklung)
- Wie lange werden Daten gespeichert?
- Rechte der Nutzer (Auskunft, Löschung, Widerspruch)
- Keine Cookies? → Trotzdem erwähnen

## Umsetzung

### Option A: Statische Seiten im Frontend
```
/impressum
/datenschutz
```
- Einfache React-Komponenten mit Text
- Links im Footer der Seite

### Option B: Backend-API + Frontend
- Text in Datenbank oder Konfiguration
- Flexibler, aber mehr Aufwand

**Empfehlung:** Option A (statische Seiten) - einfacher und ausreichend

## Akzeptanzkriterien

- [x] Impressum-Seite unter `/impressum` erreichbar
- [x] Datenschutz-Seite unter `/datenschutz` erreichbar
- [x] Links zu beiden Seiten im Footer sichtbar
- [ ] Inhalt rechtlich korrekt (vom Betreiber geprüft)

## Betreiber-Daten

```
Firmenname: Hendl Heinrich GmbH
Betreiber:  Siegfried Heinrich
Adresse:    Am Mitterweg 4
            D-83209 Prien a. Chiemsee
E-Mail:     info@hendl-heinrich.de (Platzhalter)
Telefon:    0171 7105524
```

## Hinweise

- E-Mail ist aktuell Platzhalter (info@hendl-heinrich.de) - echte E-Mail nachtragen!
- Es gibt Online-Generatoren für Datenschutzerklärungen (z.B. e-recht24.de)
- Impressum-Generator: z.B. impressum-generator.de

## Beispiel-Struktur Datenschutzerklärung

```
1. Verantwortlicher
2. Erhobene Daten
   - Bei Reservierung: Name, Telefonnummer
3. Zweck der Datenverarbeitung
   - Abwicklung der Reservierung
   - Kontaktaufnahme bei Problemen
4. Speicherdauer
   - Reservierungsdaten werden nach X Tagen gelöscht
5. Ihre Rechte
   - Auskunft, Berichtigung, Löschung
6. Kontakt für Datenschutzanfragen
```

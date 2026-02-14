# TASK-007: Admin-Statistik-Dashboard

## Status
- [x] Neu
- [ ] In Bearbeitung
- [ ] Review
- [ ] Abgeschlossen

## Priorität
**Phase 2** - Nach MVP-Launch und ersten echten Daten

## Kontext
Betreiber benötigen Einblick in Verkaufszahlen und Auslastung, um bessere Geschäftsentscheidungen zu treffen. Zusätzlich soll das Wetter gespeichert werden, um Korrelationen zwischen Wetter und Verkauf analysieren zu können.

## Ziel
Admin-Dashboard mit Statistiken über verkaufte Hähnchen, Auslastung pro Standort und Wetter-Korrelation.

## Entscheidungen (bereits geklärt)

| Frage | Entscheidung |
|-------|--------------|
| Umsatz tracken? | Nein, nur Mengen (Hähnchen/Pommes) |
| Wetter-Daten? | Ja, automatisch via OpenWeatherMap API |
| Wetter-Ort? | Pro Location (Koordinaten speichern) |
| Zeiträume? | Vordefiniert (Tag/Woche/Monat) + freie Auswahl |
| Zugriff? | Nur Admin |
| Statistiken Prio? | Verkaufte Hähnchen, Auslastung |
| Export? | Nein, nur Dashboard-Anzeige |

## Akzeptanzkriterien

- [ ] Location-Entity hat latitude/longitude Felder
- [ ] DailyWeather-Entity speichert Wetter pro Tag/Standort
- [ ] WeatherService holt Daten von OpenWeatherMap API
- [ ] Wetter wird täglich automatisch gespeichert (Scheduler)
- [ ] StatisticsService berechnet Verkaufszahlen und Auslastung
- [ ] GET /admin/statistics Endpoint liefert Dashboard-Daten
- [ ] Zeitraum-Filter funktioniert (Tag, Woche, Monat, custom)
- [ ] Nur ROLE_ADMIN hat Zugriff
- [ ] Tests geschrieben und erfolgreich

## Technische Umsetzung

### Datenbank-Migration (V4)

```sql
-- Location: Koordinaten hinzufügen
ALTER TABLE location ADD COLUMN latitude DECIMAL(10, 8);
ALTER TABLE location ADD COLUMN longitude DECIMAL(11, 8);

-- Wetter-Tabelle
CREATE TABLE daily_weather (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL REFERENCES location(id),
    date DATE NOT NULL,
    temperature DECIMAL(4, 1),           -- z.B. 23.5°C
    feels_like DECIMAL(4, 1),
    condition VARCHAR(50),               -- sunny, cloudy, rainy, etc.
    condition_icon VARCHAR(10),          -- OpenWeatherMap icon code
    humidity INTEGER,                    -- Prozent
    wind_speed DECIMAL(4, 1),            -- m/s
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(location_id, date)
);
```

### Neue Komponenten

| Komponente | Beschreibung |
|------------|--------------|
| `DailyWeather` | Entity für Wetterdaten |
| `DailyWeatherRepository` | Repository |
| `WeatherService` | OpenWeatherMap API Client |
| `WeatherScheduler` | Täglicher Job zum Wetter-Abruf |
| `StatisticsService` | Berechnet alle Statistiken |
| `StatisticsController` | Admin-Endpoints |
| `StatisticsResponse` | DTO für Dashboard-Daten |

### API Endpoints

```
GET /api/v1/admin/statistics
    ?from=2026-01-01
    &to=2026-01-31
    &locationId={uuid}     (optional, sonst alle)

Response:
{
    "period": { "from": "2026-01-01", "to": "2026-01-31" },
    "summary": {
        "totalChickensSold": 450,
        "totalReservations": 120,
        "averagePerDay": 15,
        "averageUtilization": 78.5
    },
    "byLocation": [
        {
            "locationId": "...",
            "locationName": "Innenstadt",
            "chickensSold": 280,
            "reservations": 75,
            "utilization": 82.3
        }
    ],
    "daily": [
        {
            "date": "2026-01-15",
            "chickensSold": 25,
            "utilization": 83.3,
            "weather": {
                "temperature": 18.5,
                "condition": "sunny"
            }
        }
    ]
}
```

### OpenWeatherMap Integration

- API: https://api.openweathermap.org/data/2.5/weather
- Free Tier: 1000 Calls/Tag (ausreichend)
- API Key in `application.yml`: `weather.api.key`
- Abruf: Täglich um 12:00 Uhr für jeden aktiven Standort

## Abhängigkeiten

- **Voraussetzung**: TASK-001 bis TASK-006 abgeschlossen ✅
- **Empfohlen**: MVP läuft produktiv mit echten Daten
- **Benötigt**: OpenWeatherMap API Key (kostenlose Registrierung)

## Betroffene Komponenten

- **Entity**: Location (erweitern), DailyWeather (neu)
- **Repository**: DailyWeatherRepository (neu)
- **Service**: WeatherService (neu), StatisticsService (neu)
- **Controller**: StatisticsController (neu)
- **DTOs**: StatisticsResponse, WeatherResponse (neu)
- **DB-Migration**: V4

## Geschätzter Aufwand

- Migration + Entities: 1h
- WeatherService + Scheduler: 2h
- StatisticsService: 2h
- Controller + DTOs: 1h
- Tests: 2h

**Gesamt: ~8h**

## Offene Punkte für Implementierung

- [ ] OpenWeatherMap API Key besorgen
- [ ] Koordinaten der bestehenden Standorte eintragen

## Follow-up (optional, später)

- [ ] CSV-Export der Statistiken
- [ ] No-Show-Rate Analyse
- [ ] Grafische Charts im Frontend
- [ ] Vergleich mit Vorperiode

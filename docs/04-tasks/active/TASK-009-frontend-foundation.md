# TASK-009: Frontend Foundation

## Status
- [ ] Neu
- [x] In Bearbeitung
- [ ] Review
- [ ] Abgeschlossen

## Ziel
Modernes, erweiterbares Frontend für Kunden und Mitarbeiter mit gutem Design.

## Tech Stack

| Technologie | Zweck |
|-------------|-------|
| **React 18** | UI Framework |
| **TypeScript** | Type Safety |
| **Vite** | Build Tool (schnell!) |
| **Tailwind CSS** | Styling |
| **shadcn/ui** | UI Komponenten (modern, accessible) |
| **React Router** | Routing |
| **TanStack Query** | API State Management |
| **Lucide Icons** | Icons |

## Struktur

```
frontend/
├── src/
│   ├── components/          # Wiederverwendbare Komponenten
│   │   ├── ui/              # shadcn/ui Basis-Komponenten
│   │   ├── layout/          # Header, Footer, Navigation
│   │   └── shared/          # Gemeinsame Business-Komponenten
│   ├── features/            # Feature-basierte Module
│   │   ├── customer/        # Kunden-Features
│   │   │   ├── components/
│   │   │   ├── hooks/
│   │   │   └── pages/
│   │   ├── staff/           # Mitarbeiter-Features
│   │   └── admin/           # Admin-Features
│   ├── hooks/               # Globale Custom Hooks
│   ├── lib/                 # Utilities, API Client
│   ├── types/               # TypeScript Types
│   └── styles/              # Globale Styles
├── public/
└── package.json
```

## Seiten

### Kunde (Public)
- `/` - Startseite mit Standortauswahl
- `/availability` - Verfügbarkeit & Reservierung
- `/reservation/lookup` - Reservierung nachschlagen
- `/reservation/:code` - Reservierungsdetails

### Mitarbeiter (Auth)
- `/staff/login` - Login
- `/staff/dashboard` - Tagesübersicht
- `/staff/inventory` - Vorrat eintragen
- `/staff/reservations` - Reservierungsliste

### Admin (Auth)
- `/admin/locations` - Standortverwaltung
- `/admin/statistics` - Statistiken (Phase 2)

## Design-Prinzipien

1. **Mobile First** - Kunden nutzen Smartphones
2. **Tablet-optimiert** - Mitarbeiter nutzen Tablets
3. **Accessibility** - WCAG 2.1 konform
4. **Dark Mode** - Optional, aber vorbereitet
5. **Deutsch** - UI-Texte auf Deutsch

## Farbschema (Vorschlag)

```css
/* Warm, appetitlich, Foodtruck-Feeling */
--primary: #F97316;      /* Orange - Hauptfarbe */
--primary-dark: #EA580C;
--secondary: #FEF3C7;    /* Warm Yellow - Akzente */
--success: #22C55E;      /* Grün - Verfügbar */
--warning: #EAB308;      /* Gelb - Begrenzt */
--danger: #EF4444;       /* Rot - Ausverkauft */
--neutral: #1F2937;      /* Dark Gray - Text */
```

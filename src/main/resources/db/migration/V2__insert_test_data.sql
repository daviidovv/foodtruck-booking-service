-- =====================================================
-- Foodtruck Booking Service - Test Data
-- Version: V2
-- Date: 2026-02-11
-- Description: Inserts sample data for development and testing.
--              DO NOT use in production!
-- =====================================================

-- =====================================================
-- Locations
-- =====================================================
INSERT INTO location (id, name, address, active, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'Innenstadt', 'Marktplatz 1, 12345 Musterstadt', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'Gewerbegebiet', 'Industriestraße 42, 12345 Musterstadt', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- Location Schedules
-- Innenstadt: Monday, Wednesday, Friday
-- Gewerbegebiet: Tuesday, Thursday, Saturday
-- =====================================================

-- Innenstadt Schedule
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    -- Monday
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440000', 1, '11:00', '20:00', 50, true),
    -- Wednesday
    ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440000', 3, '11:00', '20:00', 50, true),
    -- Friday (higher capacity)
    ('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440000', 5, '11:00', '21:00', 60, true);

-- Gewerbegebiet Schedule
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    -- Tuesday
    ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 2, '11:00', '19:00', 40, true),
    -- Thursday
    ('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 4, '11:00', '19:00', 40, true),
    -- Saturday (earlier hours, slightly more capacity)
    ('660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 6, '10:00', '18:00', 45, true);

-- =====================================================
-- Sample Reservations (various statuses)
-- Using future dates relative to common test scenarios
-- =====================================================

-- PENDING reservations
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status, notes, created_at, updated_at)
VALUES
    ('770e8400-e29b-41d4-a716-446655440001',
     '550e8400-e29b-41d4-a716-446655440000',
     'Max Mustermann',
     'max@example.com',
     2, 3,
     CURRENT_DATE + INTERVAL '1 day' + TIME '14:30',
     'PENDING',
     'Bitte extra knusprig',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('770e8400-e29b-41d4-a716-446655440002',
     '550e8400-e29b-41d4-a716-446655440000',
     'Anna Schmidt',
     NULL,  -- No email provided
     1, 2,
     CURRENT_DATE + INTERVAL '1 day' + TIME '15:00',
     'PENDING',
     NULL,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- CONFIRMED reservations
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status, notes, created_at, updated_at)
VALUES
    ('770e8400-e29b-41d4-a716-446655440003',
     '550e8400-e29b-41d4-a716-446655440000',
     'Peter Müller',
     'peter.mueller@firma.de',
     3, 4,
     CURRENT_DATE + INTERVAL '1 day' + TIME '12:30',
     'CONFIRMED',
     'Firmenbestellung',
     CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),

    ('770e8400-e29b-41d4-a716-446655440004',
     '550e8400-e29b-41d4-a716-446655440001',
     'Lisa Weber',
     'lisa.w@mail.com',
     2, 2,
     CURRENT_DATE + INTERVAL '2 days' + TIME '13:00',
     'CONFIRMED',
     NULL,
     CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '12 hours');

-- COMPLETED reservation (historical)
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status, notes, created_at, updated_at)
VALUES
    ('770e8400-e29b-41d4-a716-446655440005',
     '550e8400-e29b-41d4-a716-446655440000',
     'Thomas Klein',
     'thomas.k@web.de',
     1, 1,
     CURRENT_DATE - INTERVAL '1 day' + TIME '18:00',
     'COMPLETED',
     NULL,
     CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE - INTERVAL '1 day');

-- CANCELLED reservation
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status, notes, created_at, updated_at)
VALUES
    ('770e8400-e29b-41d4-a716-446655440006',
     '550e8400-e29b-41d4-a716-446655440001',
     'Maria Schneider',
     'maria.s@gmail.com',
     4, 5,
     CURRENT_DATE + INTERVAL '3 days' + TIME '12:00',
     'CANCELLED',
     'Kunde hat telefonisch storniert',
     CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days');

-- NO_SHOW reservation (historical)
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, pickup_time, status, notes, created_at, updated_at)
VALUES
    ('770e8400-e29b-41d4-a716-446655440007',
     '550e8400-e29b-41d4-a716-446655440000',
     'Frank Hoffmann',
     NULL,
     2, 0,
     CURRENT_DATE - INTERVAL '2 days' + TIME '14:00',
     'NO_SHOW',
     'Nicht erschienen, keine Kontaktmöglichkeit',
     CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '2 days');

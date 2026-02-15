-- =====================================================
-- Development Data for Local Testing
-- This is a REPEATABLE migration (R__) - runs when changed
-- DO NOT use in production!
-- =====================================================

-- =====================================================
-- Additional Schedules for Mo, Sa, So (Testing only)
-- This allows testing the staff login any day of the week
-- =====================================================

-- Delete existing test schedules (Mo, Sa, So only)
DELETE FROM location_schedule WHERE id IN (
    'b0000000-0000-0000-0000-000000000101',
    'b0000000-0000-0000-0000-000000000102',
    'b0000000-0000-0000-0000-000000000103',
    'b0000000-0000-0000-0000-000000000104',
    'b0000000-0000-0000-0000-000000000105',
    'b0000000-0000-0000-0000-000000000106'
);

-- Montag (1) - Test-Tag
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000101', 'a0000000-0000-0000-0000-000000000001', 1, '11:00', '19:00', 50, true),  -- Traunreut (Wagen 1)
    ('b0000000-0000-0000-0000-000000000102', 'a0000000-0000-0000-0000-000000000002', 1, '11:00', '19:00', 50, true);  -- Raubling (Wagen 2)

-- Samstag (6) - Test-Tag
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000103', 'a0000000-0000-0000-0000-000000000003', 6, '11:00', '19:00', 50, true),  -- Mitterfelden (Wagen 1)
    ('b0000000-0000-0000-0000-000000000104', 'a0000000-0000-0000-0000-000000000004', 6, '11:00', '19:00', 50, true);  -- Bad Endorf (Wagen 2)

-- Sonntag (7) - Test-Tag
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000105', 'a0000000-0000-0000-0000-000000000005', 7, '11:00', '19:00', 50, true),  -- Siegsdorf (Wagen 1)
    ('b0000000-0000-0000-0000-000000000106', 'a0000000-0000-0000-0000-000000000006', 7, '11:00', '19:00', 50, true);  -- Bruckmühl (Wagen 2)


-- =====================================================
-- Sample Daily Inventory for Testing
-- =====================================================

DELETE FROM daily_inventory WHERE id IN (
    'd0000000-0000-0000-0000-000000000001',
    'd0000000-0000-0000-0000-000000000002',
    'd0000000-0000-0000-0000-000000000003',
    'd0000000-0000-0000-0000-000000000004',
    'd0000000-0000-0000-0000-000000000005',
    'd0000000-0000-0000-0000-000000000006',
    'd0000000-0000-0000-0000-000000000007'
);

-- Inventory for all locations for today
INSERT INTO daily_inventory (id, location_id, date, total_chickens, created_at, updated_at)
VALUES
    ('d0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', CURRENT_DATE, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Traunreut
    ('d0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', CURRENT_DATE, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Raubling
    ('d0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', CURRENT_DATE, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Mitterfelden
    ('d0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', CURRENT_DATE, 55, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Bad Endorf
    ('d0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', CURRENT_DATE, 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Siegsdorf
    ('d0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', CURRENT_DATE, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),  -- Bruckmühl
    ('d0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000007', CURRENT_DATE, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);  -- Prien


-- =====================================================
-- Sample Reservations for Testing
-- =====================================================

-- Clear existing sample reservations
DELETE FROM reservation WHERE id IN (
    'c0000000-0000-0000-0000-000000000001',
    'c0000000-0000-0000-0000-000000000002',
    'c0000000-0000-0000-0000-000000000003',
    'c0000000-0000-0000-0000-000000000004',
    'c0000000-0000-0000-0000-000000000005',
    'c0000000-0000-0000-0000-000000000006'
);

-- Sample reservations with various statuses
INSERT INTO reservation (id, location_id, customer_name, customer_email, chicken_count, fries_count, reservation_date, pickup_time, status, confirmation_code, notes, created_at, updated_at)
VALUES
    -- CONFIRMED reservations
    ('c0000000-0000-0000-0000-000000000001',
     'a0000000-0000-0000-0000-000000000001',  -- Traunreut
     'Max Mustermann',
     'max@example.com',
     2, 3,
     CURRENT_DATE,
     '14:30',
     'CONFIRMED',
     'TEST0001',
     'Bitte extra knusprig',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('c0000000-0000-0000-0000-000000000002',
     'a0000000-0000-0000-0000-000000000002',  -- Raubling
     'Anna Schmidt',
     NULL,
     1, 2,
     CURRENT_DATE,
     '15:00',
     'CONFIRMED',
     'TEST0002',
     NULL,
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

    ('c0000000-0000-0000-0000-000000000003',
     'a0000000-0000-0000-0000-000000000003',  -- Mitterfelden
     'Peter Müller',
     'peter.mueller@firma.de',
     3, 4,
     CURRENT_DATE,
     '12:30',
     'CONFIRMED',
     'TEST0003',
     'Firmenbestellung',
     CURRENT_TIMESTAMP - INTERVAL '2 hours', CURRENT_TIMESTAMP - INTERVAL '1 hour'),

    -- COMPLETED reservation
    ('c0000000-0000-0000-0000-000000000004',
     'a0000000-0000-0000-0000-000000000005',  -- Siegsdorf
     'Thomas Klein',
     'thomas.k@web.de',
     1, 1,
     CURRENT_DATE - INTERVAL '1 day',
     '18:00',
     'COMPLETED',
     'TEST0004',
     NULL,
     CURRENT_DATE - INTERVAL '2 days', CURRENT_DATE - INTERVAL '1 day'),

    -- CANCELLED reservation
    ('c0000000-0000-0000-0000-000000000005',
     'a0000000-0000-0000-0000-000000000006',  -- Bruckmühl
     'Maria Schneider',
     'maria.s@gmail.com',
     4, 5,
     CURRENT_DATE + INTERVAL '3 days',
     '12:00',
     'CANCELLED',
     'TEST0005',
     'Kunde hat telefonisch storniert',
     CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),

    -- NO_SHOW reservation
    ('c0000000-0000-0000-0000-000000000006',
     'a0000000-0000-0000-0000-000000000007',  -- Prien
     'Frank Hoffmann',
     NULL,
     2, 0,
     CURRENT_DATE - INTERVAL '2 days',
     '14:00',
     'NO_SHOW',
     'TEST0006',
     'Nicht erschienen, keine Kontaktmöglichkeit',
     CURRENT_DATE - INTERVAL '3 days', CURRENT_DATE - INTERVAL '2 days');

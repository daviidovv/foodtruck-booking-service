-- =====================================================
-- Sample Reservations for Local Development
-- This is a REPEATABLE migration (R__) - runs when changed
-- DO NOT use in production!
-- =====================================================

-- Clear existing sample reservations (keeps real data safe by using specific IDs)
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
     CURRENT_DATE + INTERVAL '1 day',
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
     CURRENT_DATE + INTERVAL '1 day',
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
     CURRENT_DATE + INTERVAL '1 day',
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

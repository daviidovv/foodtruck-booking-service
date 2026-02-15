-- =====================================================
-- Foodtruck Booking Service - Base Data
-- Version: V2
-- Date: 2026-02-15
-- Description: Production locations and schedules
-- =====================================================

-- =====================================================
-- Locations (7 Standorte)
-- =====================================================
INSERT INTO location (id, name, address, active, created_at, updated_at)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Traunreut', 'Adresse Traunreut', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000002', 'Raubling', 'Adresse Raubling', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000003', 'Mitterfelden', 'Adresse Mitterfelden', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000004', 'Bad Endorf', 'Adresse Bad Endorf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000005', 'Siegsdorf', 'Adresse Siegsdorf', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000006', 'Bruckmühl', 'Adresse Bruckmühl', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('a0000000-0000-0000-0000-000000000007', 'Prien', 'Adresse Prien', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- =====================================================
-- Location Schedules
-- day_of_week: 1=Mo, 2=Di, 3=Mi, 4=Do, 5=Fr, 6=Sa, 7=So
-- =====================================================

-- Dienstag (2)
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000001', 'a0000000-0000-0000-0000-000000000001', 2, '11:00', '19:00', 50, true),  -- Traunreut
    ('b0000000-0000-0000-0000-000000000002', 'a0000000-0000-0000-0000-000000000002', 2, '11:00', '19:00', 50, true);  -- Raubling

-- Mittwoch (3)
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000003', 'a0000000-0000-0000-0000-000000000003', 3, '11:00', '19:00', 50, true),  -- Mitterfelden
    ('b0000000-0000-0000-0000-000000000004', 'a0000000-0000-0000-0000-000000000004', 3, '11:00', '19:00', 50, true);  -- Bad Endorf

-- Donnerstag (4)
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000005', 'a0000000-0000-0000-0000-000000000005', 4, '11:00', '19:00', 50, true),  -- Siegsdorf
    ('b0000000-0000-0000-0000-000000000006', 'a0000000-0000-0000-0000-000000000006', 4, '11:00', '19:00', 50, true);  -- Bruckmühl

-- Freitag (5)
INSERT INTO location_schedule (id, location_id, day_of_week, opening_time, closing_time, daily_capacity, active)
VALUES
    ('b0000000-0000-0000-0000-000000000007', 'a0000000-0000-0000-0000-000000000001', 5, '11:00', '19:00', 50, true),  -- Traunreut (2. Tag)
    ('b0000000-0000-0000-0000-000000000008', 'a0000000-0000-0000-0000-000000000007', 5, '11:00', '19:00', 50, true);  -- Prien

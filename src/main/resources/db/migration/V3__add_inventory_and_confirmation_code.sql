-- V3: Add daily_inventory table and confirmation_code to reservation
-- Changes the workflow to auto-accept reservations based on daily inventory

-- Daily Inventory Table (Täglicher Vorrat)
CREATE TABLE daily_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    date DATE NOT NULL,
    total_chickens INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_daily_inventory_location
        FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT uk_daily_inventory_location_date
        UNIQUE (location_id, date),
    CONSTRAINT chk_total_chickens
        CHECK (total_chickens >= 0)
);

CREATE INDEX idx_daily_inventory_location_date ON daily_inventory(location_id, date);

-- Add confirmation_code to reservation
ALTER TABLE reservation ADD COLUMN confirmation_code VARCHAR(8);

-- Generate codes for existing reservations (for migration)
UPDATE reservation SET confirmation_code = UPPER(SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 8))
WHERE confirmation_code IS NULL;

-- Now make it NOT NULL and UNIQUE
ALTER TABLE reservation ALTER COLUMN confirmation_code SET NOT NULL;
ALTER TABLE reservation ADD CONSTRAINT uk_reservation_confirmation_code UNIQUE (confirmation_code);

-- Add reservation_date column (derived from pickup_time for existing data)
ALTER TABLE reservation ADD COLUMN reservation_date DATE;
UPDATE reservation SET reservation_date = DATE(pickup_time) WHERE reservation_date IS NULL;
ALTER TABLE reservation ALTER COLUMN reservation_date SET NOT NULL;

-- Make pickup_time optional and change to TIME type
-- First, extract just the time portion
ALTER TABLE reservation ADD COLUMN pickup_time_new TIME;
UPDATE reservation SET pickup_time_new = pickup_time::TIME WHERE pickup_time IS NOT NULL;

-- Drop old column and rename new one
ALTER TABLE reservation DROP COLUMN pickup_time;
ALTER TABLE reservation RENAME COLUMN pickup_time_new TO pickup_time;

-- Update index for new date column
CREATE INDEX idx_reservation_date ON reservation(reservation_date);
DROP INDEX IF EXISTS idx_reservation_location_date;
CREATE INDEX idx_reservation_location_date ON reservation(location_id, reservation_date);

-- Remove PENDING status from existing reservations (migrate to CONFIRMED)
UPDATE reservation SET status = 'CONFIRMED' WHERE status = 'PENDING';

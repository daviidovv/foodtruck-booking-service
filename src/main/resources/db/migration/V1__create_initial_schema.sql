-- =====================================================
-- Foodtruck Booking Service - Initial Schema
-- Version: V1
-- Date: 2026-02-11
-- Description: Creates all core tables for the foodtruck
--              reservation system with multi-location support.
-- =====================================================

-- =====================================================
-- Table: location
-- Purpose: Stores foodtruck locations (e.g., "Innenstadt", "Gewerbegebiet")
-- =====================================================
CREATE TABLE location (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uk_location_name UNIQUE (name)
);

COMMENT ON TABLE location IS 'Foodtruck locations where reservations can be made';
COMMENT ON COLUMN location.name IS 'Unique location name (e.g., Innenstadt)';
COMMENT ON COLUMN location.address IS 'Full address of the location';
COMMENT ON COLUMN location.active IS 'Soft delete flag - false means location is deactivated';

-- =====================================================
-- Table: location_schedule
-- Purpose: Defines operating hours and capacity per location per weekday
-- =====================================================
CREATE TABLE location_schedule (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    day_of_week INT NOT NULL,
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL,
    daily_capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,

    -- Foreign Key
    CONSTRAINT fk_location_schedule_location
        FOREIGN KEY (location_id) REFERENCES location(id)
        ON DELETE CASCADE,

    -- Unique constraint: One schedule per location per day
    CONSTRAINT uk_location_schedule_location_day
        UNIQUE (location_id, day_of_week),

    -- Check constraints
    CONSTRAINT chk_day_of_week
        CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_daily_capacity
        CHECK (daily_capacity > 0),
    CONSTRAINT chk_opening_before_closing
        CHECK (opening_time < closing_time)
);

COMMENT ON TABLE location_schedule IS 'Weekly schedule per location with operating hours and capacity';
COMMENT ON COLUMN location_schedule.day_of_week IS 'ISO 8601 weekday: 1=Monday, 7=Sunday';
COMMENT ON COLUMN location_schedule.daily_capacity IS 'Maximum number of chickens available per day';
COMMENT ON COLUMN location_schedule.active IS 'Whether the location operates on this day';

-- =====================================================
-- Table: reservation
-- Purpose: Stores all customer reservations
-- =====================================================
CREATE TABLE reservation (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    location_id UUID NOT NULL,
    customer_name VARCHAR(200) NOT NULL,
    customer_email VARCHAR(255),  -- NULL allowed! (Design decision: low barrier)
    chicken_count INT NOT NULL,
    fries_count INT NOT NULL,
    pickup_time TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key
    CONSTRAINT fk_reservation_location
        FOREIGN KEY (location_id) REFERENCES location(id)
        ON DELETE RESTRICT,

    -- Check constraints
    CONSTRAINT chk_chicken_count_non_negative
        CHECK (chicken_count >= 0),
    CONSTRAINT chk_fries_count_non_negative
        CHECK (fries_count >= 0),
    CONSTRAINT chk_min_order_quantity
        CHECK (chicken_count + fries_count > 0),
    CONSTRAINT chk_status_valid
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'NO_SHOW'))
);

COMMENT ON TABLE reservation IS 'Customer reservations for chicken and fries pickup';
COMMENT ON COLUMN reservation.customer_email IS 'Optional email for confirmation (NULL allowed for low barrier)';
COMMENT ON COLUMN reservation.status IS 'PENDING -> CONFIRMED -> COMPLETED/NO_SHOW or CANCELLED';
COMMENT ON COLUMN reservation.pickup_time IS 'Requested pickup time within opening hours';

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Reservation indexes
CREATE INDEX idx_reservation_location_id ON reservation(location_id);
CREATE INDEX idx_reservation_pickup_time ON reservation(pickup_time);
CREATE INDEX idx_reservation_status ON reservation(status);
CREATE INDEX idx_reservation_location_pickup_date ON reservation(location_id, DATE(pickup_time));
CREATE INDEX idx_reservation_created_at ON reservation(created_at);

-- Location schedule indexes
CREATE INDEX idx_location_schedule_location_id ON location_schedule(location_id);

COMMENT ON INDEX idx_reservation_location_pickup_date IS 'Optimizes daily reservation queries per location';

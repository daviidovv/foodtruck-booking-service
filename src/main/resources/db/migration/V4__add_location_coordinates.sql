-- V4: Add coordinates (latitude/longitude) to location table
-- Used for Google Maps / Apple Maps links

ALTER TABLE location ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE location ADD COLUMN longitude DOUBLE PRECISION;

COMMENT ON COLUMN location.latitude IS 'GPS latitude coordinate for map links';
COMMENT ON COLUMN location.longitude IS 'GPS longitude coordinate for map links';

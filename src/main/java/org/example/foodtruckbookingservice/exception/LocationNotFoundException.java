package org.example.foodtruckbookingservice.exception;

import java.util.UUID;

/**
 * Exception thrown when a location is not found.
 */
public class LocationNotFoundException extends RuntimeException {

    private final UUID locationId;

    public LocationNotFoundException(UUID locationId) {
        super("Location not found with id: " + locationId);
        this.locationId = locationId;
    }

    public UUID getLocationId() {
        return locationId;
    }
}

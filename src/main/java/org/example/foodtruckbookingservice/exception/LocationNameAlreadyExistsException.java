package org.example.foodtruckbookingservice.exception;

/**
 * Exception thrown when a location name already exists.
 */
public class LocationNameAlreadyExistsException extends RuntimeException {

    private final String name;

    public LocationNameAlreadyExistsException(String name) {
        super("Location with name already exists: " + name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

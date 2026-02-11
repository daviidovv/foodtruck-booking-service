package org.example.foodtruckbookingservice.exception;

/**
 * Exception thrown when chicken capacity is exceeded.
 */
public class CapacityExceededException extends RuntimeException {

    private final int requested;
    private final int available;

    public CapacityExceededException(int requested, int available) {
        super(String.format("Capacity exceeded. Requested: %d, Available: %d", requested, available));
        this.requested = requested;
        this.available = available;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}

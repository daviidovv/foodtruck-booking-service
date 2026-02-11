package org.example.foodtruckbookingservice.exception;

import org.example.foodtruckbookingservice.entity.ReservationStatus;

/**
 * Exception thrown when an invalid status transition is attempted.
 */
public class InvalidStatusTransitionException extends RuntimeException {

    private final ReservationStatus currentStatus;
    private final ReservationStatus requestedStatus;

    public InvalidStatusTransitionException(ReservationStatus currentStatus, ReservationStatus requestedStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, requestedStatus));
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }

    public ReservationStatus getCurrentStatus() {
        return currentStatus;
    }

    public ReservationStatus getRequestedStatus() {
        return requestedStatus;
    }
}

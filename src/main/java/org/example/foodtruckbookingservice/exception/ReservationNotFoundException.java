package org.example.foodtruckbookingservice.exception;

import java.util.UUID;

/**
 * Exception thrown when a reservation is not found.
 */
public class ReservationNotFoundException extends RuntimeException {

    private final UUID reservationId;

    public ReservationNotFoundException(UUID reservationId) {
        super("Reservation not found with id: " + reservationId);
        this.reservationId = reservationId;
    }

    public UUID getReservationId() {
        return reservationId;
    }
}

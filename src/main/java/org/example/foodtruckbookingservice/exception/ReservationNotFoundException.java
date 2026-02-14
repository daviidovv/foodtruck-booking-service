package org.example.foodtruckbookingservice.exception;

import java.util.UUID;

/**
 * Exception thrown when a reservation is not found.
 */
public class ReservationNotFoundException extends RuntimeException {

    private final UUID reservationId;
    private final String confirmationCode;

    public ReservationNotFoundException(UUID reservationId) {
        super("Reservation not found with id: " + reservationId);
        this.reservationId = reservationId;
        this.confirmationCode = null;
    }

    public ReservationNotFoundException(String confirmationCode) {
        super("Reservation not found with code: " + confirmationCode);
        this.reservationId = null;
        this.confirmationCode = confirmationCode;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }
}

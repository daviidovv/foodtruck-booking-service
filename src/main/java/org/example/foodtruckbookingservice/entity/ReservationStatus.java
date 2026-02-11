package org.example.foodtruckbookingservice.entity;

/**
 * Status values for reservations with allowed state transitions.
 *
 * <p>State transition diagram:
 * <pre>
 *                PENDING
 *                   │
 *         ┌─────────┼─────────┐
 *         │         │         │
 *         ▼         │         ▼
 *    CANCELLED ◄────┼──── CONFIRMED
 *                   │         │
 *                   │    ┌────┴────┐
 *                   │    ▼         ▼
 *                   │ COMPLETED  NO_SHOW
 * </pre>
 */
public enum ReservationStatus {

    /** Reservation received, waiting for staff confirmation. */
    PENDING,

    /** Confirmed by staff, products reserved. */
    CONFIRMED,

    /** Cancelled (by staff). Terminal state. */
    CANCELLED,

    /** Customer picked up the order. Terminal state. */
    COMPLETED,

    /** Customer did not show up. Terminal state. */
    NO_SHOW;

    /**
     * Checks if a transition to the given status is allowed.
     *
     * @param newStatus the target status
     * @return true if the transition is valid, false otherwise
     */
    public boolean canTransitionTo(ReservationStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == COMPLETED || newStatus == NO_SHOW || newStatus == CANCELLED;
            case CANCELLED, COMPLETED, NO_SHOW -> false; // Terminal states
        };
    }

    /**
     * Checks if this status is a terminal state (no further transitions allowed).
     *
     * @return true if terminal state, false otherwise
     */
    public boolean isTerminal() {
        return this == CANCELLED || this == COMPLETED || this == NO_SHOW;
    }
}

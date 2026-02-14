package org.example.foodtruckbookingservice.entity;

/**
 * Status values for reservations with allowed state transitions.
 *
 * <p>Note: Since the auto-accept workflow, reservations start as CONFIRMED.
 * PENDING is kept for backward compatibility with existing data but is not
 * used in new reservations.
 *
 * <p>State transition diagram (new workflow):
 * <pre>
 *         Customer reserves
 *                │
 *                ▼
 *           CONFIRMED ◄─── Starting point (auto-confirmed)
 *                │
 *       ┌────────┼────────┐
 *       ▼        ▼        ▼
 *  CANCELLED  COMPLETED  NO_SHOW
 * </pre>
 */
public enum ReservationStatus {

    /**
     * @deprecated PENDING is no longer used in new reservations.
     * Reservations are auto-confirmed if inventory is available.
     * Kept for backward compatibility with existing data.
     */
    @Deprecated
    PENDING,

    /** Auto-confirmed when created (if inventory available). Starting state. */
    CONFIRMED,

    /** Cancelled (by customer via code or by staff). Terminal state. */
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
            // PENDING kept for backward compatibility - can still transition
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

    /**
     * Checks if cancellation is allowed from this status.
     *
     * @return true if reservation can be cancelled
     */
    public boolean canCancel() {
        return this == CONFIRMED || this == PENDING;
    }
}

package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for availability check.
 * Based on daily_inventory (staff-entered) rather than fixed daily_capacity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponse {

    private UUID locationId;
    private String locationName;
    private LocalDate date;
    private Integer dayOfWeek;
    private String dayName;
    private LocalTime openingTime;
    private LocalTime closingTime;

    /**
     * Whether inventory has been set for today by staff.
     */
    private Boolean inventorySet;

    /**
     * Total chickens available (from daily_inventory).
     * Null if inventory not set.
     */
    private Integer totalChickens;

    /**
     * Chickens already reserved (CONFIRMED reservations).
     */
    private Integer reservedChickens;

    /**
     * Available chickens (totalChickens - reservedChickens).
     */
    private Integer availableChickens;

    /**
     * Whether the location is open today.
     */
    private Boolean isOpen;

    private AvailabilityStatus availabilityStatus;

    /**
     * Optional message (e.g., "Vorrat wurde noch nicht eingetragen").
     */
    private String message;

    /**
     * Availability status based on remaining inventory.
     */
    public enum AvailabilityStatus {
        /** >30% available */
        AVAILABLE,
        /** 10-30% available */
        LIMITED,
        /** <10% available */
        ALMOST_FULL,
        /** No chickens left (0 available) */
        SOLD_OUT,
        /** No inventory set by staff */
        NOT_AVAILABLE,
        /** Location closed on this day */
        CLOSED
    }
}

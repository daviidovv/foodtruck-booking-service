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
    private Integer totalCapacity;
    private Integer reservedCapacity;
    private Integer availableCapacity;
    private Boolean isOpen;
    private AvailabilityStatus availabilityStatus;

    /**
     * Availability status based on remaining capacity percentage.
     */
    public enum AvailabilityStatus {
        /** >30% available */
        AVAILABLE,
        /** 10-30% available */
        LIMITED,
        /** <10% available */
        ALMOST_FULL,
        /** No capacity left */
        FULL,
        /** Location closed on this day */
        CLOSED
    }
}

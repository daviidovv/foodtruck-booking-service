package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for daily inventory data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private UUID id;
    private UUID locationId;
    private String locationName;
    private LocalDate date;

    /**
     * Whether inventory has been set for this date.
     */
    private Boolean inventorySet;

    /**
     * Total chickens entered by staff.
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
     * Number of reservations for the day.
     */
    private Long reservationCount;

    /**
     * Utilization percentage ((reserved / total) * 100).
     */
    private Double utilizationPercent;

    /**
     * Status based on remaining percentage.
     */
    private AvailabilityResponse.AvailabilityStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Optional message.
     */
    private String message;
}

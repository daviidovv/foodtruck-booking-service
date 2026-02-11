package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for capacity information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapacityResponse {

    private UUID locationId;
    private String locationName;
    private LocalDate date;
    private Integer totalCapacity;
    private ReservedCapacity reserved;
    private Integer available;
    private Double utilizationPercent;
    private CapacityStatus status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservedCapacity {
        private Integer pending;
        private Integer confirmed;
        private Integer total;
    }

    public enum CapacityStatus {
        /** >30% available */
        AVAILABLE,
        /** 10-30% available */
        LIMITED,
        /** <10% available */
        ALMOST_FULL,
        /** 0% available */
        FULL
    }
}

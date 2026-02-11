package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.foodtruckbookingservice.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for reservation data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private UUID id;
    private UUID locationId;
    private String locationName;
    private String locationAddress;
    private String customerName;
    private String customerEmail;
    private Integer chickenCount;
    private Integer friesCount;
    private LocalDateTime pickupTime;
    private ReservationStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package org.example.foodtruckbookingservice.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.foodtruckbookingservice.entity.ReservationStatus;

/**
 * Request DTO for updating reservation status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private ReservationStatus status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}

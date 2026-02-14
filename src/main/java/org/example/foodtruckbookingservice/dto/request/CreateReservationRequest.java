package org.example.foodtruckbookingservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Request DTO for creating a new reservation.
 *
 * <p>Reservations are only for the same day (today).
 * Pickup time is optional - customers can come whenever they want.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "Location ID is required")
    private UUID locationId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    private String customerName;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String customerEmail;

    @NotNull(message = "Chicken count is required")
    @Min(value = 0, message = "Chicken count must be at least 0")
    @Max(value = 50, message = "Chicken count must not exceed 50")
    private Integer chickenCount;

    @NotNull(message = "Fries count is required")
    @Min(value = 0, message = "Fries count must be at least 0")
    @Max(value = 50, message = "Fries count must not exceed 50")
    private Integer friesCount;

    /**
     * Optional pickup time (HH:mm format).
     * If not specified, customer can come whenever they want.
     * Must be within opening hours if specified.
     */
    private LocalTime pickupTime;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}

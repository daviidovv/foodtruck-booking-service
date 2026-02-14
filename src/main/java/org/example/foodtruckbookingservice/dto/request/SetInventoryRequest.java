package org.example.foodtruckbookingservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for setting daily chicken inventory.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetInventoryRequest {

    @NotNull(message = "Location ID is required")
    private UUID locationId;

    @NotNull(message = "Total chickens is required")
    @Min(value = 0, message = "Total chickens must be at least 0")
    private Integer totalChickens;
}

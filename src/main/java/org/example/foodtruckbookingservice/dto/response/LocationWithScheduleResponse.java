package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for location with its schedules.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationWithScheduleResponse {

    private UUID locationId;
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<ScheduleResponse> schedules;
}

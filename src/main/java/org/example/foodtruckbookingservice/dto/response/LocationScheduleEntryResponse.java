package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for a location entry in the schedule.
 * Contains location info and opening hours for a specific day.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationScheduleEntryResponse {

    private UUID locationId;
    private String locationName;
    private String address;
    private Double latitude;
    private Double longitude;
    private LocalTime openingTime;
    private LocalTime closingTime;
}

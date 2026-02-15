package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for a single day's schedule.
 * Contains all locations that operate on this day.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayScheduleResponse {

    private Integer dayOfWeek;
    private String dayName;
    private List<LocationScheduleEntryResponse> locations;
}

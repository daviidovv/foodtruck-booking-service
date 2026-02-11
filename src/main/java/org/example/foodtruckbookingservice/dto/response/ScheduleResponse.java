package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for schedule data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleResponse {

    private UUID id;
    private Integer dayOfWeek;
    private String dayName;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private Integer dailyCapacity;
    private Boolean active;
}

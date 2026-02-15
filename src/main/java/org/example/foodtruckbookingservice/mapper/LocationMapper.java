package org.example.foodtruckbookingservice.mapper;

import org.example.foodtruckbookingservice.dto.request.CreateLocationRequest;
import org.example.foodtruckbookingservice.dto.response.LocationResponse;
import org.example.foodtruckbookingservice.dto.response.LocationWithScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.ScheduleResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.LocationSchedule;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Mapper for Location entity and DTOs.
 */
@Component
public class LocationMapper {

    /**
     * Convert Location entity to LocationResponse DTO.
     */
    public LocationResponse toResponse(Location entity) {
        if (entity == null) {
            return null;
        }
        return LocationResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert CreateLocationRequest DTO to Location entity.
     */
    public Location toEntity(CreateLocationRequest request) {
        if (request == null) {
            return null;
        }
        return Location.builder()
                .name(request.getName())
                .address(request.getAddress())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    /**
     * Convert Location with schedules to LocationWithScheduleResponse DTO.
     */
    public LocationWithScheduleResponse toResponseWithSchedules(Location location, List<LocationSchedule> schedules) {
        if (location == null) {
            return null;
        }
        return LocationWithScheduleResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .schedules(schedules.stream()
                        .map(this::toScheduleResponse)
                        .toList())
                .build();
    }

    /**
     * Convert LocationSchedule entity to ScheduleResponse DTO.
     */
    public ScheduleResponse toScheduleResponse(LocationSchedule entity) {
        if (entity == null) {
            return null;
        }
        return ScheduleResponse.builder()
                .id(entity.getId())
                .dayOfWeek(entity.getDayOfWeek())
                .dayName(getDayName(entity.getDayOfWeek()))
                .openingTime(entity.getOpeningTime())
                .closingTime(entity.getClosingTime())
                .dailyCapacity(entity.getDailyCapacity())
                .active(entity.getActive())
                .build();
    }

    /**
     * Get localized day name from ISO 8601 day of week (1=Monday, 7=Sunday).
     */
    private String getDayName(int dayOfWeek) {
        DayOfWeek day = DayOfWeek.of(dayOfWeek);
        return day.getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }
}

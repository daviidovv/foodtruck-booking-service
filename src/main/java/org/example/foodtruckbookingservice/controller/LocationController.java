package org.example.foodtruckbookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.CreateLocationRequest;
import org.example.foodtruckbookingservice.dto.request.CreateScheduleRequest;
import org.example.foodtruckbookingservice.dto.response.AvailabilityResponse;
import org.example.foodtruckbookingservice.dto.response.LocationResponse;
import org.example.foodtruckbookingservice.dto.response.LocationWithScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.ScheduleResponse;
import org.example.foodtruckbookingservice.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for location operations.
 * Public endpoints for customers, admin endpoints for location management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class LocationController {

    private final LocationService locationService;

    /**
     * Get all active locations.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/locations")
    public ResponseEntity<Map<String, List<LocationResponse>>> getAllLocations() {
        log.info("GET /api/v1/locations");
        List<LocationResponse> locations = locationService.getAllActiveLocations();
        return ResponseEntity.ok(Map.of("content", locations));
    }

    /**
     * Get location by ID.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/locations/{locationId}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable UUID locationId) {
        log.info("GET /api/v1/locations/{}", locationId);
        LocationResponse location = locationService.getLocationById(locationId);
        return ResponseEntity.ok(location);
    }

    /**
     * Get schedule for a location.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/locations/{locationId}/schedule")
    public ResponseEntity<LocationWithScheduleResponse> getLocationSchedule(@PathVariable UUID locationId) {
        log.info("GET /api/v1/locations/{}/schedule", locationId);
        LocationWithScheduleResponse schedule = locationService.getLocationSchedule(locationId);
        return ResponseEntity.ok(schedule);
    }

    /**
     * Check availability for a location on a specific date.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/locations/{locationId}/availability")
    public ResponseEntity<AvailabilityResponse> checkAvailability(
            @PathVariable UUID locationId,
            @RequestParam LocalDate date) {
        log.info("GET /api/v1/locations/{}/availability?date={}", locationId, date);
        AvailabilityResponse availability = locationService.checkAvailability(locationId, date);
        return ResponseEntity.ok(availability);
    }

    // ==================== Admin Endpoints ====================

    /**
     * Create a new location.
     * Admin endpoint - requires ROLE_ADMIN.
     */
    @PostMapping("/admin/locations")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        log.info("POST /api/v1/admin/locations - name: {}", request.getName());
        LocationResponse location = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(location);
    }

    /**
     * Update an existing location.
     * Admin endpoint - requires ROLE_ADMIN.
     */
    @PutMapping("/admin/locations/{locationId}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable UUID locationId,
            @Valid @RequestBody CreateLocationRequest request) {
        log.info("PUT /api/v1/admin/locations/{}", locationId);
        LocationResponse location = locationService.updateLocation(locationId, request);
        return ResponseEntity.ok(location);
    }

    /**
     * Create or update schedule for a location.
     * Admin endpoint - requires ROLE_ADMIN.
     */
    @PostMapping("/admin/locations/{locationId}/schedule")
    public ResponseEntity<ScheduleResponse> createOrUpdateSchedule(
            @PathVariable UUID locationId,
            @Valid @RequestBody CreateScheduleRequest request) {
        log.info("POST /api/v1/admin/locations/{}/schedule - day: {}", locationId, request.getDayOfWeek());
        ScheduleResponse schedule = locationService.createOrUpdateSchedule(locationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }
}

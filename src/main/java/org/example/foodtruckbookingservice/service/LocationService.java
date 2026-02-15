package org.example.foodtruckbookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.CreateLocationRequest;
import org.example.foodtruckbookingservice.dto.request.CreateScheduleRequest;
import org.example.foodtruckbookingservice.dto.response.AvailabilityResponse;
import org.example.foodtruckbookingservice.dto.response.DayScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.InventoryResponse;
import org.example.foodtruckbookingservice.dto.response.LocationResponse;
import org.example.foodtruckbookingservice.dto.response.LocationScheduleEntryResponse;
import org.example.foodtruckbookingservice.dto.response.LocationWithScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.ScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.WeeklyScheduleResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.LocationSchedule;
import org.example.foodtruckbookingservice.exception.BusinessRuleViolationException;
import org.example.foodtruckbookingservice.exception.LocationNameAlreadyExistsException;
import org.example.foodtruckbookingservice.exception.LocationNotFoundException;
import org.example.foodtruckbookingservice.mapper.LocationMapper;
import org.example.foodtruckbookingservice.repository.LocationRepository;
import org.example.foodtruckbookingservice.repository.LocationScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for location and schedule operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationScheduleRepository scheduleRepository;
    private final InventoryService inventoryService;
    private final LocationMapper locationMapper;

    /**
     * Get all active locations.
     */
    public List<LocationResponse> getAllActiveLocations() {
        log.debug("Fetching all active locations");
        return locationRepository.findByActiveTrue().stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    /**
     * Get location by ID.
     */
    public LocationResponse getLocationById(UUID locationId) {
        log.debug("Fetching location with id: {}", locationId);
        Location location = findLocationOrThrow(locationId);
        return locationMapper.toResponse(location);
    }

    /**
     * Get location schedule.
     */
    public LocationWithScheduleResponse getLocationSchedule(UUID locationId) {
        log.debug("Fetching schedule for location: {}", locationId);
        Location location = findLocationOrThrow(locationId);
        List<LocationSchedule> schedules = scheduleRepository.findByLocationIdAndActiveTrue(locationId);
        return locationMapper.toResponseWithSchedules(location, schedules);
    }

    /**
     * Check availability for a location on a specific date.
     * Uses daily_inventory for today (same-day only).
     */
    public AvailabilityResponse checkAvailability(UUID locationId, LocalDate date) {
        log.debug("Checking availability for location {} on {}", locationId, date);

        Location location = findLocationOrThrow(locationId);
        int dayOfWeek = date.getDayOfWeek().getValue();

        // Check if location is open on this day
        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return buildClosedResponse(location, date, dayOfWeek);
        }

        // Only same-day reservations allowed
        LocalDate today = LocalDate.now();
        if (!date.equals(today)) {
            return buildNotTodayResponse(location, schedule, date);
        }

        // Get inventory for today
        InventoryResponse inventory = inventoryService.getInventory(locationId, date);

        if (!inventory.getInventorySet()) {
            return buildNoInventoryResponse(location, schedule, date);
        }

        return buildAvailabilityResponse(location, schedule, date, inventory);
    }

    /**
     * Get the weekly schedule for all active locations.
     * Returns locations grouped by day of week.
     */
    public WeeklyScheduleResponse getWeeklySchedule() {
        log.debug("Fetching weekly schedule for all active locations");

        List<LocationSchedule> allSchedules = scheduleRepository.findAllActiveWithLocation();

        Map<Integer, List<LocationSchedule>> schedulesByDay = allSchedules.stream()
                .collect(Collectors.groupingBy(LocationSchedule::getDayOfWeek));

        List<DayScheduleResponse> daySchedules = new ArrayList<>();

        for (int day = 1; day <= 7; day++) {
            List<LocationSchedule> dayLocations = schedulesByDay.getOrDefault(day, List.of());

            if (!dayLocations.isEmpty()) {
                List<LocationScheduleEntryResponse> entries = dayLocations.stream()
                        .sorted(Comparator.comparing(s -> s.getLocation().getName()))
                        .map(this::toLocationScheduleEntry)
                        .toList();

                daySchedules.add(DayScheduleResponse.builder()
                        .dayOfWeek(day)
                        .dayName(getDayName(day))
                        .locations(entries)
                        .build());
            }
        }

        return WeeklyScheduleResponse.builder()
                .schedule(daySchedules)
                .build();
    }

    /**
     * Get locations that are open today.
     */
    public List<LocationResponse> getTodayLocations() {
        log.debug("Fetching locations open today");
        int todayDayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        List<LocationSchedule> todaySchedules = scheduleRepository.findByDayOfWeekAndActiveTrue(todayDayOfWeek);

        return todaySchedules.stream()
                .map(LocationSchedule::getLocation)
                .filter(Location::getActive)
                .map(locationMapper::toResponse)
                .toList();
    }

    private LocationScheduleEntryResponse toLocationScheduleEntry(LocationSchedule schedule) {
        Location location = schedule.getLocation();
        return LocationScheduleEntryResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .openingTime(schedule.getOpeningTime())
                .closingTime(schedule.getClosingTime())
                .build();
    }

    /**
     * Create a new location.
     */
    @Transactional
    public LocationResponse createLocation(CreateLocationRequest request) {
        log.info("Creating new location: {}", request.getName());

        if (locationRepository.existsByName(request.getName())) {
            throw new LocationNameAlreadyExistsException(request.getName());
        }

        Location location = locationMapper.toEntity(request);
        Location saved = locationRepository.save(location);

        log.info("Created location with id: {}", saved.getId());
        return locationMapper.toResponse(saved);
    }

    /**
     * Update an existing location.
     */
    @Transactional
    public LocationResponse updateLocation(UUID locationId, CreateLocationRequest request) {
        log.info("Updating location: {}", locationId);

        Location location = findLocationOrThrow(locationId);

        // Check if name is being changed and if new name already exists
        if (!location.getName().equals(request.getName()) &&
                locationRepository.existsByName(request.getName())) {
            throw new LocationNameAlreadyExistsException(request.getName());
        }

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        if (request.getActive() != null) {
            location.setActive(request.getActive());
        }

        Location saved = locationRepository.save(location);
        log.info("Updated location: {}", saved.getId());
        return locationMapper.toResponse(saved);
    }

    /**
     * Create or update a schedule for a location.
     */
    @Transactional
    public ScheduleResponse createOrUpdateSchedule(UUID locationId, CreateScheduleRequest request) {
        log.info("Creating/updating schedule for location {} on day {}", locationId, request.getDayOfWeek());

        Location location = findLocationOrThrow(locationId);

        if (request.getOpeningTime().isAfter(request.getClosingTime()) ||
                request.getOpeningTime().equals(request.getClosingTime())) {
            throw new BusinessRuleViolationException(
                    "Opening time must be before closing time",
                    "INVALID_TIME_RANGE");
        }

        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeek(locationId, request.getDayOfWeek())
                .orElse(LocationSchedule.builder()
                        .location(location)
                        .dayOfWeek(request.getDayOfWeek())
                        .build());

        schedule.setOpeningTime(request.getOpeningTime());
        schedule.setClosingTime(request.getClosingTime());
        schedule.setDailyCapacity(request.getDailyCapacity());
        schedule.setActive(request.getActive() != null ? request.getActive() : true);

        LocationSchedule saved = scheduleRepository.save(schedule);
        log.info("Saved schedule with id: {}", saved.getId());
        return locationMapper.toScheduleResponse(saved);
    }

    private Location findLocationOrThrow(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    private AvailabilityResponse buildAvailabilityResponse(
            Location location,
            LocationSchedule schedule,
            LocalDate date,
            InventoryResponse inventory) {

        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(schedule.getDayOfWeek())
                .dayName(getDayName(schedule.getDayOfWeek()))
                .openingTime(schedule.getOpeningTime())
                .closingTime(schedule.getClosingTime())
                .inventorySet(true)
                .totalChickens(inventory.getTotalChickens())
                .reservedChickens(inventory.getReservedChickens())
                .availableChickens(inventory.getAvailableChickens())
                .isOpen(true)
                .availabilityStatus(inventory.getStatus())
                .build();
    }

    private AvailabilityResponse buildNoInventoryResponse(Location location, LocationSchedule schedule, LocalDate date) {
        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(schedule.getDayOfWeek())
                .dayName(getDayName(schedule.getDayOfWeek()))
                .openingTime(schedule.getOpeningTime())
                .closingTime(schedule.getClosingTime())
                .inventorySet(false)
                .availableChickens(0)
                .isOpen(true)
                .availabilityStatus(AvailabilityResponse.AvailabilityStatus.NOT_AVAILABLE)
                .message("Vorrat wurde noch nicht eingetragen")
                .build();
    }

    private AvailabilityResponse buildNotTodayResponse(Location location, LocationSchedule schedule, LocalDate date) {
        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(schedule.getDayOfWeek())
                .dayName(getDayName(schedule.getDayOfWeek()))
                .openingTime(schedule.getOpeningTime())
                .closingTime(schedule.getClosingTime())
                .inventorySet(false)
                .availableChickens(0)
                .isOpen(true)
                .availabilityStatus(AvailabilityResponse.AvailabilityStatus.NOT_AVAILABLE)
                .message("Reservierungen nur für heute möglich")
                .build();
    }

    private AvailabilityResponse buildClosedResponse(Location location, LocalDate date, int dayOfWeek) {
        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(dayOfWeek)
                .dayName(getDayName(dayOfWeek))
                .inventorySet(false)
                .availableChickens(0)
                .isOpen(false)
                .availabilityStatus(AvailabilityResponse.AvailabilityStatus.CLOSED)
                .build();
    }

    private String getDayName(int dayOfWeek) {
        DayOfWeek day = DayOfWeek.of(dayOfWeek);
        return day.getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }
}

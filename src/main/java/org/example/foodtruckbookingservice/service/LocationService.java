package org.example.foodtruckbookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.CreateLocationRequest;
import org.example.foodtruckbookingservice.dto.request.CreateScheduleRequest;
import org.example.foodtruckbookingservice.dto.response.AvailabilityResponse;
import org.example.foodtruckbookingservice.dto.response.LocationResponse;
import org.example.foodtruckbookingservice.dto.response.LocationWithScheduleResponse;
import org.example.foodtruckbookingservice.dto.response.ScheduleResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.LocationSchedule;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.example.foodtruckbookingservice.exception.BusinessRuleViolationException;
import org.example.foodtruckbookingservice.exception.LocationNameAlreadyExistsException;
import org.example.foodtruckbookingservice.exception.LocationNotFoundException;
import org.example.foodtruckbookingservice.mapper.LocationMapper;
import org.example.foodtruckbookingservice.repository.LocationRepository;
import org.example.foodtruckbookingservice.repository.LocationScheduleRepository;
import org.example.foodtruckbookingservice.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

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
    private final ReservationRepository reservationRepository;
    private final LocationMapper locationMapper;

    private static final Set<ReservationStatus> ACTIVE_STATUSES =
            Set.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

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
     */
    public AvailabilityResponse checkAvailability(UUID locationId, LocalDate date) {
        log.debug("Checking availability for location {} on {}", locationId, date);

        Location location = findLocationOrThrow(locationId);
        int dayOfWeek = date.getDayOfWeek().getValue();

        return scheduleRepository.findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .map(schedule -> buildAvailabilityResponse(location, schedule, date))
                .orElseGet(() -> buildClosedResponse(location, date, dayOfWeek));
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

    /**
     * Get available capacity for a location on a specific date.
     */
    public int getAvailableCapacity(UUID locationId, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        return scheduleRepository.findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .map(schedule -> {
                    LocalDateTime startOfDay = date.atStartOfDay();
                    LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

                    Integer reserved = reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                            locationId, startOfDay, endOfDay, ACTIVE_STATUSES);

                    return schedule.getDailyCapacity() - (reserved != null ? reserved : 0);
                })
                .orElse(0);
    }

    private Location findLocationOrThrow(UUID locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    private AvailabilityResponse buildAvailabilityResponse(Location location, LocationSchedule schedule, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Integer reserved = reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                location.getId(), startOfDay, endOfDay, ACTIVE_STATUSES);
        int reservedCapacity = reserved != null ? reserved : 0;
        int availableCapacity = schedule.getDailyCapacity() - reservedCapacity;

        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(schedule.getDayOfWeek())
                .dayName(getDayName(schedule.getDayOfWeek()))
                .openingTime(schedule.getOpeningTime())
                .closingTime(schedule.getClosingTime())
                .totalCapacity(schedule.getDailyCapacity())
                .reservedCapacity(reservedCapacity)
                .availableCapacity(Math.max(0, availableCapacity))
                .isOpen(true)
                .availabilityStatus(calculateAvailabilityStatus(availableCapacity, schedule.getDailyCapacity()))
                .build();
    }

    private AvailabilityResponse buildClosedResponse(Location location, LocalDate date, int dayOfWeek) {
        return AvailabilityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .dayOfWeek(dayOfWeek)
                .dayName(getDayName(dayOfWeek))
                .totalCapacity(0)
                .reservedCapacity(0)
                .availableCapacity(0)
                .isOpen(false)
                .availabilityStatus(AvailabilityResponse.AvailabilityStatus.CLOSED)
                .build();
    }

    private AvailabilityResponse.AvailabilityStatus calculateAvailabilityStatus(int available, int total) {
        if (total == 0 || available <= 0) {
            return AvailabilityResponse.AvailabilityStatus.FULL;
        }

        double percentAvailable = (double) available / total * 100;

        if (percentAvailable > 30) {
            return AvailabilityResponse.AvailabilityStatus.AVAILABLE;
        } else if (percentAvailable > 10) {
            return AvailabilityResponse.AvailabilityStatus.LIMITED;
        } else {
            return AvailabilityResponse.AvailabilityStatus.ALMOST_FULL;
        }
    }

    private String getDayName(int dayOfWeek) {
        DayOfWeek day = DayOfWeek.of(dayOfWeek);
        return day.getDisplayName(TextStyle.FULL, Locale.GERMAN);
    }
}

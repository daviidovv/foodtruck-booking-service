package org.example.foodtruckbookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.CreateReservationRequest;
import org.example.foodtruckbookingservice.dto.request.UpdateStatusRequest;
import org.example.foodtruckbookingservice.dto.response.CapacityResponse;
import org.example.foodtruckbookingservice.dto.response.ReservationResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.LocationSchedule;
import org.example.foodtruckbookingservice.entity.Reservation;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.example.foodtruckbookingservice.exception.BusinessRuleViolationException;
import org.example.foodtruckbookingservice.exception.CapacityExceededException;
import org.example.foodtruckbookingservice.exception.InvalidStatusTransitionException;
import org.example.foodtruckbookingservice.exception.LocationNotFoundException;
import org.example.foodtruckbookingservice.exception.ReservationNotFoundException;
import org.example.foodtruckbookingservice.mapper.ReservationMapper;
import org.example.foodtruckbookingservice.repository.LocationRepository;
import org.example.foodtruckbookingservice.repository.LocationScheduleRepository;
import org.example.foodtruckbookingservice.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for reservation operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final LocationRepository locationRepository;
    private final LocationScheduleRepository scheduleRepository;
    private final ReservationMapper reservationMapper;

    private static final Set<ReservationStatus> ACTIVE_STATUSES =
            Set.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);

    private static final int MIN_MINUTES_BEFORE_PICKUP = 30;

    /**
     * Create a new reservation.
     */
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        log.info("Creating reservation for location {} at {}", request.getLocationId(), request.getPickupTime());

        // Validate location exists and is active
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new LocationNotFoundException(request.getLocationId()));

        if (!location.getActive()) {
            throw new BusinessRuleViolationException(
                    "Location is not active",
                    "LOCATION_INACTIVE");
        }

        // Validate at least one product
        if (request.getChickenCount() + request.getFriesCount() <= 0) {
            throw new BusinessRuleViolationException(
                    "At least one product must be selected",
                    "MIN_ORDER_QUANTITY");
        }

        // Validate pickup time
        validatePickupTime(request.getPickupTime(), request.getLocationId());

        // Check capacity
        checkCapacity(request.getLocationId(), request.getPickupTime().toLocalDate(), request.getChickenCount());

        // Create reservation
        Reservation reservation = reservationMapper.toEntity(request);
        reservation.setLocation(location);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Created reservation with id: {}", saved.getId());

        return reservationMapper.toResponse(saved);
    }

    /**
     * Get reservation by ID.
     */
    public ReservationResponse getReservationById(UUID reservationId) {
        log.debug("Fetching reservation: {}", reservationId);
        Reservation reservation = findReservationOrThrow(reservationId);
        return reservationMapper.toResponse(reservation);
    }

    /**
     * Get reservations for a location on a specific date.
     */
    public List<ReservationResponse> getReservationsForLocationAndDate(UUID locationId, LocalDate date) {
        log.debug("Fetching reservations for location {} on {}", locationId, date);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return reservationRepository
                .findByLocationIdAndPickupTimeBetweenOrderByPickupTimeAsc(locationId, startOfDay, endOfDay)
                .stream()
                .map(reservationMapper::toResponse)
                .toList();
    }

    /**
     * Get all reservations (paginated).
     */
    public Page<ReservationResponse> getAllReservations(Pageable pageable) {
        log.debug("Fetching all reservations");
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toResponse);
    }

    /**
     * Get reservations for a location (paginated).
     */
    public Page<ReservationResponse> getReservationsForLocation(UUID locationId, Pageable pageable) {
        log.debug("Fetching reservations for location: {}", locationId);
        return reservationRepository.findByLocationId(locationId, pageable)
                .map(reservationMapper::toResponse);
    }

    /**
     * Update reservation status.
     */
    @Transactional
    public ReservationResponse updateStatus(UUID reservationId, UpdateStatusRequest request) {
        log.info("Updating status of reservation {} to {}", reservationId, request.getStatus());

        Reservation reservation = findReservationOrThrow(reservationId);

        // Validate status transition
        if (!reservation.getStatus().canTransitionTo(request.getStatus())) {
            throw new InvalidStatusTransitionException(reservation.getStatus(), request.getStatus());
        }

        // If confirming, check capacity is still available
        if (reservation.getStatus() == ReservationStatus.PENDING &&
                request.getStatus() == ReservationStatus.CONFIRMED) {
            checkCapacity(
                    reservation.getLocation().getId(),
                    reservation.getPickupTime().toLocalDate(),
                    reservation.getChickenCount());
        }

        reservation.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            reservation.setNotes(request.getNotes());
        }

        Reservation saved = reservationRepository.save(reservation);
        log.info("Updated reservation {} status to {}", reservationId, saved.getStatus());

        return reservationMapper.toResponse(saved);
    }

    /**
     * Get capacity information for a location on a specific date.
     */
    public CapacityResponse getCapacity(UUID locationId, LocalDate date) {
        log.debug("Getting capacity for location {} on {}", locationId, date);

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        int dayOfWeek = date.getDayOfWeek().getValue();
        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return buildEmptyCapacityResponse(location, date);
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        // Get reserved counts by status
        int pendingCount = getPendingChickenCount(locationId, startOfDay, endOfDay);
        int confirmedCount = getConfirmedChickenCount(locationId, startOfDay, endOfDay);
        int totalReserved = pendingCount + confirmedCount;
        int available = Math.max(0, schedule.getDailyCapacity() - totalReserved);
        double utilization = (double) totalReserved / schedule.getDailyCapacity() * 100;

        return CapacityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .totalCapacity(schedule.getDailyCapacity())
                .reserved(CapacityResponse.ReservedCapacity.builder()
                        .pending(pendingCount)
                        .confirmed(confirmedCount)
                        .total(totalReserved)
                        .build())
                .available(available)
                .utilizationPercent(Math.round(utilization * 10) / 10.0)
                .status(calculateCapacityStatus(available, schedule.getDailyCapacity()))
                .build();
    }

    private void validatePickupTime(LocalDateTime pickupTime, UUID locationId) {
        // Must be at least 30 minutes in the future
        LocalDateTime minPickupTime = LocalDateTime.now().plusMinutes(MIN_MINUTES_BEFORE_PICKUP);
        if (pickupTime.isBefore(minPickupTime)) {
            throw new BusinessRuleViolationException(
                    String.format("Pickup time must be at least %d minutes in the future", MIN_MINUTES_BEFORE_PICKUP),
                    "PICKUP_TOO_SOON");
        }

        // Must be within opening hours
        LocalDate date = pickupTime.toLocalDate();
        int dayOfWeek = date.getDayOfWeek().getValue();

        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Location is closed on this day",
                        "LOCATION_CLOSED"));

        LocalTime pickupTimeOfDay = pickupTime.toLocalTime();
        if (pickupTimeOfDay.isBefore(schedule.getOpeningTime()) ||
                pickupTimeOfDay.isAfter(schedule.getClosingTime())) {
            throw new BusinessRuleViolationException(
                    String.format("Pickup time must be within opening hours (%s - %s)",
                            schedule.getOpeningTime(), schedule.getClosingTime()),
                    "OUTSIDE_OPENING_HOURS");
        }
    }

    private void checkCapacity(UUID locationId, LocalDate date, int requestedChickenCount) {
        int dayOfWeek = date.getDayOfWeek().getValue();

        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(locationId, dayOfWeek)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Location is closed on this day",
                        "LOCATION_CLOSED"));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        Integer reserved = reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                locationId, startOfDay, endOfDay, ACTIVE_STATUSES);
        int reservedCount = reserved != null ? reserved : 0;

        int available = schedule.getDailyCapacity() - reservedCount;

        if (requestedChickenCount > available) {
            throw new CapacityExceededException(requestedChickenCount, available);
        }
    }

    private int getPendingChickenCount(UUID locationId, LocalDateTime start, LocalDateTime end) {
        Integer count = reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                locationId, start, end, Set.of(ReservationStatus.PENDING));
        return count != null ? count : 0;
    }

    private int getConfirmedChickenCount(UUID locationId, LocalDateTime start, LocalDateTime end) {
        Integer count = reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                locationId, start, end, Set.of(ReservationStatus.CONFIRMED));
        return count != null ? count : 0;
    }

    private Reservation findReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    private CapacityResponse buildEmptyCapacityResponse(Location location, LocalDate date) {
        return CapacityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .totalCapacity(0)
                .reserved(CapacityResponse.ReservedCapacity.builder()
                        .pending(0)
                        .confirmed(0)
                        .total(0)
                        .build())
                .available(0)
                .utilizationPercent(0.0)
                .status(CapacityResponse.CapacityStatus.FULL)
                .build();
    }

    private CapacityResponse.CapacityStatus calculateCapacityStatus(int available, int total) {
        if (total == 0 || available <= 0) {
            return CapacityResponse.CapacityStatus.FULL;
        }

        double percentAvailable = (double) available / total * 100;

        if (percentAvailable > 30) {
            return CapacityResponse.CapacityStatus.AVAILABLE;
        } else if (percentAvailable > 10) {
            return CapacityResponse.CapacityStatus.LIMITED;
        } else {
            return CapacityResponse.CapacityStatus.ALMOST_FULL;
        }
    }
}

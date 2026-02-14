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

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for reservation operations.
 *
 * <p>Workflow: Reservations are auto-confirmed if inventory is available.
 * Only same-day reservations are allowed.
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
    private final InventoryService inventoryService;

    private static final String CODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Create a new reservation (auto-confirmed if inventory available).
     */
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        log.info("Creating reservation for location {}", request.getLocationId());

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

        // Validate location is open today
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();
        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(request.getLocationId(), dayOfWeek)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Location is closed today",
                        "LOCATION_CLOSED"));

        // Validate pickup time if provided
        if (request.getPickupTime() != null) {
            validatePickupTime(request.getPickupTime(), schedule);
        }

        // Check inventory is set and has enough chickens
        if (!inventoryService.isInventorySet(request.getLocationId())) {
            throw new BusinessRuleViolationException(
                    "Reservierung aktuell nicht möglich - Vorrat wurde noch nicht eingetragen",
                    "INVENTORY_NOT_SET");
        }

        int available = inventoryService.getAvailableChickens(request.getLocationId());
        if (request.getChickenCount() > available) {
            throw new CapacityExceededException(request.getChickenCount(), available);
        }

        // Generate unique confirmation code
        String confirmationCode = generateUniqueConfirmationCode();

        // Create reservation (auto-confirmed)
        Reservation reservation = reservationMapper.toEntity(request, confirmationCode);
        reservation.setLocation(location);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Created reservation with id: {} and code: {}", saved.getId(), confirmationCode);

        ReservationResponse response = reservationMapper.toResponse(saved);
        response.setMessage(String.format(
                "Reservierung erfolgreich! Bitte notieren Sie Ihren Bestätigungscode: %s",
                confirmationCode));

        return response;
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
     * Get reservation by confirmation code.
     */
    public ReservationResponse getReservationByCode(String confirmationCode) {
        log.debug("Fetching reservation by code: {}", confirmationCode);
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode.toUpperCase())
                .orElseThrow(() -> new ReservationNotFoundException(
                        "Reservation with code " + confirmationCode + " not found"));
        return reservationMapper.toResponse(reservation);
    }

    /**
     * Cancel reservation by confirmation code.
     */
    @Transactional
    public ReservationResponse cancelByCode(String confirmationCode) {
        log.info("Cancelling reservation by code: {}", confirmationCode);

        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode.toUpperCase())
                .orElseThrow(() -> new ReservationNotFoundException(
                        "Reservation with code " + confirmationCode + " not found"));

        if (!reservation.getStatus().canCancel()) {
            throw new InvalidStatusTransitionException(
                    reservation.getStatus(),
                    ReservationStatus.CANCELLED);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservationRepository.save(reservation);

        log.info("Cancelled reservation {} with code {}", saved.getId(), confirmationCode);

        ReservationResponse response = reservationMapper.toResponse(saved);
        response.setMessage("Reservierung wurde erfolgreich storniert. Die Hähnchen sind wieder verfügbar.");
        return response;
    }

    /**
     * Get reservations for a location on a specific date.
     */
    public List<ReservationResponse> getReservationsForLocationAndDate(UUID locationId, LocalDate date) {
        log.debug("Fetching reservations for location {} on {}", locationId, date);

        return reservationRepository
                .findByLocationIdAndDate(locationId, date)
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

        reservation.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            reservation.setNotes(request.getNotes());
        }

        Reservation saved = reservationRepository.save(reservation);
        log.info("Updated reservation {} status to {}", reservationId, saved.getStatus());

        return reservationMapper.toResponse(saved);
    }

    /**
     * Get capacity/inventory information for a location on a specific date.
     */
    public CapacityResponse getCapacity(UUID locationId, LocalDate date) {
        log.debug("Getting capacity for location {} on {}", locationId, date);

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        // Use inventory service for today
        if (date.equals(LocalDate.now())) {
            return buildCapacityFromInventory(location, date);
        }

        // For other dates, return empty (only same-day)
        return buildEmptyCapacityResponse(location, date);
    }

    private void validatePickupTime(LocalTime pickupTime, LocationSchedule schedule) {
        // Must be in the future
        LocalTime now = LocalTime.now();
        if (pickupTime.isBefore(now)) {
            throw new BusinessRuleViolationException(
                    "Abholzeit muss in der Zukunft liegen",
                    "PICKUP_IN_PAST");
        }

        // Must be within opening hours
        if (pickupTime.isBefore(schedule.getOpeningTime()) ||
                pickupTime.isAfter(schedule.getClosingTime())) {
            throw new BusinessRuleViolationException(
                    String.format("Abholzeit muss innerhalb der Öffnungszeiten liegen (%s - %s)",
                            schedule.getOpeningTime(), schedule.getClosingTime()),
                    "OUTSIDE_OPENING_HOURS");
        }
    }

    private String generateUniqueConfirmationCode() {
        String code;
        int attempts = 0;
        do {
            code = generateRandomCode();
            attempts++;
            if (attempts > 100) {
                throw new IllegalStateException("Unable to generate unique confirmation code");
            }
        } while (reservationRepository.existsByConfirmationCode(code));
        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CODE_CHARACTERS.charAt(RANDOM.nextInt(CODE_CHARACTERS.length())));
        }
        return sb.toString();
    }

    private Reservation findReservationOrThrow(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));
    }

    private CapacityResponse buildCapacityFromInventory(Location location, LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        LocationSchedule schedule = scheduleRepository
                .findByLocationIdAndDayOfWeekAndActiveTrue(location.getId(), dayOfWeek)
                .orElse(null);

        if (schedule == null) {
            return buildEmptyCapacityResponse(location, date);
        }

        var inventoryResponse = inventoryService.getInventory(location.getId(), date);

        if (!inventoryResponse.getInventorySet()) {
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

        int totalChickens = inventoryResponse.getTotalChickens();
        int reservedChickens = inventoryResponse.getReservedChickens();
        int available = inventoryResponse.getAvailableChickens();
        double utilization = inventoryResponse.getUtilizationPercent();

        return CapacityResponse.builder()
                .locationId(location.getId())
                .locationName(location.getName())
                .date(date)
                .totalCapacity(totalChickens)
                .reserved(CapacityResponse.ReservedCapacity.builder()
                        .pending(0)  // No more PENDING
                        .confirmed(reservedChickens)
                        .total(reservedChickens)
                        .build())
                .available(available)
                .utilizationPercent(Math.round(utilization * 10) / 10.0)
                .status(calculateCapacityStatus(available, totalChickens))
                .build();
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

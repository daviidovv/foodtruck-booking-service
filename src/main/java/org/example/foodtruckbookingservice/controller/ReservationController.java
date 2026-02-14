package org.example.foodtruckbookingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.CreateReservationRequest;
import org.example.foodtruckbookingservice.dto.request.SetInventoryRequest;
import org.example.foodtruckbookingservice.dto.request.UpdateStatusRequest;
import org.example.foodtruckbookingservice.dto.response.CapacityResponse;
import org.example.foodtruckbookingservice.dto.response.InventoryResponse;
import org.example.foodtruckbookingservice.dto.response.ReservationResponse;
import org.example.foodtruckbookingservice.service.InventoryService;
import org.example.foodtruckbookingservice.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for reservation operations.
 * Public endpoints for customers, staff/admin endpoints for management.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;
    private final InventoryService inventoryService;

    // ==================== Public Endpoints ====================

    /**
     * Create a new reservation.
     * Public endpoint - no authentication required.
     * Reservations are auto-confirmed if inventory is available.
     */
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        log.info("POST /api/v1/reservations - location: {}, customer: {}",
                request.getLocationId(), request.getCustomerName());
        ReservationResponse reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /**
     * Get reservation by ID.
     * Public endpoint - no authentication required.
     */
    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable UUID reservationId) {
        log.info("GET /api/v1/reservations/{}", reservationId);
        ReservationResponse reservation = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Get reservation by confirmation code.
     * Public endpoint - allows customers to look up their reservation.
     */
    @GetMapping("/reservations/code/{confirmationCode}")
    public ResponseEntity<ReservationResponse> getReservationByCode(
            @PathVariable String confirmationCode) {
        log.info("GET /api/v1/reservations/code/{}", confirmationCode);
        ReservationResponse reservation = reservationService.getReservationByCode(confirmationCode);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Cancel reservation by confirmation code.
     * Public endpoint - allows customers to cancel their reservation anytime.
     */
    @DeleteMapping("/reservations/code/{confirmationCode}")
    public ResponseEntity<ReservationResponse> cancelReservationByCode(
            @PathVariable String confirmationCode) {
        log.info("DELETE /api/v1/reservations/code/{}", confirmationCode);
        ReservationResponse reservation = reservationService.cancelByCode(confirmationCode);
        return ResponseEntity.ok(reservation);
    }

    // ==================== Staff Endpoints ====================

    /**
     * Get reservations for a location on a specific date.
     * Staff endpoint - requires ROLE_STAFF or ROLE_ADMIN.
     */
    @GetMapping("/staff/reservations")
    public ResponseEntity<List<ReservationResponse>> getStaffReservations(
            @RequestParam UUID locationId,
            @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        log.info("GET /api/v1/staff/reservations - location: {}, date: {}", locationId, queryDate);
        List<ReservationResponse> reservations = reservationService
                .getReservationsForLocationAndDate(locationId, queryDate);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Update reservation status.
     * Staff endpoint - requires ROLE_STAFF or ROLE_ADMIN.
     */
    @PatchMapping("/staff/reservations/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable UUID reservationId,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("PATCH /api/v1/staff/reservations/{}/status - newStatus: {}",
                reservationId, request.getStatus());
        ReservationResponse reservation = reservationService.updateStatus(reservationId, request);
        return ResponseEntity.ok(reservation);
    }

    /**
     * Get capacity for a location on a specific date.
     * Staff endpoint - requires ROLE_STAFF or ROLE_ADMIN.
     */
    @GetMapping("/staff/capacity")
    public ResponseEntity<CapacityResponse> getCapacity(
            @RequestParam UUID locationId,
            @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        log.info("GET /api/v1/staff/capacity - location: {}, date: {}", locationId, queryDate);
        CapacityResponse capacity = reservationService.getCapacity(locationId, queryDate);
        return ResponseEntity.ok(capacity);
    }

    /**
     * Set daily chicken inventory.
     * Staff endpoint - requires ROLE_STAFF or ROLE_ADMIN.
     */
    @PostMapping("/staff/inventory")
    public ResponseEntity<InventoryResponse> setInventory(
            @Valid @RequestBody SetInventoryRequest request) {
        log.info("POST /api/v1/staff/inventory - location: {}, chickens: {}",
                request.getLocationId(), request.getTotalChickens());
        InventoryResponse inventory = inventoryService.setInventory(request);
        return ResponseEntity.ok(inventory);
    }

    /**
     * Get current inventory for a location.
     * Staff endpoint - requires ROLE_STAFF or ROLE_ADMIN.
     */
    @GetMapping("/staff/inventory")
    public ResponseEntity<InventoryResponse> getInventory(
            @RequestParam UUID locationId,
            @RequestParam(required = false) LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        log.info("GET /api/v1/staff/inventory - location: {}, date: {}", locationId, queryDate);
        InventoryResponse inventory = inventoryService.getInventory(locationId, queryDate);
        return ResponseEntity.ok(inventory);
    }

    // ==================== Admin Endpoints ====================

    /**
     * Get all reservations (paginated).
     * Admin endpoint - requires ROLE_ADMIN.
     */
    @GetMapping("/admin/reservations")
    public ResponseEntity<Page<ReservationResponse>> getAllReservations(
            @RequestParam(required = false) UUID locationId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/admin/reservations - locationId: {}", locationId);

        Page<ReservationResponse> reservations;
        if (locationId != null) {
            reservations = reservationService.getReservationsForLocation(locationId, pageable);
        } else {
            reservations = reservationService.getAllReservations(pageable);
        }
        return ResponseEntity.ok(reservations);
    }
}

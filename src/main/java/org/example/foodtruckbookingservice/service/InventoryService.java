package org.example.foodtruckbookingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.foodtruckbookingservice.dto.request.SetInventoryRequest;
import org.example.foodtruckbookingservice.dto.response.AvailabilityResponse;
import org.example.foodtruckbookingservice.dto.response.InventoryResponse;
import org.example.foodtruckbookingservice.entity.DailyInventory;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.example.foodtruckbookingservice.exception.LocationNotFoundException;
import org.example.foodtruckbookingservice.repository.DailyInventoryRepository;
import org.example.foodtruckbookingservice.repository.LocationRepository;
import org.example.foodtruckbookingservice.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing daily chicken inventory.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final DailyInventoryRepository inventoryRepository;
    private final LocationRepository locationRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Set or update daily inventory for a location.
     *
     * @param request the inventory request
     * @return the inventory response
     */
    @Transactional
    public InventoryResponse setInventory(SetInventoryRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new LocationNotFoundException(request.getLocationId()));

        LocalDate today = LocalDate.now();

        DailyInventory inventory = inventoryRepository
                .findByLocationIdAndDate(request.getLocationId(), today)
                .map(existing -> {
                    log.info("Updating inventory for location {} on {}: {} -> {}",
                            request.getLocationId(), today, existing.getTotalChickens(), request.getTotalChickens());
                    existing.setTotalChickens(request.getTotalChickens());
                    return existing;
                })
                .orElseGet(() -> {
                    log.info("Creating new inventory for location {} on {}: {}",
                            request.getLocationId(), today, request.getTotalChickens());
                    return DailyInventory.builder()
                            .location(location)
                            .date(today)
                            .totalChickens(request.getTotalChickens())
                            .build();
                });

        inventory = inventoryRepository.save(inventory);
        return toInventoryResponse(inventory, location);
    }

    /**
     * Get inventory for a location on a specific date.
     *
     * @param locationId the location ID
     * @param date       the date (defaults to today)
     * @return the inventory response
     */
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(UUID locationId, LocalDate date) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        LocalDate queryDate = date != null ? date : LocalDate.now();

        Optional<DailyInventory> inventoryOpt = inventoryRepository
                .findByLocationIdAndDate(locationId, queryDate);

        if (inventoryOpt.isEmpty()) {
            return InventoryResponse.builder()
                    .locationId(locationId)
                    .locationName(location.getName())
                    .date(queryDate)
                    .inventorySet(false)
                    .message("Bitte Tagesvorrat eintragen!")
                    .build();
        }

        return toInventoryResponse(inventoryOpt.get(), location);
    }

    /**
     * Get available chickens for a location today.
     * Returns 0 if no inventory is set.
     *
     * @param locationId the location ID
     * @return available chicken count
     */
    @Transactional(readOnly = true)
    public int getAvailableChickens(UUID locationId) {
        LocalDate today = LocalDate.now();

        Optional<DailyInventory> inventoryOpt = inventoryRepository
                .findByLocationIdAndDate(locationId, today);

        if (inventoryOpt.isEmpty()) {
            return 0;
        }

        int totalChickens = inventoryOpt.get().getTotalChickens();
        int reservedChickens = getReservedChickens(locationId, today);

        return Math.max(0, totalChickens - reservedChickens);
    }

    /**
     * Check if there's enough inventory for a reservation.
     *
     * @param locationId    the location ID
     * @param chickenCount  chickens requested
     * @return true if inventory is sufficient
     */
    @Transactional(readOnly = true)
    public boolean hasEnoughInventory(UUID locationId, int chickenCount) {
        return getAvailableChickens(locationId) >= chickenCount;
    }

    /**
     * Check if inventory has been set for a location today.
     *
     * @param locationId the location ID
     * @return true if inventory exists
     */
    @Transactional(readOnly = true)
    public boolean isInventorySet(UUID locationId) {
        return inventoryRepository.existsByLocationIdAndDate(locationId, LocalDate.now());
    }

    /**
     * Reduce total inventory when a reservation is completed (chickens picked up).
     *
     * @param locationId   the location ID
     * @param chickenCount number of chickens to subtract
     */
    @Transactional
    public void reduceInventory(UUID locationId, int chickenCount) {
        LocalDate today = LocalDate.now();

        inventoryRepository.findByLocationIdAndDate(locationId, today)
                .ifPresent(inventory -> {
                    int newTotal = Math.max(0, inventory.getTotalChickens() - chickenCount);
                    log.info("Reducing inventory for location {} from {} to {} (-{} chickens)",
                            locationId, inventory.getTotalChickens(), newTotal, chickenCount);
                    inventory.setTotalChickens(newTotal);
                    inventoryRepository.save(inventory);
                });
    }

    private int getReservedChickens(UUID locationId, LocalDate date) {
        return reservationRepository.sumChickenCountByLocationAndDateAndStatus(
                locationId,
                date,
                List.of(ReservationStatus.CONFIRMED)
        );
    }

    private InventoryResponse toInventoryResponse(DailyInventory inventory, Location location) {
        int reservedChickens = getReservedChickens(inventory.getLocation().getId(), inventory.getDate());
        int availableChickens = Math.max(0, inventory.getTotalChickens() - reservedChickens);
        long reservationCount = reservationRepository.countByLocationIdAndReservationDate(
                inventory.getLocation().getId(), inventory.getDate());

        double utilizationPercent = inventory.getTotalChickens() > 0
                ? (reservedChickens * 100.0) / inventory.getTotalChickens()
                : 0.0;

        return InventoryResponse.builder()
                .id(inventory.getId())
                .locationId(location.getId())
                .locationName(location.getName())
                .date(inventory.getDate())
                .inventorySet(true)
                .totalChickens(inventory.getTotalChickens())
                .reservedChickens(reservedChickens)
                .availableChickens(availableChickens)
                .reservationCount(reservationCount)
                .utilizationPercent(utilizationPercent)
                .status(calculateStatus(availableChickens, inventory.getTotalChickens()))
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .message("Vorrat erfolgreich eingetragen")
                .build();
    }

    private AvailabilityResponse.AvailabilityStatus calculateStatus(int available, int total) {
        if (available == 0) {
            return AvailabilityResponse.AvailabilityStatus.SOLD_OUT;
        }

        double percentage = (available * 100.0) / total;

        if (percentage > 30) {
            return AvailabilityResponse.AvailabilityStatus.AVAILABLE;
        } else if (percentage > 10) {
            return AvailabilityResponse.AvailabilityStatus.LIMITED;
        } else {
            return AvailabilityResponse.AvailabilityStatus.ALMOST_FULL;
        }
    }
}

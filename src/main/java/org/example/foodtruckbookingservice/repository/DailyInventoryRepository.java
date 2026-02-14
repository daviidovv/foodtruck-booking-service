package org.example.foodtruckbookingservice.repository;

import org.example.foodtruckbookingservice.entity.DailyInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DailyInventory entity operations.
 */
@Repository
public interface DailyInventoryRepository extends JpaRepository<DailyInventory, UUID> {

    /**
     * Find inventory for a location on a specific date.
     *
     * @param locationId the location ID
     * @param date       the date
     * @return optional inventory entry
     */
    Optional<DailyInventory> findByLocationIdAndDate(UUID locationId, LocalDate date);

    /**
     * Check if inventory exists for a location on a specific date.
     *
     * @param locationId the location ID
     * @param date       the date
     * @return true if inventory exists
     */
    boolean existsByLocationIdAndDate(UUID locationId, LocalDate date);
}

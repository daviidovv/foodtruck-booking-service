package org.example.foodtruckbookingservice.repository;

import org.example.foodtruckbookingservice.entity.LocationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LocationSchedule entity operations.
 */
@Repository
public interface LocationScheduleRepository extends JpaRepository<LocationSchedule, UUID> {

    /**
     * Find all schedules for a location.
     *
     * @param locationId the location ID
     * @return list of schedules
     */
    List<LocationSchedule> findByLocationId(UUID locationId);

    /**
     * Find all active schedules for a location.
     *
     * @param locationId the location ID
     * @return list of active schedules
     */
    List<LocationSchedule> findByLocationIdAndActiveTrue(UUID locationId);

    /**
     * Find schedule for a specific location and day of week.
     *
     * @param locationId the location ID
     * @param dayOfWeek  the day of week (1=Monday, 7=Sunday)
     * @return the schedule if found
     */
    Optional<LocationSchedule> findByLocationIdAndDayOfWeek(UUID locationId, Integer dayOfWeek);

    /**
     * Find active schedule for a specific location and day of week.
     *
     * @param locationId the location ID
     * @param dayOfWeek  the day of week (1=Monday, 7=Sunday)
     * @return the active schedule if found
     */
    Optional<LocationSchedule> findByLocationIdAndDayOfWeekAndActiveTrue(UUID locationId, Integer dayOfWeek);

    /**
     * Check if a schedule exists for the given location and day.
     *
     * @param locationId the location ID
     * @param dayOfWeek  the day of week
     * @return true if exists
     */
    boolean existsByLocationIdAndDayOfWeek(UUID locationId, Integer dayOfWeek);

    /**
     * Find all active schedules with their locations eagerly loaded.
     * Only returns schedules where both schedule and location are active.
     *
     * @return list of active schedules with locations
     */
    @Query("SELECT s FROM LocationSchedule s JOIN FETCH s.location l WHERE s.active = true AND l.active = true")
    List<LocationSchedule> findAllActiveWithLocation();

    /**
     * Find all active schedules for a specific day of week.
     *
     * @param dayOfWeek the day of week (1=Monday, 7=Sunday)
     * @return list of active schedules for that day
     */
    @Query("SELECT s FROM LocationSchedule s JOIN FETCH s.location l WHERE s.dayOfWeek = :dayOfWeek AND s.active = true AND l.active = true")
    List<LocationSchedule> findByDayOfWeekAndActiveTrue(Integer dayOfWeek);
}

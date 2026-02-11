package org.example.foodtruckbookingservice.repository;

import org.example.foodtruckbookingservice.entity.Reservation;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Reservation entity operations.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Find all reservations for a location within a time range.
     * Used for daily reservation views.
     *
     * @param locationId the location ID
     * @param startTime  start of time range
     * @param endTime    end of time range
     * @return list of reservations ordered by pickup time
     */
    List<Reservation> findByLocationIdAndPickupTimeBetweenOrderByPickupTimeAsc(
            UUID locationId,
            LocalDateTime startTime,
            LocalDateTime endTime);

    /**
     * Find reservations for a location within a time range with specific statuses.
     * Used for capacity calculation (only PENDING and CONFIRMED count).
     *
     * @param locationId the location ID
     * @param startTime  start of time range
     * @param endTime    end of time range
     * @param statuses   the statuses to include
     * @return list of reservations
     */
    List<Reservation> findByLocationIdAndPickupTimeBetweenAndStatusIn(
            UUID locationId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Collection<ReservationStatus> statuses);

    /**
     * Find all reservations with a specific status.
     *
     * @param status the reservation status
     * @return list of reservations
     */
    List<Reservation> findByStatus(ReservationStatus status);

    /**
     * Find all reservations for a location (paginated).
     *
     * @param locationId the location ID
     * @param pageable   pagination info
     * @return page of reservations
     */
    Page<Reservation> findByLocationId(UUID locationId, Pageable pageable);

    /**
     * Find all reservations (paginated).
     *
     * @param pageable pagination info
     * @return page of reservations
     */
    Page<Reservation> findAll(Pageable pageable);

    /**
     * Calculate total chicken count for a location on a specific date.
     * Only counts PENDING and CONFIRMED reservations.
     *
     * @param locationId the location ID
     * @param startTime  start of day
     * @param endTime    end of day
     * @param statuses   statuses to include (PENDING, CONFIRMED)
     * @return total chicken count (or null if no reservations)
     */
    @Query("SELECT COALESCE(SUM(r.chickenCount), 0) FROM Reservation r " +
            "WHERE r.location.id = :locationId " +
            "AND r.pickupTime >= :startTime " +
            "AND r.pickupTime < :endTime " +
            "AND r.status IN :statuses")
    Integer sumChickenCountByLocationAndDateAndStatus(
            @Param("locationId") UUID locationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("statuses") Collection<ReservationStatus> statuses);

    /**
     * Count reservations by status for a location within a time range.
     * Used for statistics.
     *
     * @param locationId the location ID
     * @param startTime  start of time range
     * @param endTime    end of time range
     * @param status     the status to count
     * @return count of reservations
     */
    long countByLocationIdAndPickupTimeBetweenAndStatus(
            UUID locationId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            ReservationStatus status);

    /**
     * Count all reservations for a location within a time range.
     *
     * @param locationId the location ID
     * @param startTime  start of time range
     * @param endTime    end of time range
     * @return count of reservations
     */
    long countByLocationIdAndPickupTimeBetween(
            UUID locationId,
            LocalDateTime startTime,
            LocalDateTime endTime);
}

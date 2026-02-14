package org.example.foodtruckbookingservice.repository;

import org.example.foodtruckbookingservice.entity.Reservation;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Reservation entity operations.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Find reservation by confirmation code.
     *
     * @param confirmationCode the confirmation code (e.g., "HUHNK4M7")
     * @return optional reservation
     */
    Optional<Reservation> findByConfirmationCode(String confirmationCode);

    /**
     * Check if a confirmation code already exists.
     *
     * @param confirmationCode the confirmation code
     * @return true if exists
     */
    boolean existsByConfirmationCode(String confirmationCode);

    /**
     * Find all reservations for a location on a specific date.
     * Used for daily reservation views.
     *
     * @param locationId      the location ID
     * @param reservationDate the date
     * @return list of reservations ordered by pickup time (nulls last)
     */
    @Query("SELECT r FROM Reservation r WHERE r.location.id = :locationId " +
            "AND r.reservationDate = :date ORDER BY r.pickupTime ASC NULLS LAST")
    List<Reservation> findByLocationIdAndDate(
            @Param("locationId") UUID locationId,
            @Param("date") LocalDate reservationDate);

    /**
     * Find reservations for a location on a date with specific statuses.
     * Used for capacity calculation (only CONFIRMED counts).
     *
     * @param locationId      the location ID
     * @param reservationDate the date
     * @param statuses        the statuses to include
     * @return list of reservations
     */
    List<Reservation> findByLocationIdAndReservationDateAndStatusIn(
            UUID locationId,
            LocalDate reservationDate,
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
     * Only counts CONFIRMED reservations (PENDING is deprecated).
     *
     * @param locationId      the location ID
     * @param reservationDate the date
     * @param statuses        statuses to include (typically just CONFIRMED)
     * @return total chicken count (0 if no reservations)
     */
    @Query("SELECT COALESCE(SUM(r.chickenCount), 0) FROM Reservation r " +
            "WHERE r.location.id = :locationId " +
            "AND r.reservationDate = :date " +
            "AND r.status IN :statuses")
    Integer sumChickenCountByLocationAndDateAndStatus(
            @Param("locationId") UUID locationId,
            @Param("date") LocalDate reservationDate,
            @Param("statuses") Collection<ReservationStatus> statuses);

    /**
     * Count reservations by status for a location on a date.
     * Used for statistics.
     *
     * @param locationId      the location ID
     * @param reservationDate the date
     * @param status          the status to count
     * @return count of reservations
     */
    long countByLocationIdAndReservationDateAndStatus(
            UUID locationId,
            LocalDate reservationDate,
            ReservationStatus status);

    /**
     * Count all reservations for a location on a date.
     *
     * @param locationId      the location ID
     * @param reservationDate the date
     * @return count of reservations
     */
    long countByLocationIdAndReservationDate(UUID locationId, LocalDate reservationDate);
}

package org.example.foodtruckbookingservice.repository;

import org.example.foodtruckbookingservice.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Location entity operations.
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    /**
     * Find all active locations.
     *
     * @return list of active locations
     */
    List<Location> findByActiveTrue();

    /**
     * Find a location by its unique name.
     *
     * @param name the location name
     * @return the location if found
     */
    Optional<Location> findByName(String name);

    /**
     * Check if a location with the given name exists.
     *
     * @param name the location name
     * @return true if exists
     */
    boolean existsByName(String name);
}

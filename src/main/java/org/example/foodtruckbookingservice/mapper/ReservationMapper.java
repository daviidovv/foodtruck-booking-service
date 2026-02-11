package org.example.foodtruckbookingservice.mapper;

import org.example.foodtruckbookingservice.dto.request.CreateReservationRequest;
import org.example.foodtruckbookingservice.dto.response.ReservationResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.Reservation;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

/**
 * Mapper for Reservation entity and DTOs.
 */
@Component
public class ReservationMapper {

    /**
     * Convert Reservation entity to ReservationResponse DTO.
     */
    public ReservationResponse toResponse(Reservation entity) {
        if (entity == null) {
            return null;
        }

        Location location = entity.getLocation();

        return ReservationResponse.builder()
                .id(entity.getId())
                .locationId(location != null ? location.getId() : null)
                .locationName(location != null ? location.getName() : null)
                .locationAddress(location != null ? location.getAddress() : null)
                .customerName(entity.getCustomerName())
                .customerEmail(entity.getCustomerEmail())
                .chickenCount(entity.getChickenCount())
                .friesCount(entity.getFriesCount())
                .pickupTime(entity.getPickupTime())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert CreateReservationRequest DTO to Reservation entity.
     * Note: Location must be set separately after fetching from DB.
     */
    public Reservation toEntity(CreateReservationRequest request) {
        if (request == null) {
            return null;
        }
        return Reservation.builder()
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .chickenCount(request.getChickenCount())
                .friesCount(request.getFriesCount())
                .pickupTime(request.getPickupTime())
                .notes(request.getNotes())
                .status(ReservationStatus.PENDING)
                .build();
    }
}

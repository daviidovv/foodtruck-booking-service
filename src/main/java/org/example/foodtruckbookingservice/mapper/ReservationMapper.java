package org.example.foodtruckbookingservice.mapper;

import org.example.foodtruckbookingservice.dto.request.CreateReservationRequest;
import org.example.foodtruckbookingservice.dto.response.ReservationResponse;
import org.example.foodtruckbookingservice.entity.Location;
import org.example.foodtruckbookingservice.entity.Reservation;
import org.example.foodtruckbookingservice.entity.ReservationStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
                .confirmationCode(entity.getConfirmationCode())
                .locationId(location != null ? location.getId() : null)
                .locationName(location != null ? location.getName() : null)
                .locationAddress(location != null ? location.getAddress() : null)
                .customerName(entity.getCustomerName())
                .customerEmail(entity.getCustomerEmail())
                .chickenCount(entity.getChickenCount())
                .friesCount(entity.getFriesCount())
                .reservationDate(entity.getReservationDate())
                .pickupTime(entity.getPickupTime())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .canCancel(entity.getStatus().canCancel())
                .build();
    }

    /**
     * Convert CreateReservationRequest DTO to Reservation entity.
     * Note: Location and confirmationCode must be set separately.
     */
    public Reservation toEntity(CreateReservationRequest request, String confirmationCode) {
        if (request == null) {
            return null;
        }
        return Reservation.builder()
                .confirmationCode(confirmationCode)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .chickenCount(request.getChickenCount())
                .friesCount(request.getFriesCount())
                .reservationDate(LocalDate.now())  // Always today (same-day only)
                .pickupTime(request.getPickupTime())  // Can be null
                .notes(request.getNotes())
                .status(ReservationStatus.CONFIRMED)  // Auto-confirmed!
                .build();
    }
}

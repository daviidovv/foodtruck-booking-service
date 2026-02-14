package org.example.foodtruckbookingservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.foodtruckbookingservice.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Response DTO for reservation data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private UUID id;

    /**
     * Unique confirmation code for customer lookup and cancellation.
     * Format: 8 uppercase alphanumeric characters (e.g., "HUHNK4M7")
     */
    private String confirmationCode;

    private UUID locationId;
    private String locationName;
    private String locationAddress;
    private String customerName;
    private String customerEmail;
    private Integer chickenCount;
    private Integer friesCount;

    /**
     * The date of the reservation (only same-day reservations allowed).
     */
    private LocalDate reservationDate;

    /**
     * Optional pickup time. Null if customer didn't specify a time.
     */
    private LocalTime pickupTime;

    private ReservationStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Whether the reservation can be cancelled.
     * True if status is CONFIRMED.
     */
    private Boolean canCancel;

    /**
     * Optional message (e.g., after creation).
     */
    private String message;
}

package org.example.foodtruckbookingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Entity representing weekly operating schedule for a location.
 *
 * <p>Defines which days a location is open, with operating hours and daily capacity.
 * Uses ISO 8601 weekday numbering: 1=Monday, 7=Sunday.
 */
@Entity
@Table(name = "location_schedule",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_location_schedule_location_day",
                columnNames = {"location_id", "day_of_week"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /**
     * Day of week (ISO 8601): 1=Monday, 2=Tuesday, ..., 7=Sunday.
     */
    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    /**
     * Maximum number of chickens available for this day.
     * Fries are unlimited in MVP.
     */
    @Column(name = "daily_capacity", nullable = false)
    private Integer dailyCapacity;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}

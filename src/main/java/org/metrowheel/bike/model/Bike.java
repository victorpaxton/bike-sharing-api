package org.metrowheel.bike.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metrowheel.common.model.BaseEntity;
import org.metrowheel.station.model.Station;

import java.time.LocalDate;

/**
 * Entity representing a bike in the system.
 */
@Entity
@Table(name = "bikes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bike extends BaseEntity {
    
    @Column(name = "bike_number", nullable = false, unique = true)
    private String bikeNumber; // #B-001, #E-1234
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BikeType type; // STANDARD, ELECTRIC
    
    @Column(name = "battery_level")
    private Integer batteryLevel; // For electric bikes
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private BikeStatus status = BikeStatus.AVAILABLE;
    
    // Current station where the bike is located
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station currentStation;
    
    @Column(name = "model_name")
    private String modelName;
    
    @Column(name = "manufacture_year")
    private Integer manufactureYear;
    
    @Column(name = "last_maintenance_date")
    private LocalDate lastMaintenanceDate;
    
    @Column(name = "average_rating")
    private Double averageRating;
    
    @Column(name = "total_ratings")
    @Builder.Default
    private Integer totalRatings = 0;

    /**
     * Convenience method to check if bike is available
     *
     * @return true if the bike status is AVAILABLE
     */
    public boolean isAvailable() {
        return status == BikeStatus.AVAILABLE;
    }
}

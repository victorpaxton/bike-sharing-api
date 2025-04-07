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
import org.metrowheel.user.model.User;

import java.time.LocalDateTime;

/**
 * Entity for tracking the history of bike movements and usage.
 */
@Entity
@Table(name = "bike_history")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeHistory extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;
    
    @ManyToOne
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;
    
    @ManyToOne
    @JoinColumn(name = "end_station_id")
    private Station endStation;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "distance_traveled")
    private Double distanceTraveled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trip_status", nullable = false)
    @Builder.Default
    private TripStatus status = TripStatus.ACTIVE;
    
    public enum TripStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED
    }
}

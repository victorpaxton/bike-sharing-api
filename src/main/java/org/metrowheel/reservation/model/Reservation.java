package org.metrowheel.reservation.model;

import jakarta.persistence.*;
import lombok.*;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.common.model.BaseEntity;
import org.metrowheel.station.model.Station;
import org.metrowheel.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a bike reservation in the system.
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "start_station_id", nullable = false)
    private Station startStation;
    
    @ManyToOne
    @JoinColumn(name = "end_station_id")
    private Station endStation;
    
    @Column(nullable = false)
    private LocalDateTime reservationTime;
    
    @Column(nullable = false)
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @Column(nullable = false)
    private Integer durationMinutes;
    
    private Double distanceTraveled;
    
    @Column(nullable = false)
    private BigDecimal baseRate;
    
    @Column(nullable = false)
    private BigDecimal timeCost;
    
    @Column(nullable = false)
    private BigDecimal discount;
    
    @Column(nullable = false)
    private BigDecimal totalCost;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
}

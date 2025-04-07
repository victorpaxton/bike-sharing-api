package org.metrowheel.reservation.model;

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
import org.metrowheel.bike.model.Bike;
import org.metrowheel.common.model.BaseEntity;
import org.metrowheel.station.model.Station;
import org.metrowheel.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bike reservation in the system.
 */
@Entity
@Table(name = "reservations")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "bike_id")
    private Bike bike;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station startStation;
    
    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;
    
    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @Column(nullable = false)
    private BigDecimal cost;
    
    @Column(name = "premium_discount")
    @Builder.Default
    private Boolean isPremiumDiscount = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ReservationStatus status = ReservationStatus.SCHEDULED;
}

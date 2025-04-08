package org.metrowheel.reservation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.station.model.StationDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for reservation responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private UUID id;
    private BikeDTO bike;
    private StationDTO startStation;
    private StationDTO endStation;
    private LocalDateTime reservationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    private Double distanceTraveled;
    private BigDecimal baseRate;
    private BigDecimal timeCost;
    private BigDecimal discount;
    private BigDecimal totalCost;
    private ReservationStatus status;
} 
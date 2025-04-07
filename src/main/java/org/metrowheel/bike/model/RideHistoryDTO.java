package org.metrowheel.bike.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for ride history display.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideHistoryDTO {
    private String id;
    private String fromStationName;
    private String toStationName;
    private LocalDateTime rideDate;
    private String bikeNumber;
    private Double distanceKm;
    private Long durationMinutes;
    private BigDecimal baseRate;
    private BigDecimal timeCost;
    private BigDecimal discount;
    private BigDecimal totalCost;
    private String status;
}

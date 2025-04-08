package org.metrowheel.reservation.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for creating a new reservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationRequest {
    
    @NotNull(message = "Bike ID is required")
    private UUID bikeId;
    
    @NotNull(message = "Station ID is required")
    private UUID stationId;
    
    @NotNull(message = "Duration is required")
    @Min(value = 30, message = "Minimum duration is 30 minutes")
    private Integer durationMinutes;
} 
package org.metrowheel.reservation.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request model for ending a bike rental
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndRentalRequest {
    
    @NotNull(message = "Return station ID is required")
    private UUID returnStationId;
} 
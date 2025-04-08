package org.metrowheel.bike.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for adding a bike to a station.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddBikeToStationRequest {
    
    @NotBlank(message = "Bike number is required")
    private String bikeNumber;
    
    @NotNull(message = "Bike type is required")
    private BikeType type;
} 
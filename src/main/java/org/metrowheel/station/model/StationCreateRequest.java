package org.metrowheel.station.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request model for creating a new station.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StationCreateRequest {
    
    @NotBlank(message = "Station name is required")
    private String name;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    @NotNull(message = "Latitude is required")
    private Double latitude;
    
    @NotNull(message = "Longitude is required")
    private Double longitude;
    
    @NotBlank(message = "City is required")
    private String city;
    
    private String district;
    
    private String ward;
    
    /**
     * Base64-encoded image data for station photo
     * Will be uploaded to Cloudinary during station creation
     */
    private String base64Image;
    
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be greater than 0")
    private Integer capacity;
}

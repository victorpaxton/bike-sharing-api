package org.metrowheel.station.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request object for searching for stations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationSearchRequest {
    // Current location of the user
    private Double latitude;
    private Double longitude;
    
    // Search radius in meters (default: 500m as shown in UI)
    private Integer radius = 500;
    
    // Filtering options
    private String nameFilter; // For searching by station name
    private Boolean onlyWithAvailableBikes = false;
    private Boolean onlyWithAvailableDocks = false;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 20;
}

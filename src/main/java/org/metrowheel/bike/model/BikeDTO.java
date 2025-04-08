package org.metrowheel.bike.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.metrowheel.station.model.StationDTO;

import java.time.LocalDate;

/**
 * DTO for bike responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BikeDTO {
    private String id;
    private String bikeNumber;
    private BikeType type;
    private Integer batteryLevel;
    private BikeStatus status;
    private String modelName;
    private Integer manufactureYear;
    private LocalDate lastMaintenanceDate;
    private Double averageRating;
    private Integer totalRatings;
    private StationDTO currentStation;
} 
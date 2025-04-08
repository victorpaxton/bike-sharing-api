package org.metrowheel.station.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.metrowheel.bike.model.BikeDTO;

import java.util.List;

/**
 * Data Transfer Object for Station information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StationDTO {
    private String id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String city;
    private String district;
    private String ward;
    private String imageUrl;
    private Integer availableStandardBikes;
    private Integer availableElectricBikes;
    private Integer capacity;
    private Boolean status;
    private Double distance;
    private List<BikeDTO> bikes;
}

package org.metrowheel.station.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.common.model.BaseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bike rental station with geographical coordinates and status information.
 */
@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Station extends BaseEntity {

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false)
    private Double longitude;
    
    private String city;
    
    private String district;
    
    private String ward;
    
    @Column(name = "zip_code")
    private String zipCode;
    
    /**
     * URL or path to the station's image
     */
    @Column(name = "image_url")
    private String imageUrl;
    
    /**
     * Total capacity of bikes at this station
     */
    private Integer capacity;
    
    /**
     * Current number of available standard bikes at this station
     */
    @Column(name = "available_standard_bikes")
    private Integer availableStandardBikes = 0;
    
    /**
     * Current number of available electric bikes at this station
     */
    @Column(name = "available_electric_bikes")
    private Integer availableElectricBikes = 0;
    
    /**
     * Whether this station is currently operational
     */
    private Boolean status;
    
    /**
     * The bikes currently docked at this station
     */
    @OneToMany(mappedBy = "currentStation")
    private List<Bike> bikes = new ArrayList<>();
}

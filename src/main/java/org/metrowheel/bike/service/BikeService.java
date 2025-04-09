package org.metrowheel.bike.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.metrowheel.bike.model.AddBikeToStationRequest;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.bike.model.BikeStatus;
import org.metrowheel.bike.model.BikeType;
import org.metrowheel.bike.repository.BikeRepository;
import org.metrowheel.station.model.Station;
import org.metrowheel.station.model.StationDTO;
import org.metrowheel.station.repository.StationRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for bike-related operations.
 */
@ApplicationScoped
public class BikeService {

    @Inject
    BikeRepository bikeRepository;
    
    @Inject
    StationRepository stationRepository;

    /**
     * Get all bikes in the system
     * 
     * @return List of all bikes as DTOs
     */
    public List<BikeDTO> getAllBikes() {
        return bikeRepository.listAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Add a bike to a station. If the bike doesn't exist, it will be created.
     * 
     * @param stationId The ID of the station to add the bike to
     * @param request The request containing bike number and type
     * @return The updated bike as DTO
     */
    @Transactional
    public BikeDTO addBikeToStation(String stationId, AddBikeToStationRequest request) {
        // Find the bike by bike number
        Bike bike = bikeRepository.find("bikeNumber", request.getBikeNumber()).firstResult();
        
        // If bike doesn't exist, create a new one
        if (bike == null) {
            bike = Bike.builder()
                    .bikeNumber(request.getBikeNumber())
                    .type(request.getType())
                    .status(BikeStatus.AVAILABLE)
                    .manufactureYear(LocalDate.now().getYear())
                    .totalRatings(0)
                    .build();
            
            // For electric bikes, set initial battery level
            if (request.getType() == BikeType.ELECTRIC) {
                bike.setBatteryLevel(100);
            }
            
            bikeRepository.persist(bike);
        }
        
        // Find the station
        UUID stationUuid = UUID.fromString(stationId);
        Station station = stationRepository.findByIdOptional(stationUuid)
                .orElseThrow(() -> new NotFoundException("Station not found with id: " + stationId));
        
        // Check if station is active
        if (!station.getStatus()) {
            throw new BadRequestException("Cannot add bike to inactive station");
        }
        
        // Check if station has available capacity
        int currentBikes = station.getAvailableStandardBikes() + station.getAvailableElectricBikes();
        if (currentBikes >= station.getCapacity()) {
            throw new BadRequestException("Station is at full capacity");
        }
        
        // Update bike's current station
        bike.setCurrentStation(station);
        bike.setStatus(BikeStatus.AVAILABLE);
        
        // Update station's bike count
        if (bike.getType() == BikeType.STANDARD) {
            station.setAvailableStandardBikes(station.getAvailableStandardBikes() + 1);
        } else {
            station.setAvailableElectricBikes(station.getAvailableElectricBikes() + 1);
        }
        
        // Persist changes
        bikeRepository.persist(bike);
        stationRepository.persist(station);
        
        return mapToDTO(bike);
    }
    
    /**
     * Map a Bike entity to a BikeDTO
     * 
     * @param bike The bike entity
     * @return The bike DTO
     */
    public BikeDTO mapToDTO(Bike bike) {
        StationDTO stationDTO = null;
        if (bike.getCurrentStation() != null) {
            stationDTO = StationDTO.builder()
                    .id(bike.getCurrentStation().getId().toString())
                    .name(bike.getCurrentStation().getName())
                    .address(bike.getCurrentStation().getAddress())
                    .latitude(bike.getCurrentStation().getLatitude())
                    .longitude(bike.getCurrentStation().getLongitude())
                    .city(bike.getCurrentStation().getCity())
                    .district(bike.getCurrentStation().getDistrict())
                    .ward(bike.getCurrentStation().getWard())
                    .imageUrl(bike.getCurrentStation().getImageUrl())
                    .availableStandardBikes(bike.getCurrentStation().getAvailableStandardBikes())
                    .availableElectricBikes(bike.getCurrentStation().getAvailableElectricBikes())
                    .capacity(bike.getCurrentStation().getCapacity())
                    .status(bike.getCurrentStation().getStatus())
                    .build();
        }
        
        return BikeDTO.builder()
                .id(bike.getId().toString())
                .bikeNumber(bike.getBikeNumber())
                .type(bike.getType())
                .batteryLevel(bike.getBatteryLevel())
                .status(bike.getStatus())
                .modelName(bike.getModelName())
                .manufactureYear(bike.getManufactureYear())
                .lastMaintenanceDate(bike.getLastMaintenanceDate())
                .averageRating(bike.getAverageRating())
                .totalRatings(bike.getTotalRatings())
                .currentStation(stationDTO)
                .build();
    }
} 
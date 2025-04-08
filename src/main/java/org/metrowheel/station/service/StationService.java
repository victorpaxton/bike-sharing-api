package org.metrowheel.station.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.bike.service.BikeService;
import org.metrowheel.media.service.MediaService;
import org.metrowheel.station.model.Station;
import org.metrowheel.station.model.StationCreateRequest;
import org.metrowheel.station.model.StationDTO;
import org.metrowheel.station.model.StationSearchRequest;
import org.metrowheel.station.repository.StationRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for station-related operations
 */
@ApplicationScoped
public class StationService {

    private static final Logger LOGGER = Logger.getLogger(StationService.class);
    private static final String STATIONS_FOLDER = "stations";

    @Inject
    StationRepository stationRepository;
    
    @Inject
    MediaService mediaService;
    
    @Inject
    BikeService bikeService;

    /**
     * Search for stations based on the provided criteria
     * 
     * @param request The search request containing filters and location
     * @return List of stations matching the criteria
     */
    public List<StationDTO> searchStations(StationSearchRequest request) {
        List<Station> stations;
        
        // If both latitude and longitude are provided, search by proximity
        if (request.getLatitude() != null && request.getLongitude() != null) {
            stations = stationRepository.findNearbyStations(
                    request.getLatitude(), 
                    request.getLongitude(), 
                    request.getRadius() / 1000.0 // Convert meters to kilometers
            );
        } else if (request.getNameFilter() != null && !request.getNameFilter().trim().isEmpty()) {
            // If name filter is provided, search by name
            stations = stationRepository.findByNameContaining(request.getNameFilter());
        } else {
            // Otherwise, get all active stations
            stations = stationRepository.findActiveStations();
        }
        
        // Apply additional filters
        if (request.getOnlyWithAvailableBikes()) {
            stations = stations.stream()
                    .filter(s -> s.getAvailableStandardBikes() > 0 || s.getAvailableElectricBikes() > 0)
                    .collect(Collectors.toList());
        }
        
        if (request.getOnlyWithAvailableDocks()) {
            stations = stations.stream()
                    .filter(s -> (s.getCapacity() - s.getAvailableStandardBikes() - s.getAvailableElectricBikes()) > 0)
                    .collect(Collectors.toList());
        }
        
        // Calculate distances and create DTOs
        List<StationDTO> stationDTOs = new ArrayList<>();
        
        if (request.getLatitude() != null && request.getLongitude() != null) {
            // Calculate distance for each station and sort by distance
            for (Station station : stations) {
                StationDTO dto = mapToDTO(station);
                
                // Calculate straight-line distance in meters (Haversine formula)
                dto.setDistance(calculateDistance(
                        request.getLatitude(), request.getLongitude(),
                        station.getLatitude(), station.getLongitude()));
                
                stationDTOs.add(dto);
            }
            
            // Sort by distance
            stationDTOs.sort(Comparator.comparing(StationDTO::getDistance));
        } else {
            // When location is not provided, don't calculate distances
            for (Station station : stations) {
                stationDTOs.add(mapToDTO(station));
            }
        }
        
        // Handle pagination if needed
        if (request.getPage() != null && request.getSize() != null) {
            int startIndex = request.getPage() * request.getSize();
            int endIndex = Math.min(startIndex + request.getSize(), stationDTOs.size());
            
            if (startIndex < stationDTOs.size()) {
                return stationDTOs.subList(startIndex, endIndex);
            }
            return new ArrayList<>();
        }
        
        return stationDTOs;
    }

    /**
     * Get a single station by ID
     * 
     * @param id The station ID
     * @return The station DTO or null if not found
     */
    public StationDTO getStationById(UUID id) {
        return stationRepository.findByIdOptional(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    /**
     * Create a new station from a StationCreateRequest
     * 
     * @param request The station creation request
     * @return The created station
     */
    @Transactional
    public StationDTO createStation(StationCreateRequest request) {
        Station station = new Station();
        
        station.setName(request.getName());
        station.setAddress(request.getAddress());
        station.setLatitude(request.getLatitude());
        station.setLongitude(request.getLongitude());
        station.setCity(request.getCity());
        station.setDistrict(request.getDistrict());
        station.setWard(request.getWard());
        station.setCapacity(request.getCapacity());
        station.setStatus(true); // Default to active
        station.setAvailableStandardBikes(0);
        station.setAvailableElectricBikes(0);
        
        // Handle image upload if base64 image is provided
        if (request.getBase64Image() != null && !request.getBase64Image().isEmpty()) {
            try {
                // Upload image to Cloudinary
                String imageUrl = mediaService.uploadImage(
                        java.util.Base64.getDecoder().decode(request.getBase64Image()),
                        STATIONS_FOLDER
                );
                station.setImageUrl(imageUrl);
                LOGGER.info("Uploaded station image to Cloudinary: " + imageUrl);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Invalid base64 image data", e);
                throw new BadRequestException("Invalid base64 image data: " + e.getMessage());
            } catch (IOException e) {
                LOGGER.error("Failed to upload station image", e);
                throw new BadRequestException("Failed to upload station image: " + e.getMessage());
            }
        }
        
        stationRepository.persist(station);
        return mapToDTO(station);
    }

    /**
     * Create a new station from StationDTO (legacy support)
     * 
     * @param stationDTO The station data
     * @return The created station
     */
    @Transactional
    public StationDTO createStation(StationDTO stationDTO) {
        Station station = new Station();
        updateStationFromDTO(station, stationDTO);
        
        stationRepository.persist(station);
        return mapToDTO(station);
    }

    /**
     * Update an existing station
     * 
     * @param id The station ID
     * @param stationDTO The updated station data
     * @return The updated station
     */
    @Transactional
    public StationDTO updateStation(UUID id, StationDTO stationDTO) {
        Station station = stationRepository.findByIdOptional(id).orElse(null);
        if (station == null) {
            return null;
        }
        
        updateStationFromDTO(station, stationDTO);
        stationRepository.persist(station);
        return mapToDTO(station);
    }

    /**
     * Delete a station
     * 
     * @param id The station ID
     * @return True if deleted, false if not found
     */
    @Transactional
    public boolean deleteStation(UUID id) {
        return stationRepository.deleteById(id);
    }

    /**
     * Map a Station entity to a StationDTO
     * 
     * @param station The station entity
     * @return The station DTO
     */
    private StationDTO mapToDTO(Station station) {
        // Map station to DTO
        StationDTO dto = StationDTO.builder()
                .id(station.getId().toString())
                .name(station.getName())
                .address(station.getAddress())
                .latitude(station.getLatitude())
                .longitude(station.getLongitude())
                .city(station.getCity())
                .district(station.getDistrict())
                .ward(station.getWard())
                .imageUrl(station.getImageUrl())
                .availableStandardBikes(station.getAvailableStandardBikes())
                .availableElectricBikes(station.getAvailableElectricBikes())
                .capacity(station.getCapacity())
                .status(station.getStatus())
                .build();
        
        // Map bikes to DTOs if they exist
        if (station.getBikes() != null && !station.getBikes().isEmpty()) {
            List<BikeDTO> bikeDTOs = station.getBikes().stream()
                    .map(bikeService::mapToDTO)
                    .collect(Collectors.toList());
            dto.setBikes(bikeDTOs);
        }
        
        return dto;
    }

    /**
     * Update a Station entity from a StationDTO
     * 
     * @param station The station entity to update
     * @param dto The source DTO
     */
    private void updateStationFromDTO(Station station, StationDTO dto) {
        station.setName(dto.getName());
        station.setAddress(dto.getAddress());
        station.setLatitude(dto.getLatitude());
        station.setLongitude(dto.getLongitude());
        station.setCity(dto.getCity());
        station.setDistrict(dto.getDistrict());
        station.setWard(dto.getWard());
        station.setImageUrl(dto.getImageUrl());
        station.setAvailableStandardBikes(dto.getAvailableStandardBikes());
        station.setAvailableElectricBikes(dto.getAvailableElectricBikes());
        station.setCapacity(dto.getCapacity());
        station.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
    }

    /**
     * Calculate the distance between two coordinates using the Haversine formula
     * 
     * @param lat1 First latitude
     * @param lon1 First longitude
     * @param lat2 Second latitude
     * @param lon2 Second longitude
     * @return Distance in meters
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        // Convert to meters
        return R * c * 1000;
    }
}

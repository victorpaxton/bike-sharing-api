package org.metrowheel.station.service;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.metrowheel.station.model.StationMapDTO;
import org.metrowheel.station.repository.StationRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class StationMapCacheService {

    private static final Logger LOGGER = Logger.getLogger(StationMapCacheService.class);

    @Inject
    StationRepository stationRepository;

    @CacheResult(cacheName = "station-map")
    public List<StationMapDTO> getCachedStations() {
        LOGGER.info("Cache miss - Fetching fresh station data for map");
        List<StationMapDTO> stations = stationRepository.findActiveStations().stream()
                .map(this::mapToMapDTO)
                .collect(Collectors.toList());
        LOGGER.info("Fetched " + stations.size() + " stations for map");
        return stations;
    }

    public Optional<List<StationMapDTO>> getCachedDataIfExists() {
        try {
            List<StationMapDTO> cachedData = getCachedStations();
            if (cachedData != null && !cachedData.isEmpty()) {
                LOGGER.info("Cache hit - Returning cached data");
                return Optional.of(cachedData);
            }
        } catch (Exception e) {
            LOGGER.warn("Error accessing cache: " + e.getMessage());
        }
        return Optional.empty();
    }

    @CacheInvalidate(cacheName = "station-map")
    public void invalidateCache() {
        LOGGER.info("Cache invalidated - Station data changed");
    }

    private StationMapDTO mapToMapDTO(org.metrowheel.station.model.Station station) {
        StationMapDTO dto = new StationMapDTO();
        dto.setId(station.getId());
        dto.setName(station.getName());
        dto.setAddress(station.getAddress());
        dto.setImageUrl(station.getImageUrl());
        dto.setLatitude(station.getLatitude());
        dto.setLongitude(station.getLongitude());
        dto.setAvailableStandardBikes(station.getAvailableStandardBikes());
        dto.setAvailableElectricBikes(station.getAvailableElectricBikes());
        dto.setCapacity(station.getCapacity());
        dto.setStatus(station.getStatus());
        return dto;
    }
} 
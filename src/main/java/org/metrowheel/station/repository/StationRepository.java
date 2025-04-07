package org.metrowheel.station.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.station.model.Station;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Station entity
 */
@ApplicationScoped
public class StationRepository implements PanacheRepositoryBase<Station, UUID> {
    
    /**
     * Find stations by city
     * 
     * @param city The city to search for
     * @return List of stations in the specified city
     */
    public List<Station> findByCity(String city) {
        return list("city", city);
    }
    
    /**
     * Find active stations
     * 
     * @return List of active stations
     */
    public List<Station> findActiveStations() {
        return list("active", true);
    }
    
    /**
     * Find stations with available bikes
     * 
     * @return List of stations that have available bikes
     */
    public List<Station> findStationsWithAvailableBikes() {
        return list("availableBikes > 0");
    }
    
    /**
     * Find stations with available docks
     * 
     * @return List of stations that have available docks for bike returns
     */
    public List<Station> findStationsWithAvailableDocks() {
        return list("availableDocks > 0");
    }
    
    /**
     * Find nearby stations based on coordinates and radius
     * 
     * @param latitude Current latitude
     * @param longitude Current longitude
     * @param radiusKm Radius in kilometers
     * @return List of nearby stations
     */
    public List<Station> findNearbyStations(Double latitude, Double longitude, Double radiusKm) {
        // Using the Haversine formula to calculate distance between coordinates
        String query = "SELECT s FROM Station s WHERE " +
                "(6371 * acos(cos(radians(?1)) * cos(radians(s.latitude)) * cos(radians(s.longitude) - radians(?2)) + sin(radians(?1)) * sin(radians(s.latitude)))) < ?3";
        return find(query, latitude, longitude, radiusKm).list();
    }
    
    /**
     * Find stations with name containing the search term
     * 
     * @param namePart The name part to search for
     * @return List of stations with names containing the search term
     */
    public List<Station> findByNameContaining(String namePart) {
        return find("LOWER(name) LIKE LOWER(?1) AND active = true", "%" + namePart + "%").list();
    }
}

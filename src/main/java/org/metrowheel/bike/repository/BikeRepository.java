package org.metrowheel.bike.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeStatus;
import org.metrowheel.bike.model.BikeType;
import org.metrowheel.station.model.Station;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Bike entity
 */
@ApplicationScoped
public class BikeRepository implements PanacheRepositoryBase<Bike, UUID> {
    
    /**
     * Find bikes by type
     * 
     * @param type The bike type to search for
     * @return List of bikes of the specified type
     */
    public List<Bike> findByType(BikeType type) {
        return list("type", type);
    }
    
    /**
     * Find available bikes
     * 
     * @return List of available bikes
     */
    public List<Bike> findAvailableBikes() {
        return list("status", BikeStatus.AVAILABLE);
    }
    
    /**
     * Find bikes by station
     * 
     * @param station The station where bikes are located
     * @return List of bikes at the specified station
     */
    public List<Bike> findByStation(Station station) {
        return list("currentStation", station);
    }
    
    /**
     * Find available bikes at a specific station
     * 
     * @param station The station to check
     * @return List of available bikes at the specified station
     */
    public List<Bike> findAvailableBikesByStation(Station station) {
        return list("currentStation = ?1 and status = ?2", station, BikeStatus.AVAILABLE);
    }
    
    /**
     * Find available bikes by type at a specific station
     * 
     * @param station The station to check
     * @param type The bike type to search for
     * @return List of available bikes of the specified type at the specified station
     */
    public List<Bike> findAvailableBikesByStationAndType(Station station, BikeType type) {
        return list("currentStation = ?1 and type = ?2 and status = ?3", station, type, BikeStatus.AVAILABLE);
    }
    
    /**
     * Find bikes that need maintenance (e.g., electric bikes with low battery)
     * 
     * @param minBatteryLevel Minimum battery level threshold for electric bikes
     * @return List of bikes that need maintenance
     */
    public List<Bike> findBikesNeedingMaintenance(Integer minBatteryLevel) {
        return find("(type = ?1 and batteryLevel < ?2) or status in (?3, ?4)", 
                BikeType.ELECTRIC, minBatteryLevel, BikeStatus.MAINTENANCE, BikeStatus.DAMAGED).list();
    }
}

package org.metrowheel.bike.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeHistory;
import org.metrowheel.bike.model.BikeHistory.TripStatus;
import org.metrowheel.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for BikeHistory entity
 */
@ApplicationScoped
public class BikeHistoryRepository implements PanacheRepositoryBase<BikeHistory, UUID> {
    
    /**
     * Find active trips for a bike
     * 
     * @param bike The bike to check for active trips
     * @return List of active bike history records
     */
    public List<BikeHistory> findActiveTripsForBike(Bike bike) {
        return list("bike = ?1 and status = ?2", bike, TripStatus.ACTIVE);
    }
    
    /**
     * Find active trip for a user
     * 
     * @param user The user to check for active trips
     * @return The active bike history record or null if none found
     */
    public BikeHistory findActiveTripsForUser(User user) {
        return find("user = ?1 and status = ?2", user, TripStatus.ACTIVE).firstResult();
    }
    
    /**
     * Find trip history for a specific bike
     * 
     * @param bike The bike to check history for
     * @return List of bike history records
     */
    public List<BikeHistory> findTripHistoryForBike(Bike bike) {
        return list("bike = ?1 ORDER BY startTime DESC", bike);
    }
    
    /**
     * Find trip history for a specific user
     * 
     * @param user The user to check history for
     * @return List of bike history records
     */
    public List<BikeHistory> findTripHistoryForUser(User user) {
        return list("user = ?1 ORDER BY startTime DESC", user);
    }
    
    /**
     * Find trips within a date range
     * 
     * @param startDate Start date of the range
     * @param endDate End date of the range
     * @return List of bike history records
     */
    public List<BikeHistory> findTripsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return list("startTime >= ?1 and startTime <= ?2", startDate, endDate);
    }
}

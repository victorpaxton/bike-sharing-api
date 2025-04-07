package org.metrowheel.bike.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeReview;
import org.metrowheel.user.model.User;

import java.util.List;
import java.util.UUID;

/**
 * Repository for BikeReview entity.
 */
@ApplicationScoped
public class BikeReviewRepository implements PanacheRepository<BikeReview> {
    
    /**
     * Find reviews by bike
     * 
     * @param bike The bike to find reviews for
     * @return List of reviews for the bike
     */
    public List<BikeReview> findByBike(Bike bike) {
        return list("bike", bike);
    }
    
    /**
     * Find reviews by bike ID
     * 
     * @param bikeId The bike ID
     * @return List of reviews for the bike
     */
    public List<BikeReview> findByBikeId(UUID bikeId) {
        return list("bike.id", bikeId);
    }
    
    /**
     * Find reviews by user
     * 
     * @param user The user who created the reviews
     * @return List of reviews by the user
     */
    public List<BikeReview> findByUser(User user) {
        return list("user", user);
    }
    
    /**
     * Check if a user has already reviewed a specific trip
     * 
     * @param tripId The bike history/trip ID
     * @param userId The user ID
     * @return true if a review exists
     */
    public boolean existsByTripAndUser(UUID tripId, UUID userId) {
        return count("trip.id = ?1 and user.id = ?2", tripId, userId) > 0;
    }
    
    /**
     * Calculate average rating for a bike
     * 
     * @param bikeId The bike ID
     * @return The average rating
     */
    public Double calculateAverageRating(UUID bikeId) {
        return getEntityManager()
                .createQuery("SELECT AVG(r.rating) FROM BikeReview r WHERE r.bike.id = :bikeId", Double.class)
                .setParameter("bikeId", bikeId)
                .getSingleResult();
    }
    
    /**
     * Count total number of ratings for a bike
     * 
     * @param bikeId The bike ID
     * @return Count of ratings
     */
    public Long countRatingsByBike(UUID bikeId) {
        return count("bike.id", bikeId);
    }
}

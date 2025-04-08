package org.metrowheel.bike.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeReview;
import org.metrowheel.user.model.User;
import org.metrowheel.reservation.model.Reservation;

import java.util.List;
import java.util.Optional;
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
     * Find review by reservation
     */
    public Optional<BikeReview> findByReservation(Reservation reservation) {
        return find("reservation", reservation).firstResultOptional();
    }

    /**
     * Get average rating for a bike
     */
    public double getAverageRatingForBike(UUID bikeId) {
        return find("bike.id", bikeId)
                .stream()
                .mapToInt(BikeReview::getRating)
                .average()
                .orElse(0.0);
    }
}

package org.metrowheel.bike.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeHistory;
import org.metrowheel.bike.model.BikeReview;
import org.metrowheel.bike.model.BikeReviewDTO;
import org.metrowheel.bike.repository.BikeRepository;
import org.metrowheel.bike.repository.BikeHistoryRepository;
import org.metrowheel.bike.repository.BikeReviewRepository;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling bike reviews.
 */
@ApplicationScoped
public class BikeReviewService {

    @Inject
    BikeReviewRepository reviewRepository;
    
    @Inject
    BikeRepository bikeRepository;
    
    @Inject
    BikeHistoryRepository bikeHistoryRepository;
    
    @Inject
    UserService userService;
    
    /**
     * Create a new review for a bike after a completed trip
     * 
     * @param userId The ID of the user creating the review
     * @param tripId The ID of the completed bike trip/history
     * @param rating The rating (1-5)
     * @param reviewText The text review (optional)
     * @return The created review as DTO
     */
    @Transactional
    public BikeReviewDTO createReview(UUID userId, UUID tripId, Integer rating, String reviewText) {
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        
        // Get user
        User user = userService.findById(userId);
        
        // Get trip
        BikeHistory trip = bikeHistoryRepository.findByIdOptional(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + tripId));
        
        // Verify trip belongs to user
        if (!trip.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only review your own trips");
        }
        
        // Verify trip is completed
        if (trip.getStatus() != BikeHistory.TripStatus.COMPLETED) {
            throw new BadRequestException("You can only review completed trips");
        }
        
        // Check if user already reviewed this trip
        if (reviewRepository.existsByTripAndUser(tripId, userId)) {
            throw new BadRequestException("You have already reviewed this trip");
        }
        
        // Create review
        BikeReview review = BikeReview.builder()
                .bike(trip.getBike())
                .user(user)
                .trip(trip)
                .rating(rating)
                .reviewText(reviewText)
                .reviewDate(LocalDateTime.now())
                .isVerifiedRide(true)
                .build();
        
        reviewRepository.persist(review);
        
        // Update bike's average rating and total ratings
        updateBikeRating(trip.getBike().getId());
        
        return mapToDTO(review);
    }
    
    /**
     * Update a bike's average rating and total ratings count
     * 
     * @param bikeId The bike ID
     */
    @Transactional
    public void updateBikeRating(UUID bikeId) {
        Double averageRating = reviewRepository.calculateAverageRating(bikeId);
        Long totalRatings = reviewRepository.countRatingsByBike(bikeId);
        
        Bike bike = bikeRepository.findByIdOptional(bikeId)
                .orElseThrow(() -> new NotFoundException("Bike not found with id: " + bikeId));
        
        bike.setAverageRating(averageRating);
        bike.setTotalRatings(totalRatings.intValue());
        
        bikeRepository.persist(bike);
    }
    
    /**
     * Get reviews for a specific bike
     * 
     * @param bikeId The bike ID
     * @return List of reviews
     */
    public List<BikeReviewDTO> getReviewsForBike(UUID bikeId) {
        // Verify bike exists
        if (!bikeRepository.findByIdOptional(bikeId).isPresent()) {
            throw new NotFoundException("Bike not found with id: " + bikeId);
        }
        
        return reviewRepository.findByBikeId(bikeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reviews by a specific user
     * 
     * @param userId The user ID
     * @return List of reviews
     */
    public List<BikeReviewDTO> getReviewsByUser(UUID userId) {
        User user = userService.findById(userId);
        
        return reviewRepository.findByUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Map BikeReview entity to BikeReviewDTO
     * 
     * @param review The review entity
     * @return The review DTO
     */
    private BikeReviewDTO mapToDTO(BikeReview review) {
        return BikeReviewDTO.builder()
                .id(review.getId())
                .bikeId(review.getBike().getId())
                .bikeNumber(review.getBike().getBikeNumber())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFullName())
                .rating(review.getRating())
                .reviewText(review.getReviewText())
                .reviewDate(review.getReviewDate())
                .isVerifiedRide(review.getIsVerifiedRide())
                .build();
    }
}

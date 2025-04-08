package org.metrowheel.bike.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.metrowheel.bike.model.BikeReview;
import org.metrowheel.bike.model.BikeReviewDTO;
import org.metrowheel.bike.model.BikeReviewRequest;
import org.metrowheel.bike.repository.BikeRepository;
import org.metrowheel.bike.repository.BikeReviewRepository;
import org.metrowheel.reservation.model.Reservation;
import org.metrowheel.reservation.model.ReservationStatus;
import org.metrowheel.reservation.repository.ReservationRepository;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling bike reviews.
 */
@ApplicationScoped
public class BikeReviewService {

    @Inject
    BikeReviewRepository bikeReviewRepository;
    
    @Inject
    BikeRepository bikeRepository;
    
    @Inject
    UserService userService;
    
    @Inject
    ReservationRepository reservationRepository;
    
    /**
     * Create a new bike review
     */
    @Transactional
    public BikeReviewDTO createReview(BikeReviewRequest request, User user) {
        // Find and validate the reservation
        Reservation reservation = reservationRepository.findByIdOptional(request.getReservationId())
                .orElseThrow(() -> new BadRequestException("Reservation not found"));

        // Validate that the review is for user's own reservation
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to review this reservation");
        }

        // Validate that the reservation is completed
        if (reservation.getStatus() != ReservationStatus.COMPLETED) {
            throw new BadRequestException("Can only review completed reservations");
        }

        // Check if review already exists for this reservation
        if (bikeReviewRepository.findByReservation(reservation).isPresent()) {
            throw new BadRequestException("Review already exists for this reservation");
        }

        // Create and save the review
        BikeReview review = BikeReview.builder()
                .bike(reservation.getBike())
                .user(user)
                .reservation(reservation)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        bikeReviewRepository.persist(review);

        return mapToDTO(review);
    }
    
    /**
     * Get reviews for a specific bike
     */
    public List<BikeReviewDTO> getBikeReviews(UUID bikeId) {
        return bikeReviewRepository.findByBikeId(bikeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get average rating for a bike
     */
    public double getBikeAverageRating(UUID bikeId) {
        return bikeReviewRepository.getAverageRatingForBike(bikeId);
    }
    
    /**
     * Map BikeReview entity to DTO
     */
    private BikeReviewDTO mapToDTO(BikeReview review) {
        return BikeReviewDTO.builder()
                .id(review.getId())
                .bikeId(review.getBike().getId())
                .userId(review.getUser().getId())
                .reservationId(review.getReservation().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

package org.metrowheel.bike.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.metrowheel.bike.model.BikeHistory;
import org.metrowheel.bike.model.RideHistoryDTO;
import org.metrowheel.bike.repository.BikeHistoryRepository;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for bike history operations.
 */
@ApplicationScoped
public class BikeHistoryService {

    @Inject
    BikeHistoryRepository bikeHistoryRepository;
    
    @Inject
    UserService userService;

    /**
     * Get ride history for a user.
     *
     * @param userId The user ID
     * @return List of ride history DTOs
     */
    public List<RideHistoryDTO> getUserRideHistory(UUID userId) {
        try {
            // Find the user using the user service
            User user = userService.findById(userId);
            
            // Get the ride history for the user
            List<BikeHistory> bikeHistories = bikeHistoryRepository.findTripHistoryForUser(user);
            
            // Map to DTOs
            return bikeHistories.stream()
                    .map(this::mapToDTO)
                    .toList();
        } catch (Exception e) {
            // Log the error and return an empty list
            System.err.println("Error retrieving ride history: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Get details for a specific ride.
     *
     * @param rideId The ride ID
     * @return The ride history DTO or null if not found
     */
    public RideHistoryDTO getRideDetails(UUID rideId) {
        BikeHistory bikeHistory = bikeHistoryRepository.findByIdOptional(rideId).orElse(null);
        if (bikeHistory == null) {
            return null;
        }
        
        return mapToDTO(bikeHistory);
    }
    
    /**
     * Map a BikeHistory entity to a RideHistoryDTO
     *
     * @param bikeHistory The bike history entity
     * @return The ride history DTO
     */
    private RideHistoryDTO mapToDTO(BikeHistory bikeHistory) {
        // Calculate duration in minutes
        Long durationMinutes = 0L;
        if (bikeHistory.getEndTime() != null && bikeHistory.getStartTime() != null) {
            durationMinutes = Duration.between(bikeHistory.getStartTime(), bikeHistory.getEndTime()).toMinutes();
        }
        
        // Calculate costs (in a real application, this would be based on actual pricing rules)
        BigDecimal baseRate = new BigDecimal("2.00");
        BigDecimal timeCost = BigDecimal.ZERO;
        if (durationMinutes > 0) {
            timeCost = new BigDecimal(durationMinutes).multiply(new BigDecimal("0.10"));
        }
        
        // Apply discount if applicable (e.g., premium members get 10% off)
        BigDecimal discount = BigDecimal.ZERO;
        if (bikeHistory.getUser() != null && isPremiumUser(bikeHistory.getUser())) {
            discount = baseRate.add(timeCost).multiply(new BigDecimal("0.10"));
        }
        
        // Calculate total cost
        BigDecimal totalCost = baseRate.add(timeCost).subtract(discount);
        
        // For free rides
        if (isFreeRide(bikeHistory)) {
            baseRate = BigDecimal.ZERO;
            timeCost = BigDecimal.ZERO;
            discount = BigDecimal.ZERO;
            totalCost = BigDecimal.ZERO;
        }
        
        return RideHistoryDTO.builder()
                .id(bikeHistory.getId().toString())
                .fromStationName(bikeHistory.getStartStation() != null ? bikeHistory.getStartStation().getName() : "Unknown")
                .toStationName(bikeHistory.getEndStation() != null ? bikeHistory.getEndStation().getName() : "In Progress")
                .rideDate(bikeHistory.getStartTime())
                .bikeNumber(bikeHistory.getBike() != null ? bikeHistory.getBike().getBikeNumber() : "Unknown")
                .distanceKm(bikeHistory.getDistanceTraveled())
                .durationMinutes(durationMinutes)
                .baseRate(baseRate)
                .timeCost(timeCost)
                .discount(discount)
                .totalCost(totalCost)
                .status(bikeHistory.getStatus().toString())
                .build();
    }
    
    /**
     * Check if a user is a premium member.
     * 
     * @param user The user to check
     * @return True if the user is a premium member
     */
    private boolean isPremiumUser(User user) {
        // In a real application, this would check user subscription status
        // For now, we'll assume users with "PREMIUM" in their roles are premium users
        return user.getRoles() != null && user.getRoles().contains("PREMIUM");
    }
    
    /**
     * Check if a ride is free.
     * 
     * @param bikeHistory The bike history to check
     * @return True if the ride is free
     */
    private boolean isFreeRide(BikeHistory bikeHistory) {
        // In a real application, this would check for promotions, free ride credits, etc.
        // For now, we'll keep it simple and assume all rides have a cost
        return false;
    }
}

package org.metrowheel.reservation.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.jboss.logging.Logger;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.bike.model.BikeStatus;
import org.metrowheel.bike.model.BikeType;
import org.metrowheel.bike.repository.BikeRepository;
import org.metrowheel.bike.service.BikeService;
import org.metrowheel.reservation.model.Reservation;
import org.metrowheel.reservation.model.ReservationDTO;
import org.metrowheel.reservation.model.ReservationRequest;
import org.metrowheel.reservation.model.ReservationStatus;
import org.metrowheel.reservation.repository.ReservationRepository;
import org.metrowheel.station.model.Station;
import org.metrowheel.station.repository.StationRepository;
import org.metrowheel.station.service.StationService;
import org.metrowheel.user.model.SubscriptionPlan;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for handling bike reservations
 */
@ApplicationScoped
public class ReservationService {
    
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class);
    
    // Pricing constants
    private static final BigDecimal STANDARD_BASE_RATE = new BigDecimal("1.00");
    private static final BigDecimal PREMIUM_BASE_RATE = new BigDecimal("0.50");
    private static final BigDecimal STANDARD_MINUTE_RATE = new BigDecimal("0.15");
    private static final BigDecimal PREMIUM_MINUTE_RATE = new BigDecimal("0.10");
    private static final int STANDARD_FREE_MINUTES = 5;
    private static final int PREMIUM_FREE_MINUTES = 60;
    
    @Inject
    ReservationRepository reservationRepository;
    
    @Inject
    BikeRepository bikeRepository;
    
    @Inject
    StationRepository stationRepository;
    
    @Inject
    BikeService bikeService;
    
    @Inject
    StationService stationService;
    
    @Inject
    UserService userService;
    
    /**
     * Create a new reservation
     */
    @Transactional
    public ReservationDTO createReservation(ReservationRequest request, User user) {
        // Validate bike exists and is available
        Bike bike = bikeRepository.findByIdOptional(request.getBikeId())
                .orElseThrow(() -> new BadRequestException("Bike not found"));
                
        if (bike.getStatus() != BikeStatus.AVAILABLE) {
            throw new BadRequestException("Bike is not available for reservation");
        }
        
        // Validate station exists and has the bike
        Station station = stationRepository.findByIdOptional(request.getStationId())
                .orElseThrow(() -> new BadRequestException("Station not found"));
                
        if (!bike.getCurrentStation().getId().equals(station.getId())) {
            throw new BadRequestException("Bike is not at the specified station");
        }
        
        // Check for existing active reservations for the user
        if (!reservationRepository.findActiveReservationsForUser(user).isEmpty()) {
            throw new BadRequestException("User already has an active reservation");
        }
        
        // Calculate costs based on subscription plan and duration
        boolean isPremium = user.getSubscriptionPlan() == SubscriptionPlan.PREMIUM;
        BigDecimal baseRate = isPremium ? PREMIUM_BASE_RATE : STANDARD_BASE_RATE;
        int freeMinutes = isPremium ? PREMIUM_FREE_MINUTES : STANDARD_FREE_MINUTES;
        BigDecimal minuteRate = isPremium ? PREMIUM_MINUTE_RATE : STANDARD_MINUTE_RATE;
        
        // Calculate billable minutes (after free minutes)
        int billableMinutes = Math.max(0, request.getDurationMinutes() - freeMinutes);
        BigDecimal timeCost = minuteRate.multiply(BigDecimal.valueOf(billableMinutes));
        
        // Calculate discount (for premium users)
        BigDecimal discount = BigDecimal.ZERO;
        if (isPremium) {
            // Apply 10% discount on total cost (up to $2)
            BigDecimal potentialDiscount = baseRate.add(timeCost).multiply(new BigDecimal("0.10"));
            discount = potentialDiscount.min(new BigDecimal("2.00"));
        }
        
        BigDecimal totalCost = baseRate.add(timeCost).subtract(discount);
        
        // Create reservation with start time and ACTIVE status
        LocalDateTime now = LocalDateTime.now();
        Reservation reservation = Reservation.builder()
                .bike(bike)
                .user(user)
                .startStation(station)
                .reservationTime(now)
                .startTime(now)
                .durationMinutes(request.getDurationMinutes())
                .baseRate(baseRate)
                .timeCost(timeCost)
                .discount(discount)
                .totalCost(totalCost)
                .status(ReservationStatus.ACTIVE)  // Set to ACTIVE by default
                .build();
        
        // Update bike status to IN_USE since the reservation is immediately active
        bike.setStatus(BikeStatus.IN_USE);
        
        // Decrease the bike count from the station when the bike is reserved
        if (bike.getType() == BikeType.STANDARD) {
            station.setAvailableStandardBikes(station.getAvailableStandardBikes() - 1);
        } else {
            station.setAvailableElectricBikes(station.getAvailableElectricBikes() - 1);
        }
        
        // Persist changes
        bikeRepository.persist(bike);
        stationRepository.persist(station);
        reservationRepository.persist(reservation);
        
        return mapToDTO(reservation);
    }
    
    /**
     * Get user's active reservations
     */
    public List<ReservationDTO> getActiveReservations(User user) {
        return reservationRepository.findActiveReservationsForUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's reservation history
     */
    public List<ReservationDTO> getReservationHistory(User user) {
        return reservationRepository.findReservationHistoryForUser(user).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Cancel a reservation
     */
    @Transactional
    public void cancelReservation(UUID reservationId, User user) {
        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new BadRequestException("Reservation not found"));
                
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to cancel this reservation");
        }
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BadRequestException("Cannot cancel a completed reservation");
        }
        
        // Update bike status back to available
        Bike bike = reservation.getBike();
        bike.setStatus(BikeStatus.AVAILABLE);
        bikeRepository.persist(bike);
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.persist(reservation);
    }
    
    /**
     * End a rental and return the bike to a station
     */
    @Transactional
    public ReservationDTO endRental(UUID reservationId, UUID returnStationId, User user) {
        // Find and validate the reservation
        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new BadRequestException("Reservation not found"));
                
        if (!reservation.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to end this rental");
        }
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BadRequestException("Reservation is not active");
        }
        
        // Find and validate the return station
        Station returnStation = stationRepository.findByIdOptional(returnStationId)
                .orElseThrow(() -> new BadRequestException("Return station not found"));
                
        // Check if station has available capacity
        int currentBikes = returnStation.getAvailableStandardBikes() + returnStation.getAvailableElectricBikes();
        if (currentBikes >= returnStation.getCapacity()) {
            throw new BadRequestException("Return station is at full capacity");
        }
        
        // Calculate final duration and costs
        LocalDateTime now = LocalDateTime.now();
        long actualDurationMinutes = java.time.Duration.between(reservation.getStartTime(), now).toMinutes();
        
        // Calculate distance between stations using Haversine formula
        double distanceKm = calculateDistance(
            reservation.getStartStation().getLatitude(),
            reservation.getStartStation().getLongitude(),
            returnStation.getLatitude(),
            returnStation.getLongitude()
        ) / 1000.0; // Convert meters to kilometers
        
        // Update bike status and location
        Bike bike = reservation.getBike();
        bike.setStatus(BikeStatus.AVAILABLE);
        
        // Get the start station to update its counts
        Station startStation = reservation.getStartStation();
        
        // Decrease bike count from start station if returning to a different station
        if (!startStation.getId().equals(returnStation.getId())) {
            if (bike.getType() == BikeType.STANDARD) {
                startStation.setAvailableStandardBikes(startStation.getAvailableStandardBikes() - 1);
            } else {
                startStation.setAvailableElectricBikes(startStation.getAvailableElectricBikes() - 1);
            }
            stationRepository.persist(startStation);
        }
        
        // Update bike's current station
        bike.setCurrentStation(returnStation);
        
        // Update return station bike counts
        if (bike.getType() == BikeType.STANDARD) {
            returnStation.setAvailableStandardBikes(returnStation.getAvailableStandardBikes() + 1);
        } else {
            returnStation.setAvailableElectricBikes(returnStation.getAvailableElectricBikes() + 1);
        }
        
        // Update reservation
        reservation.setEndTime(now);
        reservation.setEndStation(returnStation);
        reservation.setDistanceTraveled(distanceKm);
        reservation.setStatus(ReservationStatus.COMPLETED);
        
        // Recalculate costs based on actual duration
        boolean isPremium = user.getSubscriptionPlan() == SubscriptionPlan.PREMIUM;
        BigDecimal baseRate = isPremium ? PREMIUM_BASE_RATE : STANDARD_BASE_RATE;
        int freeMinutes = isPremium ? PREMIUM_FREE_MINUTES : STANDARD_FREE_MINUTES;
        BigDecimal minuteRate = isPremium ? PREMIUM_MINUTE_RATE : STANDARD_MINUTE_RATE;
        
        // Calculate billable minutes (after free minutes)
        int billableMinutes = Math.max(0, (int) actualDurationMinutes - freeMinutes);
        BigDecimal timeCost = minuteRate.multiply(BigDecimal.valueOf(billableMinutes));
        
        // Calculate discount (for premium users)
        BigDecimal discount = BigDecimal.ZERO;
        if (isPremium) {
            // Apply 10% discount on total cost (up to $2)
            BigDecimal potentialDiscount = baseRate.add(timeCost).multiply(new BigDecimal("0.10"));
            discount = potentialDiscount.min(new BigDecimal("2.00"));
        }
        
        BigDecimal totalCost = baseRate.add(timeCost).subtract(discount);
        
        // Update reservation costs
        reservation.setDurationMinutes((int) actualDurationMinutes);
        reservation.setBaseRate(baseRate);
        reservation.setTimeCost(timeCost);
        reservation.setDiscount(discount);
        reservation.setTotalCost(totalCost);
        
        // Persist changes
        bikeRepository.persist(bike);
        stationRepository.persist(returnStation);
        reservationRepository.persist(reservation);
        
        return mapToDTO(reservation);
    }
    
    /**
     * Map Reservation entity to DTO
     */
    private ReservationDTO mapToDTO(Reservation reservation) {
        return ReservationDTO.builder()
                .id(reservation.getId())
                .bike(bikeService.mapToDTO(reservation.getBike()))
                .startStation(stationService.mapToDTOWithoutBikes(reservation.getStartStation()))
                .endStation(reservation.getEndStation() != null ? 
                          stationService.mapToDTOWithoutBikes(reservation.getEndStation()) : null)
                .reservationTime(reservation.getReservationTime())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .durationMinutes(reservation.getDurationMinutes())
                .distanceTraveled(reservation.getDistanceTraveled())
                .baseRate(reservation.getBaseRate())
                .timeCost(reservation.getTimeCost())
                .discount(reservation.getDiscount())
                .totalCost(reservation.getTotalCost())
                .status(reservation.getStatus())
                .build();
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
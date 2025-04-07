package org.metrowheel.reservation.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.reservation.model.Reservation;
import org.metrowheel.reservation.model.ReservationStatus;
import org.metrowheel.station.model.Station;
import org.metrowheel.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repository for Reservation entity
 */
@ApplicationScoped
public class ReservationRepository implements PanacheRepositoryBase<Reservation, UUID> {

    /**
     * Find active reservations for a user
     * 
     * @param user The user to check for active reservations
     * @return List of active reservations
     */
    public List<Reservation> findActiveReservationsForUser(User user) {
        return list("user = ?1 and (status = ?2 or status = ?3)", 
                user, ReservationStatus.SCHEDULED, ReservationStatus.ACTIVE);
    }

    /**
     * Find active reservation for a bike
     * 
     * @param bike The bike to check for active reservations
     * @return The active reservation or null if none found
     */
    public Reservation findActiveReservationForBike(Bike bike) {
        return find("bike = ?1 and (status = ?2 or status = ?3)", 
                bike, ReservationStatus.SCHEDULED, ReservationStatus.ACTIVE).firstResult();
    }

    /**
     * Find reservations for a specific station
     * 
     * @param station The station to check for reservations
     * @return List of reservations at the specified station
     */
    public List<Reservation> findReservationsForStation(Station station) {
        return list("startStation = ?1", station);
    }

    /**
     * Find scheduled reservations set to start within a time range
     * 
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return List of scheduled reservations
     */
    public List<Reservation> findUpcomingReservations(LocalDateTime startTime, LocalDateTime endTime) {
        return list("status = ?1 and scheduledStartTime >= ?2 and scheduledStartTime <= ?3", 
                ReservationStatus.SCHEDULED, startTime, endTime);
    }

    /**
     * Find reservation history for a user
     * 
     * @param user The user to check history for
     * @return List of completed or cancelled reservations
     */
    public List<Reservation> findReservationHistoryForUser(User user) {
        return list("user = ?1 and (status = ?2 or status = ?3) ORDER BY reservationTime DESC", 
                user, ReservationStatus.COMPLETED, ReservationStatus.CANCELLED);
    }

    /**
     * Cancel all scheduled reservations for a specific bike
     * 
     * @param bike The bike to cancel reservations for
     * @return Number of reservations cancelled
     */
    public long cancelReservationsForBike(Bike bike) {
        return update("status = ?1 where bike = ?2 and status = ?3", 
                ReservationStatus.CANCELLED, bike, ReservationStatus.SCHEDULED);
    }
}

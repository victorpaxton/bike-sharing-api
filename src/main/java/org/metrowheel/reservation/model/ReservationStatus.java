package org.metrowheel.reservation.model;

/**
 * Enumeration for different statuses of a reservation.
 */
public enum ReservationStatus {
    ACTIVE,     // When the bike is currently in use
    COMPLETED,  // When the reservation has been fulfilled
    CANCELLED   // When the reservation has been cancelled
}

package org.metrowheel.bike.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.metrowheel.user.model.UserDTO;
import org.metrowheel.reservation.model.ReservationBasicDTO;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for transferring bike review data in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeReviewDTO {
    private UUID id;
    private UUID bikeId;
    private UserDTO user;
    private ReservationBasicDTO reservation;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}

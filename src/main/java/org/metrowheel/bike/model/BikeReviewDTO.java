package org.metrowheel.bike.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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
    private String bikeNumber;
    private UUID userId;
    private String userName;
    private Integer rating;
    private String reviewText;
    private LocalDateTime reviewDate;
    private Boolean isVerifiedRide;
}

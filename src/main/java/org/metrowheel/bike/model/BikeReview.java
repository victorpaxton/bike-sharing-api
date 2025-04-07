package org.metrowheel.bike.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.metrowheel.common.model.BaseEntity;
import org.metrowheel.user.model.User;

import java.time.LocalDateTime;

/**
 * Entity representing a review and rating for a bike.
 */
@Entity
@Table(name = "bike_reviews")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BikeReview extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private BikeHistory trip;
    
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5 star rating
    
    @Column(name = "review_text", length = 1000)
    private String reviewText; // Optional review text
    
    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;
    
    @Column(name = "is_verified_ride", nullable = false)
    @Builder.Default
    private Boolean isVerifiedRide = true;
}

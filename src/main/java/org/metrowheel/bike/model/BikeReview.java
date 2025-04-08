package org.metrowheel.bike.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.metrowheel.common.model.BaseEntity;
import org.metrowheel.reservation.model.Reservation;
import org.metrowheel.user.model.User;

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
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;
    
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating; // 1-5 star rating
    
    @Column(length = 1000)
    private String comment; // Optional review text

}

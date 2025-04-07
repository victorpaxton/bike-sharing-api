package org.metrowheel.bike.controller;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.bike.model.BikeReviewDTO;
import org.metrowheel.bike.service.BikeReviewService;
import org.metrowheel.common.model.ApiResponse;

import java.util.List;
import java.util.UUID;

/**
 * Controller for bike review operations.
 */
@Path("/api/bikes/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bike Reviews", description = "Operations for bike reviews and ratings")
public class BikeReviewController {

    @Inject
    BikeReviewService reviewService;
    
    /**
     * Create a new review for a bike
     */
    @POST
    @Path("/trip/{tripId}")
    @Operation(
            summary = "Add a review for a bike",
            description = "Create a new review and rating for a bike after completing a trip"
    )
    public Response createReview(
            @PathParam("tripId") String tripId,
            @Valid CreateReviewRequest request) {
        try {
            UUID tripUuid = UUID.fromString(tripId);
            BikeReviewDTO review = reviewService.createReview(
                    request.getUserId(), 
                    tripUuid,
                    request.getRating(),
                    request.getReviewText()
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Review submitted successfully", review))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Invalid ID format: " + e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error(e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get reviews for a specific bike
     */
    @GET
    @Path("/bike/{bikeId}")
    @Operation(
            summary = "Get bike reviews",
            description = "Retrieve all reviews for a specific bike"
    )
    public Response getBikeReviews(@PathParam("bikeId") String bikeId) {
        try {
            UUID bikeUuid = UUID.fromString(bikeId);
            List<BikeReviewDTO> reviews = reviewService.getReviewsForBike(bikeUuid);
            
            return Response.ok(ApiResponse.success(reviews)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Invalid bike ID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error retrieving reviews: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get reviews created by a user
     */
    @GET
    @Path("/user/{userId}")
    @Operation(
            summary = "Get user's reviews",
            description = "Retrieve all reviews created by a specific user"
    )
    public Response getUserReviews(@PathParam("userId") String userId) {
        try {
            UUID userUuid = UUID.fromString(userId);
            List<BikeReviewDTO> reviews = reviewService.getReviewsByUser(userUuid);
            
            return Response.ok(ApiResponse.success(reviews)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Invalid user ID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error retrieving reviews: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Request model for creating a review
     */
    public static class CreateReviewRequest {
        @NotNull(message = "User ID is required")
        private UUID userId;
        
        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be between 1 and 5")
        @Max(value = 5, message = "Rating must be between 1 and 5")
        private Integer rating;
        
        private String reviewText;
        
        public UUID getUserId() {
            return userId;
        }
        
        public void setUserId(UUID userId) {
            this.userId = userId;
        }
        
        public Integer getRating() {
            return rating;
        }
        
        public void setRating(Integer rating) {
            this.rating = rating;
        }
        
        public String getReviewText() {
            return reviewText;
        }
        
        public void setReviewText(String reviewText) {
            this.reviewText = reviewText;
        }
    }
}

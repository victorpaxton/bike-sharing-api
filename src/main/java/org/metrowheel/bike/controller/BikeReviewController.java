package org.metrowheel.bike.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.metrowheel.bike.model.BikeReviewDTO;
import org.metrowheel.bike.model.BikeReviewRequest;
import org.metrowheel.bike.service.BikeReviewService;
import org.metrowheel.common.model.ApiResponse;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Controller for bike review operations.
 */
@Path("/api/v1/reviews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class BikeReviewController {

    @Inject
    BikeReviewService bikeReviewService;
    
    @Inject
    UserService userService;

    @POST
    @PermitAll
    @SecurityRequirement(name = "jwt")
    public ApiResponse<BikeReviewDTO> createReview(@Valid BikeReviewRequest request) {
        User user = userService.getCurrentUser();
        BikeReviewDTO review = bikeReviewService.createReview(request, user);
        return ApiResponse.success(review);
    }

    @GET
    @Path("/bike/{bikeId}")
    @PermitAll
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<BikeReviewDTO>> getBikeReviews(@PathParam("bikeId") UUID bikeId) {
        List<BikeReviewDTO> reviews = bikeReviewService.getBikeReviews(bikeId);
        return ApiResponse.success(reviews);
    }

    @GET
    @Path("/bike/{bikeId}/rating")
    @PermitAll
    @SecurityRequirement(name = "jwt")
    public ApiResponse<Double> getBikeAverageRating(@PathParam("bikeId") UUID bikeId) {
        double rating = bikeReviewService.getBikeAverageRating(bikeId);
        return ApiResponse.success(rating);
    }
}

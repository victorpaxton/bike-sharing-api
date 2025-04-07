package org.metrowheel.user.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.bike.model.RideHistoryDTO;
import org.metrowheel.bike.service.BikeHistoryService;
import org.metrowheel.common.model.ApiResponse;
import org.metrowheel.user.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * Controller for user operations.
 */
@Path("/api/users")
@Tag(name = "Users", description = "User operations")
public class UserController {

    @Inject
    UserService userService;
    
    @Inject
    BikeHistoryService bikeHistoryService;
    
    /**
     * Get ride history for a user.
     *
     * @param userId The user ID
     * @return Response with ride history data
     */
    @GET
    @Path("/{userId}/rides")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get user ride history",
            description = "Returns the ride history for a specific user"
    )
    public Response getUserRideHistory(@PathParam("userId") String userId) {
        try {
            UUID id = UUID.fromString(userId);
            List<RideHistoryDTO> rideHistory = bikeHistoryService.getUserRideHistory(id);
            return Response.ok(ApiResponse.success(rideHistory)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Invalid user ID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error retrieving ride history: " + e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get details for a specific ride.
     *
     * @param userId The user ID
     * @param rideId The ride ID
     * @return Response with ride details
     */
    @GET
    @Path("/{userId}/rides/{rideId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Get ride details",
            description = "Returns the details for a specific ride"
    )
    public Response getRideDetails(
            @PathParam("userId") String userId,
            @PathParam("rideId") String rideId
    ) {
        try {
            // Verify that the user ID is valid
            UUID userUuid = UUID.fromString(userId);
            UUID rideUuid = UUID.fromString(rideId);
            
            // Verify the user exists
            if (!userService.userExists(userUuid)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("User not found"))
                        .build();
            }
            
            // Get ride details
            RideHistoryDTO rideDetails = bikeHistoryService.getRideDetails(rideUuid);
            if (rideDetails == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Ride not found"))
                        .build();
            }
            
            return Response.ok(ApiResponse.success(rideDetails)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ApiResponse.error("Invalid ID format"))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Error retrieving ride details: " + e.getMessage()))
                    .build();
        }
    }
}

package org.metrowheel.reservation.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.metrowheel.common.model.ApiResponse;
import org.metrowheel.reservation.model.EndRentalRequest;
import org.metrowheel.reservation.model.ReservationDTO;
import org.metrowheel.reservation.model.ReservationRequest;
import org.metrowheel.reservation.service.ReservationService;
import org.metrowheel.user.model.User;
import org.metrowheel.user.service.UserService;

import java.util.List;
import java.util.UUID;

/**
 * REST resource for bike reservations
 */
@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class ReservationController {

    @Inject
    ReservationService reservationService;
    
    @Inject
    UserService userService;

    @POST
    @RolesAllowed("USER")
    @Operation(
            summary = "Create a new reservation",
            description = "Create a new bike reservation. Requires user or premium role."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<ReservationDTO> createReservation(@Valid ReservationRequest request) {
        User currentUser = userService.getCurrentUser();
        ReservationDTO reservation = reservationService.createReservation(request, currentUser);
        return ApiResponse.success("Reservation created successfully", reservation);
    }

    @GET
    @Path("/active")
    @Operation(
            summary = "Get active reservations",
            description = "Get current user's active reservations"
    )
    @RolesAllowed("USER")
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<ReservationDTO>> getActiveReservations() {
        User currentUser = userService.getCurrentUser();
        List<ReservationDTO> reservations = reservationService.getActiveReservations(currentUser);
        return ApiResponse.success(reservations);
    }

    @GET
    @Path("/history")
    @Operation(
            summary = "Get reservation history",
            description = "Get current user's reservation history"
    )
    @RolesAllowed("USER")
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<ReservationDTO>> getReservationHistory() {
        User currentUser = userService.getCurrentUser();
        List<ReservationDTO> reservations = reservationService.getReservationHistory(currentUser);
        return ApiResponse.success(reservations);
    }

    @DELETE
    @Path("/{id}")
    @Operation(
            summary = "Cancel reservation",
            description = "Cancel a scheduled reservation"
    )
    @RolesAllowed("USER")
    @SecurityRequirement(name = "jwt")
    public ApiResponse<Void> cancelReservation(@PathParam("id") UUID id) {
        User currentUser = userService.getCurrentUser();
        reservationService.cancelReservation(id, currentUser);
        return ApiResponse.success("Reservation cancelled successfully", null);
    }

    @POST
    @Path("/{id}/end")
    @Operation(
            summary = "End rental",
            description = "End a bike rental and return the bike to a station"
    )
    @RolesAllowed("USER")
    @SecurityRequirement(name = "jwt")
    public ApiResponse<ReservationDTO> endRental(
            @PathParam("id") UUID id,
            @Valid EndRentalRequest request) {
        User currentUser = userService.getCurrentUser();
        ReservationDTO reservation = reservationService.endRental(id, request.getReturnStationId(), currentUser);
        return ApiResponse.success("Rental ended successfully", reservation);
    }
} 
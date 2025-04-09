package org.metrowheel.bike.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.bike.service.BikeService;
import org.metrowheel.common.model.ApiResponse;

import java.util.List;

/**
 * Controller for bike-related operations.
 */
@Path("/api/bikes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bikes", description = "Operations for bike management")
@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class BikeController {

    @Inject
    BikeService bikeService;

    @GET
    @Operation(
            summary = "Get all bikes",
            description = "Retrieve a list of all bikes in the system"
    )
    @RolesAllowed("ADMIN")
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<BikeDTO>> getAllBikes() {
        List<BikeDTO> bikes = bikeService.getAllBikes();
        return ApiResponse.success(bikes);
    }
} 
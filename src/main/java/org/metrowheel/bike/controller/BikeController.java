package org.metrowheel.bike.controller;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.bike.model.Bike;
import org.metrowheel.bike.service.BikeService;
import org.metrowheel.common.model.ApiResponse;

/**
 * Controller for bike-related operations.
 */
@Path("/api/bikes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Bikes", description = "Operations for bike management")
public class BikeController {

    @Inject
    BikeService bikeService;
} 
package org.metrowheel.station.controller;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.metrowheel.exception.ApiError;
import org.metrowheel.station.model.StationCreateRequest;
import org.metrowheel.station.model.StationDTO;
import org.metrowheel.station.model.StationSearchRequest;
import org.metrowheel.station.service.StationService;

import java.util.List;
import java.util.UUID;

/**
 * REST resource for bike stations
 */
@Path("/api/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StationController {

    @Inject
    StationService stationService;

    /**
     * Search for stations based on location and filters
     */
    @GET
    public Response searchStations(
            @QueryParam("latitude") Double latitude,
            @QueryParam("longitude") Double longitude,
            @QueryParam("radius") @jakarta.ws.rs.DefaultValue("500") Integer radius,
            @QueryParam("name") String nameFilter,
            @QueryParam("availableBikes") @jakarta.ws.rs.DefaultValue("false") Boolean onlyWithAvailableBikes,
            @QueryParam("availableDocks") @jakarta.ws.rs.DefaultValue("false") Boolean onlyWithAvailableDocks,
            @QueryParam("page") @jakarta.ws.rs.DefaultValue("0") Integer page,
            @QueryParam("size") @jakarta.ws.rs.DefaultValue("20") Integer size) {
        
        StationSearchRequest request = new StationSearchRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRadius(radius);
        request.setNameFilter(nameFilter);
        request.setOnlyWithAvailableBikes(onlyWithAvailableBikes);
        request.setOnlyWithAvailableDocks(onlyWithAvailableDocks);
        request.setPage(page);
        request.setSize(size);
        
        List<StationDTO> stations = stationService.searchStations(request);
        return Response.ok(stations).build();
    }

    /**
     * Get a single station by ID
     */
    @GET
    @Path("/{id}")
    public Response getStation(@PathParam("id") UUID id) {
        StationDTO station = stationService.getStationById(id);
        if (station == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("STATION_NOT_FOUND", "Station not found", Response.Status.NOT_FOUND.getStatusCode()))
                    .build();
        }
        return Response.ok(station).build();
    }

    /**
     * Create a new station (admin only)
     */
    @POST
    @RolesAllowed("ADMIN")
    public Response createStation(@Valid StationCreateRequest stationCreateRequest) {
        StationDTO created = stationService.createStation(stationCreateRequest);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    /**
     * Update an existing station (admin only)
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response updateStation(@PathParam("id") UUID id, @Valid StationDTO stationDTO) {
        StationDTO updated = stationService.updateStation(id, stationDTO);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("STATION_NOT_FOUND", "Station not found", Response.Status.NOT_FOUND.getStatusCode()))
                    .build();
        }
        return Response.ok(updated).build();
    }

    /**
     * Delete a station (admin only)
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response deleteStation(@PathParam("id") UUID id) {
        boolean deleted = stationService.deleteStation(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiError("STATION_NOT_FOUND", "Station not found", Response.Status.NOT_FOUND.getStatusCode()))
                    .build();
        }
        return Response.noContent().build();
    }
}

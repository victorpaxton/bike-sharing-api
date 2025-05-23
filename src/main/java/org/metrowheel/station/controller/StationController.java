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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.metrowheel.bike.model.AddBikeToStationRequest;
import org.metrowheel.bike.model.BikeDTO;
import org.metrowheel.bike.service.BikeService;
import org.metrowheel.station.model.StationCreateRequest;
import org.metrowheel.station.model.StationDTO;
import org.metrowheel.station.model.StationSearchRequest;
import org.metrowheel.station.service.StationService;
import org.metrowheel.common.model.ApiResponse;
import org.metrowheel.station.model.StationMapDTO;
import org.metrowheel.station.service.StationMapCacheService;

import java.util.List;
import java.util.UUID;

/**
 * REST resource for bike stations
 */
@Path("/api/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@SecurityScheme(
    securitySchemeName = "jwt",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
public class StationController {

    @Inject
    StationService stationService;

    @Inject
    BikeService bikeService;

    @Inject
    StationMapCacheService stationMapCacheService;

    @GET
    @PermitAll
    @Operation(
            summary = "Search for stations",
            description = "Search for stations based on various criteria including location, name, and availability"
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<StationDTO>> searchStations(
            @QueryParam("latitude") Double latitude,
            @QueryParam("longitude") Double longitude,
            @QueryParam("radius") @jakarta.ws.rs.DefaultValue("500") Integer radius,
            @QueryParam("name") String nameFilter,
//            @QueryParam("availableBikes") @jakarta.ws.rs.DefaultValue("false") Boolean onlyWithAvailableBikes,
//            @QueryParam("availableDocks") @jakarta.ws.rs.DefaultValue("false") Boolean onlyWithAvailableDocks,
            @QueryParam("page") @jakarta.ws.rs.DefaultValue("0") Integer page,
            @QueryParam("size") @jakarta.ws.rs.DefaultValue("20") Integer size) {
        
        StationSearchRequest request = new StationSearchRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRadius(radius);
        request.setNameFilter(nameFilter);
//        request.setOnlyWithAvailableBikes(onlyWithAvailableBikes);
//        request.setOnlyWithAvailableDocks(onlyWithAvailableDocks);
        request.setPage(page);
        request.setSize(size);
        
        List<StationDTO> stations = stationService.searchStations(request);
        return ApiResponse.success(stations);
    }

    @GET
    @Path("/{id}")
    @PermitAll
    @Operation(
            summary = "Get station by ID",
            description = "Retrieve detailed information about a specific station"
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<StationDTO> getStationById(@PathParam("id") UUID id) {
        StationDTO station = stationService.getStationById(id);
        if (station == null) {
            return ApiResponse.error("Station not found");
        }
        return ApiResponse.success(station);
    }

    @POST
    @RolesAllowed("ADMIN")
    @Operation(
            summary = "Create a new station",
            description = "Create a new bike station. Requires admin privileges."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<StationDTO> createStation(@Valid StationCreateRequest request) {
        StationDTO station = stationService.createStation(request);
        return ApiResponse.success("Station created successfully", station);
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(
            summary = "Update a station",
            description = "Update an existing station's information. Requires admin privileges."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<StationDTO> updateStation(@PathParam("id") UUID id, @Valid StationDTO stationDTO) {
        StationDTO updatedStation = stationService.updateStation(id, stationDTO);
        if (updatedStation == null) {
            return ApiResponse.error("Station not found");
        }
        return ApiResponse.success("Station updated successfully", updatedStation);
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    @Operation(
            summary = "Delete a station",
            description = "Delete a station. Requires admin privileges."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<Void> deleteStation(@PathParam("id") UUID id) {
        boolean deleted = stationService.deleteStation(id);
        if (!deleted) {
            return ApiResponse.error("Station not found");
        }
        return ApiResponse.success("Station deleted successfully", null);
    }

    @POST
    @Path("/{stationId}/bikes")
    @RolesAllowed("ADMIN")
    @Operation(
            summary = "Add bike to station",
            description = "Adds an existing bike to a specific station. Requires admin privileges."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<BikeDTO> addBikeToStation(
            @PathParam("stationId") String stationId,
            @Valid AddBikeToStationRequest request) {
        BikeDTO bike = bikeService.addBikeToStation(stationId, request);
        return ApiResponse.success("Bike added to station successfully", bike);
    }

    @GET
    @Path("/map")
    @PermitAll
    @Operation(
            summary = "Get all stations for map display",
            description = "Retrieve minimal information about all stations for displaying on a map"
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<StationMapDTO>> getAllStationsForMap() {
        List<StationMapDTO> stations = stationService.getAllStationsForMap();
        return ApiResponse.success(stations);
    }

    @GET
    @Path("/map/cache")
    @PermitAll
    @Operation(
            summary = "Get cached map data",
            description = "Retrieve cached station data for map display. Returns empty if no cached data exists."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<List<StationMapDTO>> getCachedMapData() {
        return stationMapCacheService.getCachedDataIfExists()
                .map(ApiResponse::success)
                .orElse(ApiResponse.success(List.of()));
    }

    @POST
    @Path("/map/cache/invalidate")
    @RolesAllowed("ADMIN")
    @Operation(
            summary = "Invalidate map cache",
            description = "Manually invalidate the station map cache. Requires admin privileges."
    )
    @SecurityRequirement(name = "jwt")
    public ApiResponse<Void> invalidateMapCache() {
        stationMapCacheService.invalidateCache();
        return ApiResponse.success(null);
    }
}

package org.metrowheel;

import java.util.List;
import java.util.ArrayList;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/bikes")
@Tag(name = "Bike Resource", description = "Bike management operations")
public class BikeResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Get all bikes",
        description = "Returns a list of all available bikes"
    )
    @APIResponse(
        responseCode = "200",
        description = "List of bikes",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                          schema = @Schema(implementation = Bike.class))
    )
    public List<Bike> getAllBikes() {
        // Sample data - in a real application, this would come from a database
        List<Bike> bikes = new ArrayList<>();
        bikes.add(new Bike(1L, "Mountain Bike", "Available"));
        bikes.add(new Bike(2L, "City Bike", "Available"));
        bikes.add(new Bike(3L, "Hybrid Bike", "In Use"));
        
        return bikes;
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Get bike by ID",
        description = "Returns a bike by its ID"
    )
    @APIResponse(
        responseCode = "200", 
        description = "Bike found",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                          schema = @Schema(implementation = Bike.class))
    )
    @APIResponse(
        responseCode = "404",
        description = "Bike not found"
    )
    public Bike getBikeById(@PathParam("id") Long id) {
        // In a real application, this would be a database lookup
        if (id == 1L) {
            return new Bike(1L, "Mountain Bike", "Available");
        } else if (id == 2L) {
            return new Bike(2L, "City Bike", "Available");
        } else if (id == 3L) {
            return new Bike(3L, "Hybrid Bike", "In Use");
        }
        
        return null; // In a real app, would throw a 404 exception
    }
}

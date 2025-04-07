package org.metrowheel.media.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import org.metrowheel.media.model.MediaUploadRequest;
import org.metrowheel.media.model.MediaUploadResponse;
import org.metrowheel.media.service.MediaService;

import java.io.IOException;
import java.util.Base64;

/**
 * REST resource for media management operations
 */
@Path("/api/media")
@Tag(name = "Media Resource", description = "API for media upload and management")
@Produces(MediaType.APPLICATION_JSON)
public class MediaResource {

    @Inject
    MediaService mediaService;
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Upload media",
        description = "Upload base64 encoded media (image, video, etc.) to Cloudinary and receive the media URL"
    )
    @APIResponse(
        responseCode = "200",
        description = "Media uploaded successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                          schema = @Schema(implementation = MediaUploadResponse.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input or no media data provided"
    )
    @APIResponse(
        responseCode = "500",
        description = "Failed to upload media"
    )
    public Response uploadMedia(@Valid MediaUploadRequest request) {
        try {
            // Validate the input
            if (request.getData() == null || request.getData().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(MediaUploadResponse.error("No media data provided"))
                        .build();
            }
            
            // Decode base64 data
            byte[] mediaData;
            try {
                mediaData = Base64.getDecoder().decode(request.getData());
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(MediaUploadResponse.error("Invalid base64 media data: " + e.getMessage()))
                        .build();
            }
            
            // Upload to Cloudinary
            String url = mediaService.uploadMedia(mediaData, request.getFolder(), request.getResourceType());
            
            // Extract the public ID from the URL
            String publicId = mediaService.getPublicIdFromUrl(url);
            
            // Return success response
            MediaUploadResponse response = MediaUploadResponse.success(
                url, 
                publicId, 
                request.getResourceType()
            );
            
            return Response.ok(response).build();
            
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(MediaUploadResponse.error("Failed to upload media: " + e.getMessage()))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{publicId}")
    @Operation(
        summary = "Delete media",
        description = "Delete media from Cloudinary using its public ID"
    )
    @APIResponse(
        responseCode = "200", 
        description = "Media deleted successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid public ID"
    )
    @APIResponse(
        responseCode = "500",
        description = "Failed to delete media"
    )
    public Response deleteMedia(
            @PathParam("publicId") String publicId,
            @QueryParam("resourceType") @DefaultValue("image") String resourceType) {
        
        if (publicId == null || publicId.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(MediaUploadResponse.error("No public ID provided"))
                    .build();
        }
        
        boolean deleted = mediaService.deleteMedia(publicId, resourceType);
        
        if (deleted) {
            return Response.ok(
                    new MediaUploadResponse(null, publicId, resourceType, "Media deleted successfully", true))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(MediaUploadResponse.error("Failed to delete media with public ID: " + publicId))
                    .build();
        }
    }
}

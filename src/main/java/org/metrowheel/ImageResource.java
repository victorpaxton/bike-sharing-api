package org.metrowheel;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.metrowheel.model.ImageUploadForm;
import org.metrowheel.model.ImageUploadResponse;
import org.metrowheel.service.ImageService;

import java.io.IOException;
import java.util.Base64;

@Path("/api/images")
@Tag(name = "Image Resource", description = "API for image upload and management")
public class ImageResource {

    @Inject
    ImageService imageService;
    
    @POST
    @Path("/upload")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Upload an image",
        description = "Upload a base64 encoded image to Cloudinary and receive the image URL"
    )
    @APIResponse(
        responseCode = "200",
        description = "Image uploaded successfully",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                          schema = @Schema(implementation = ImageUploadResponse.class))
    )
    @APIResponse(
        responseCode = "400",
        description = "Invalid input or no image data provided"
    )
    @APIResponse(
        responseCode = "500",
        description = "Failed to upload image"
    )
    public Response uploadImage(ImageUploadForm form) {
        try {
            // Validate the input
            if (form.getImageBase64() == null || form.getImageBase64().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ImageUploadResponse(null, "No image data provided"))
                        .build();
            }
            
            // Get the folder, defaulting to "bikes" if not specified
            String folder = form.getFolder() != null && !form.getFolder().isEmpty() 
                    ? form.getFolder() : "bikes";
            
            // Decode base64 image
            byte[] imageData = Base64.getDecoder().decode(form.getImageBase64());
            
            // Upload to Cloudinary
            String imageUrl = imageService.uploadImage(imageData, folder);
            
            // Return success response
            return Response.ok(new ImageUploadResponse(imageUrl, "Image uploaded successfully"))
                    .build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ImageUploadResponse(null, "Invalid base64 image data: " + e.getMessage()))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ImageUploadResponse(null, "Failed to upload image: " + e.getMessage()))
                    .build();
        }
    }
}

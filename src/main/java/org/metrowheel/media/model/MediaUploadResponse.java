package org.metrowheel.media.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Response model for media upload operations
 */
@Schema(name = "MediaUploadResponse", description = "Response returned after media upload")
public class MediaUploadResponse {
    
    @Schema(description = "URL of the uploaded media")
    private String url;
    
    @Schema(description = "Public ID of the uploaded media in Cloudinary")
    private String publicId;
    
    @Schema(description = "Type of media resource", example = "image")
    private String resourceType;
    
    @Schema(description = "Message describing the upload result")
    private String message;
    
    @Schema(description = "Indicates whether the operation was successful")
    private boolean success;
    
    // Default constructor
    public MediaUploadResponse() {
    }
    
    // Constructor with all fields
    public MediaUploadResponse(String url, String publicId, String resourceType, String message, boolean success) {
        this.url = url;
        this.publicId = publicId;
        this.resourceType = resourceType;
        this.message = message;
        this.success = success;
    }
    
    // Constructor for successful uploads
    public static MediaUploadResponse success(String url, String publicId, String resourceType) {
        return new MediaUploadResponse(
            url, 
            publicId, 
            resourceType,
            resourceType + " uploaded successfully",
            true
        );
    }
    
    // Constructor for failed uploads
    public static MediaUploadResponse error(String message) {
        return new MediaUploadResponse(
            null,
            null,
            null,
            message,
            false
        );
    }
    
    // Getters and setters
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getPublicId() {
        return publicId;
    }
    
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
}

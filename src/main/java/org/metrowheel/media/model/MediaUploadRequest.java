package org.metrowheel.media.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * Request model for uploading media files
 */
@Schema(name = "MediaUploadRequest", description = "Request for uploading media content")
public class MediaUploadRequest {
    
    @Schema(description = "Base64 encoded media data", required = true)
    private String data;
    
    @Schema(description = "Folder to store the media in", example = "bikes", defaultValue = "general")
    private String folder = "general";
    
    @Schema(description = "Type of media resource", example = "image", defaultValue = "image", 
            enumeration = {"image", "video", "raw", "auto"})
    private String resourceType = "image";
    
    // Default constructor
    public MediaUploadRequest() {
    }
    
    // Constructor with all fields
    public MediaUploadRequest(String data, String folder, String resourceType) {
        this.data = data;
        if (folder != null && !folder.isEmpty()) {
            this.folder = folder;
        }
        if (resourceType != null && !resourceType.isEmpty()) {
            this.resourceType = resourceType;
        }
    }
    
    // Getters and setters
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public String getFolder() {
        return folder;
    }
    
    public void setFolder(String folder) {
        if (folder != null && !folder.isEmpty()) {
            this.folder = folder;
        }
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    public void setResourceType(String resourceType) {
        if (resourceType != null && !resourceType.isEmpty()) {
            this.resourceType = resourceType;
        }
    }
}

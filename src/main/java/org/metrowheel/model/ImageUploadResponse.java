package org.metrowheel.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ImageUploadResponse", description = "Response returned after image upload")
public class ImageUploadResponse {
    
    @Schema(description = "URL of the uploaded image")
    private String imageUrl;
    
    @Schema(description = "Message describing the upload result")
    private String message;
    
    public ImageUploadResponse() {
    }
    
    public ImageUploadResponse(String imageUrl, String message) {
        this.imageUrl = imageUrl;
        this.message = message;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}

package org.metrowheel.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ImageUploadForm", description = "Form for uploading an image")
public class ImageUploadForm {
    
    @Schema(description = "Base64 encoded image data")
    private String imageBase64;
    
    @Schema(description = "Folder to store the image in", defaultValue = "bikes")
    private String folder = "bikes";
    
    public String getImageBase64() {
        return imageBase64;
    }
    
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
    
    public String getFolder() {
        return folder;
    }
    
    public void setFolder(String folder) {
        this.folder = folder;
    }
}

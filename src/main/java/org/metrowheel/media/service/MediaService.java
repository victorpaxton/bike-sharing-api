package org.metrowheel.media.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Service responsible for handling media operations including
 * upload, retrieval, and management of different types of media files
 */
@ApplicationScoped
public class MediaService {
    
    private static final Logger LOGGER = Logger.getLogger(MediaService.class);
    
    @Inject
    Cloudinary cloudinary;
    
    /**
     * Uploads media data to Cloudinary
     * 
     * @param mediaData The media data as a byte array
     * @param folder The folder to store the media in (e.g., "bikes", "users", "profiles")
     * @param resourceType The type of resource ("image", "video", "raw", etc)
     * @return The Cloudinary URL of the uploaded media
     * @throws IOException If there's an error during upload
     */
    public String uploadMedia(byte[] mediaData, String folder, String resourceType) throws IOException {
        LOGGER.info("Uploading " + resourceType + " to Cloudinary folder: " + folder);
        
        // Create a temporary file
        File tempFile = File.createTempFile("upload_", ".tmp");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(mediaData);
        }
        
        try {
            // Generate a unique public ID for the media
            String publicId = folder + "/" + UUID.randomUUID().toString();
            
            // Upload to cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, 
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "resource_type", resourceType,
                    "overwrite", true
                ));
            
            // Get the secure URL from the response
            String secureUrl = (String) uploadResult.get("secure_url");
            LOGGER.info(resourceType + " uploaded successfully to: " + secureUrl);
            
            return secureUrl;
        } finally {
            // Delete the temporary file
            if (!tempFile.delete()) {
                LOGGER.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }
    
    /**
     * Overloaded method for uploading images, using "image" as the default resource type
     */
    public String uploadImage(byte[] imageData, String folder) throws IOException {
        return uploadMedia(imageData, folder, "image");
    }
    
    /**
     * Deletes a media file from Cloudinary
     * 
     * @param publicId The public ID of the media to delete
     * @param resourceType The type of resource ("image", "video", "raw", etc)
     * @return True if the deletion was successful
     */
    public boolean deleteMedia(String publicId, String resourceType) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().destroy(
                publicId, 
                ObjectUtils.asMap("resource_type", resourceType)
            );
            
            String status = (String) result.get("result");
            boolean success = "ok".equals(status);
            
            if (success) {
                LOGGER.info(resourceType + " with public ID " + publicId + " deleted successfully");
            } else {
                LOGGER.warn("Failed to delete " + resourceType + " with public ID " + publicId);
            }
            
            return success;
        } catch (IOException e) {
            LOGGER.error("Error deleting " + resourceType + " with public ID " + publicId, e);
            return false;
        }
    }
    
    /**
     * Overloaded method for deleting images, using "image" as the default resource type
     */
    public boolean deleteImage(String publicId) {
        return deleteMedia(publicId, "image");
    }
    
    /**
     * Extracts the public ID from a Cloudinary URL
     * 
     * @param cloudinaryUrl The full Cloudinary URL
     * @return The public ID extracted from the URL
     */
    public String getPublicIdFromUrl(String cloudinaryUrl) {
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            return null;
        }
        
        // Example URL: https://res.cloudinary.com/dwajaledd/image/upload/v1234567890/folder/filename.jpg
        // We need to extract: folder/filename
        
        try {
            // Remove the domain and initial path parts
            String[] parts = cloudinaryUrl.split("/upload/");
            if (parts.length < 2) {
                return null;
            }
            
            // Remove version number if present and file extension
            String path = parts[1];
            if (path.startsWith("v")) {
                path = path.substring(path.indexOf('/') + 1);
            }
            
            // Remove file extension
            int extensionIndex = path.lastIndexOf('.');
            if (extensionIndex > 0) {
                path = path.substring(0, extensionIndex);
            }
            
            return path;
        } catch (Exception e) {
            LOGGER.error("Error extracting public ID from URL: " + cloudinaryUrl, e);
            return null;
        }
    }
}

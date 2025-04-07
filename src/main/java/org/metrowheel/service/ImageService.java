package org.metrowheel.service;

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

@ApplicationScoped
public class ImageService {
    
    private static final Logger LOGGER = Logger.getLogger(ImageService.class);
    
    @Inject
    Cloudinary cloudinary;
    
    /**
     * Uploads an image to Cloudinary
     * 
     * @param imageData The image data as a byte array
     * @param folder The folder to store the image in (e.g., "bikes", "users")
     * @return The Cloudinary URL of the uploaded image
     * @throws IOException If there's an error during upload
     */
    public String uploadImage(byte[] imageData, String folder) throws IOException {
        LOGGER.info("Uploading image to Cloudinary folder: " + folder);
        
        // Create a temporary file
        File tempFile = File.createTempFile("upload_", ".tmp");
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(imageData);
        }
        
        try {
            // Generate a unique public ID for the image
            String publicId = folder + "/" + UUID.randomUUID().toString();
            
            // Upload to cloudinary
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(tempFile, 
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", folder,
                    "overwrite", true
                ));
            
            // Get the secure URL from the response
            String secureUrl = (String) uploadResult.get("secure_url");
            LOGGER.info("Image uploaded successfully to: " + secureUrl);
            
            return secureUrl;
        } finally {
            // Delete the temporary file
            if (!tempFile.delete()) {
                LOGGER.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }
}

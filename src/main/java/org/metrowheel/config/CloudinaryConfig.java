package org.metrowheel.config;

import com.cloudinary.Cloudinary;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class CloudinaryConfig {
    
    private final String CLOUD_NAME = "dwajaledd";
    private final String API_KEY = "512412195345133";
    private final String API_SECRET = "JxHt_mqXTqGftclSq7LUE_5o_BQ";
    
    @Produces
    @ApplicationScoped
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        config.put("secure", "true");
        
        return new Cloudinary(config);
    }
}

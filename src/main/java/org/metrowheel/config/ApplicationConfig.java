package org.metrowheel.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Centralized configuration for application-wide settings.
 * Uses MicroProfile Config for property injection.
 */
@ApplicationScoped
public class ApplicationConfig {
    
    @ConfigProperty(name = "quarkus.application.name", defaultValue = "bike-sharing-api")
    String applicationName;
    
    @ConfigProperty(name = "quarkus.application.version", defaultValue = "1.0.0")
    String applicationVersion;
    
    @ConfigProperty(name = "app.default.page-size", defaultValue = "20")
    int defaultPageSize;
    
    @ConfigProperty(name = "app.default.max-page-size", defaultValue = "100") 
    int maxPageSize;
    
    public String getApplicationName() {
        return applicationName;
    }
    
    public String getApplicationVersion() {
        return applicationVersion;
    }
    
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    
    public int getMaxPageSize() {
        return maxPageSize;
    }
}

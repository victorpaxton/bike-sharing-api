package org.metrowheel.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends WebApplicationException {
    
    public ResourceNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resourceType, String identifier) {
        super(resourceType + " with identifier " + identifier + " not found", 
              Response.Status.NOT_FOUND);
    }
}

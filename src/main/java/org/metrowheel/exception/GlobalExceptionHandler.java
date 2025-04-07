package org.metrowheel.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global exception handler that provides consistent error responses
 * for all uncaught exceptions in the application.
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Override
    public Response toResponse(Exception exception) {
        logger.error("Unhandled exception", exception);
        
        ApiError error = new ApiError(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()
        );
        
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
    }
}

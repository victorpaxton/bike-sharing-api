package org.metrowheel.exception;

import java.time.LocalDateTime;

/**
 * Standardized error response for all API errors.
 */
public class ApiError {
    
    private final String code;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;
    
    public ApiError(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

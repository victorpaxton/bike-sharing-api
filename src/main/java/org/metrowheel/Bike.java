package org.metrowheel;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "Bike", description = "Represents a bike in the sharing system")
public class Bike {
    
    @Schema(required = true, description = "Unique identifier of the bike")
    private Long id;
    
    @Schema(required = true, description = "Type of the bike (Mountain, City, etc.)")
    private String type;
    
    @Schema(required = true, description = "Current status of the bike (Available, In Use, Maintenance)")
    private String status;
    
    // Default constructor required for JSON serialization/deserialization
    public Bike() {
    }
    
    public Bike(Long id, String type, String status) {
        this.id = id;
        this.type = type;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

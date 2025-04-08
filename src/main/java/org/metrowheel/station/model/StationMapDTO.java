package org.metrowheel.station.model;

import java.util.UUID;

/**
 * Data Transfer Object containing minimal station information for map display
 */
public class StationMapDTO {
    private UUID id;
    private String name;
    private String address;
    private String imageUrl;
    private double latitude;
    private double longitude;
    private int availableStandardBikes;
    private int availableElectricBikes;
    private int capacity;
    private boolean status;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getAvailableStandardBikes() {
        return availableStandardBikes;
    }

    public void setAvailableStandardBikes(int availableStandardBikes) {
        this.availableStandardBikes = availableStandardBikes;
    }

    public int getAvailableElectricBikes() {
        return availableElectricBikes;
    }

    public void setAvailableElectricBikes(int availableElectricBikes) {
        this.availableElectricBikes = availableElectricBikes;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
} 
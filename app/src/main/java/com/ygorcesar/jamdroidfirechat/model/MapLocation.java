package com.ygorcesar.jamdroidfirechat.model;

public class MapLocation {

    private String latitude;
    private String longitude;

    public MapLocation() {
    }

    public MapLocation(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}

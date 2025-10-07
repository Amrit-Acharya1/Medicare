package com.acharyaamrit.medicare.model;

public class UserLocationUpdateRequest {
    private String lat;
    private String longt;

    public UserLocationUpdateRequest() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongt() {
        return longt;
    }

    public void setLongt(String longt) {
        this.longt = longt;
    }

    public UserLocationUpdateRequest(String lat, String longt) {
        this.lat = lat;
        this.longt = longt;
    }
}

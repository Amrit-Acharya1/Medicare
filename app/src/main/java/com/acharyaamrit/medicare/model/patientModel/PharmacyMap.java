package com.acharyaamrit.medicare.model.patientModel;

public class PharmacyMap {
    private String id;
    private String user_id;
    private String pharmacy_name;
    private String contact;
    private String lat;
    private String longt;
    private String distance;
    private String self;

    public PharmacyMap(String id, String user_id, String pharmacy_name, String contact, String lat, String longt, String distance, String self) {
        this.id = id;
        this.user_id = user_id;
        this.pharmacy_name = pharmacy_name;
        this.contact = contact;
        this.lat = lat;
        this.longt = longt;
        this.distance = distance;
        this.self = self;
    }

    public PharmacyMap() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
}

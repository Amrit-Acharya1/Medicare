package com.acharyaamrit.medicare.model;

public class Clicnic {
    private int id;
    private int clicnic_id;
    private String user_type;
    private String name;
    private String email;
    private String contact;
    private String dob;
    private String address;

    private String lat;
    private String longt;

    public Clicnic() {
    }

    public Clicnic(int id, int clicnic_id, String user_type, String name, String email, String contact, String dob, String address, String lat, String longt) {
        this.id = id;
        this.clicnic_id = clicnic_id;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.dob = dob;
        this.address = address;
        this.lat = lat;
        this.longt = longt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClicnic_id() {
        return clicnic_id;
    }

    public void setClicnic_id(int clicnic_id) {
        this.clicnic_id = clicnic_id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}

package com.acharyaamrit.medicare.model;

public class Pharmacy {
    private int id;
    private int pharmacy_id;
    private String user_type;
    private String name;
    private String email;
    private String contact;
    private String dob;
    private String address;
    private String pan_no;
    private String lat;
    private String longt;
    private String clicnic;
    private String[] topic;

    public Pharmacy() {
    }


    public Pharmacy(int id, int pharmacy_id, String user_type, String name, String email, String contact, String dob, String address, String pan_no, String lat, String longt, String clicnic, String[] topic) {
        this.id = id;
        this.pharmacy_id = pharmacy_id;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.dob = dob;
        this.address = address;
        this.pan_no = pan_no;
        this.lat = lat;
        this.longt = longt;
        this.clicnic = clicnic;
    }

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(int pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
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

    public String getPan_no() {
        return pan_no;
    }

    public void setPan_no(String pan_no) {
        this.pan_no = pan_no;
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

    public String getClicnic() {
        return clicnic;
    }

    public void setClicnic(String clicnic) {
        this.clicnic = clicnic;
    }
}

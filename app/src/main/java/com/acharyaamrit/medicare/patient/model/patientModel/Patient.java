package com.acharyaamrit.medicare.patient.model.patientModel;

public class Patient {
    private int id;
    private int patient_id;
    private String user_type;
    private String name;
    private String email;
    private String contact;
    private String dob;
    private String address;
    private String blood_group;
    private String lat;
    private String longt;
    private String emergency_contact;
    private String gender;
    private String[] topic;

    public Patient(int id, int patient_id, String user_type, String name, String email, String contact, String dob, String address, String blood_group, String lat, String longt, String emergency_contact, String gender, String[] topic) {
        this.id = id;
        this.patient_id = patient_id;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.dob = dob;
        this.address = address;
        this.blood_group = blood_group;
        this.lat = lat;
        this.longt = longt;
        this.emergency_contact = emergency_contact;
        this.gender = gender;
        this.topic = topic;
    }
    public Patient(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
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

    public String getBlood_group() {
        return blood_group;
    }

    public void setBlood_group(String blood_group) {
        this.blood_group = blood_group;
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

    public String getEmergency_contact() {
        return emergency_contact;
    }

    public void setEmergency_contact(String emergency_contact) {
        this.emergency_contact = emergency_contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String[] getTopic() {
        return topic;
    }

    public void setTopic(String[] topic) {
        this.topic = topic;
    }
}

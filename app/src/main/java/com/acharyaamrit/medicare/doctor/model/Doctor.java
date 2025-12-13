package com.acharyaamrit.medicare.doctor.model;

public class Doctor {
    private int id;
    private int doctor_id;
    private String user_type;
    private String name;
    private String email;
    private String contact;
    private String dob;
    private String address;
    private  String speciality;
    private String clicnic;
    private String gender;
    private String[] topic;



    public Doctor(int id, int doctor_id, String user_type, String name, String email, String contact, String dob, String address, String speciality, String clicnic, String gender, String[] topic) {
        this.id = id;
        this.doctor_id = doctor_id;
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.dob = dob;
        this.address = address;
        this.speciality = speciality;
        this.clicnic = clicnic;
        this.gender = gender;
        this.topic = topic;
    }

    public Doctor() {
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

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
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

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getClicnic() {
        return clicnic;
    }

    public void setClicnic(String clicnic) {
        this.clicnic = clicnic;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

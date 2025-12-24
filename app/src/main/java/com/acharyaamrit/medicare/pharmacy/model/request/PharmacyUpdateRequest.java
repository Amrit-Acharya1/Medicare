package com.acharyaamrit.medicare.pharmacy.model.request;

public class PharmacyUpdateRequest {
    private String address;
    private String contact;
    private String dob;
    private String pan_no;

    public PharmacyUpdateRequest() {
    }

    public PharmacyUpdateRequest(String address, String contact, String dob, String pan_no) {
        this.address = address;
        this.contact = contact;
        this.dob = dob;
        this.pan_no = pan_no;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getPan_no() {
        return pan_no;
    }

    public void setPan_no(String pan_no) {
        this.pan_no = pan_no;
    }
}

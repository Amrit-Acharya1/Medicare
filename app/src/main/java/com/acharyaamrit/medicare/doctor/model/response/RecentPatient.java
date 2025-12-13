package com.acharyaamrit.medicare.doctor.model.response;

public class RecentPatient {

    private String relation_id;
    private String patient_id;
    private String name;
    private String email;
    private String phone;
    private String created_at;

    public RecentPatient() {
    }

    public RecentPatient(String relation_id, String patient_id, String name, String email, String phone, String created_at) {
        this.relation_id = relation_id;
        this.patient_id = patient_id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.created_at=created_at;
    }

    public String getRelation_id() {
        return relation_id;
    }

    public void setRelation_id(String relation_id) {
        this.relation_id = relation_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

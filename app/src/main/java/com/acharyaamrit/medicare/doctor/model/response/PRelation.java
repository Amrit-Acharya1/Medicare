package com.acharyaamrit.medicare.doctor.model.response;

public class PRelation {
    private int id;
    private String doctor_name;
    private String patient_id;
    private String created_at;
    private String updated_at;

    public PRelation() {
    }

    public PRelation(int id, String doctor_name, String patient_id, String created_at, String updated_at) {
        this.id = id;
        this.doctor_name = doctor_name;
        this.patient_id = patient_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}

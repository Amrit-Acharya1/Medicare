package com.acharyaamrit.medicare.patient.model.patientModel;

public class PatientDocument {
    private int id;
    private String patient_id;
    private String doctor_name;
    private String document_type;
    private String document_url;
    private String created_at;

    public PatientDocument(int id, String patient_id, String doctor_name, String document_type, String document_url, String created_at) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_name = doctor_name;
        this.document_type = document_type;
        this.document_url = document_url;
        this.created_at = created_at;
    }

    public PatientDocument() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getDocument_url() {
        return document_url;
    }

    public void setDocument_url(String document_url) {
        this.document_url = document_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

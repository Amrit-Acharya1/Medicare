package com.acharyaamrit.medicare.patient.model.request;

import androidx.annotation.Nullable;

import java.io.File;

public class PatientDocumentRequest {
    private String document_type;
    private String patient_id;
    @Nullable
    private String doctor_id;
    private File document;

    public PatientDocumentRequest() {
    }

    public PatientDocumentRequest(String document_type, String patient_id, @Nullable String doctor_id, File document) {
        this.document_type = document_type;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.document = document;
    }

    public String getDocument_type() {
        return document_type;
    }

    public void setDocument_type(String document_type) {
        this.document_type = document_type;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public @Nullable String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(@Nullable String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public File getDocument() {
        return document;
    }

    public void setDocument(File document) {
        this.document = document;
    }

}

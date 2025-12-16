package com.acharyaamrit.medicare.doctor.model.request;

public class PRelationRequest {
    private int patient_id;

    public PRelationRequest() {
    }

    public PRelationRequest(int patient_id) {
        this.patient_id = patient_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }
}

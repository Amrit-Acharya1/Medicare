package com.acharyaamrit.medicare.doctor.model.request;

public class PrescriptionRelationRequest {
    private int doctor_id;
    private int patient_id;

    public PrescriptionRelationRequest() {
    }

    public PrescriptionRelationRequest(int doctor_id, int patient_id) {
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }
}

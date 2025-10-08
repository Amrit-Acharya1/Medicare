package com.acharyaamrit.medicare.model;

public class TimelineRequest {
    int patient_id;

    public TimelineRequest(int patient_id) {
        this.patient_id = patient_id;
    }

    public TimelineRequest() {
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }
}

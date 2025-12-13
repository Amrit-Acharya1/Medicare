package com.acharyaamrit.medicare.doctor.model.response;

import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchPatientResponse {

    @SerializedName("patitents")
    private List<Patient> patients;

    public SearchPatientResponse() {
    }

    public SearchPatientResponse(List<Patient> patients) {
        this.patients = patients;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}

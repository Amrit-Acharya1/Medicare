package com.acharyaamrit.medicare.pharmacy.model;

import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrescriptionPharmacy {
    private int id;
    @SerializedName("doctor_name")

    private String doctor_name;
    @SerializedName("patient_id")

    private String patient_id;
    @SerializedName("created_at")

    private String created_at;
    @SerializedName("updated_at")

    private String updated_at;
    @SerializedName("preciption")
    private List<Preciption> prescriptionList;


    public PrescriptionPharmacy(int id, String doctor_name, String patient_id, String created_at, String updated_at, List<Preciption> prescriptionList) {
        this.id = id;
        this.doctor_name = doctor_name;
        this.patient_id = patient_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.prescriptionList = prescriptionList;
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

    public List<Preciption> getPrescriptionList() {
        return prescriptionList;
    }

    public void setPrescriptionList(List<Preciption> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }
}

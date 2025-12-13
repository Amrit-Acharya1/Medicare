package com.acharyaamrit.medicare.patient.model.patientModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentPreciption {

    int id;
    String doctor_name;
    String patient_id;
    String created_at;
    @SerializedName("preciption")
    private List<Preciption> preciptionList;

    public CurrentPreciption(int id, String doctor_name, String patient_id, String created_at) {
        this.id = id;
        this.doctor_name = doctor_name;
        this.patient_id = patient_id;
        this.created_at = created_at;
    }

    public CurrentPreciption() {
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
    public List<Preciption> getPreciptionList() {
        return preciptionList;
    }

    public void setPreciptionList(List<Preciption> preciptionList) {
        this.preciptionList = preciptionList;
    }
}

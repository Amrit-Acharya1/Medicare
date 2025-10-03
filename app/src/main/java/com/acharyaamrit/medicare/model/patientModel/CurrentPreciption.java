package com.acharyaamrit.medicare.model.patientModel;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentPreciption {

    int id;
    String doctor_id;
    String patient_id;
    String created_at;
    @SerializedName("preciption")
    private List<Preciption> preciptionList;

    public CurrentPreciption(int id, String doctor_id, String patient_id, String created_at) {
        this.id = id;
        this.doctor_id = doctor_id;
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

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
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

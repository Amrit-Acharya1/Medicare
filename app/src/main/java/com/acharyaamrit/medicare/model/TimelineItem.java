package com.acharyaamrit.medicare.model;

import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TimelineItem {
    int id;
    String doctor_name;
    String pharmacy_name;
    String created_at;
    String updated_at;

    @SerializedName("preciption")
    List<Preciption> preciption;

    public TimelineItem() {
    }

    public TimelineItem(int id, String doctor_name, String pharmacy_name, String created_at, String updated_at, List<Preciption> preciption) {
        this.id = id;
        this.doctor_name = doctor_name;
        this.pharmacy_name = pharmacy_name;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.preciption = preciption;
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

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
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

    public List<Preciption> getPreciption() {
        return preciption;
    }

    public void setPreciption(List<Preciption> preciption) {
        this.preciption = preciption;
    }
}

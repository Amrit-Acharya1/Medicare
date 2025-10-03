package com.acharyaamrit.medicare.model.patientModel;

import com.google.gson.annotations.SerializedName;

public class Preciption {

    @SerializedName("id")
    int id;
    @SerializedName("preciption_relation_id")
    String preciption_relation_id;
    @SerializedName("medicine_id")
    String medicine_id;
    @SerializedName("frequency")
    String frequency;
    @SerializedName("duration")
    String duration;
    @SerializedName("duration_type")
    String duration_type;
    @SerializedName("qty")
    String qty;
    @SerializedName("note")
    String note;
    @SerializedName("created_at")
    String created_at;

    public Preciption() {
    }

    public Preciption(int id, String preciption_relation_id, String medicine_id, String frequency, String duration, String duration_type, String qty, String note, String created_at) {
        this.id = id;
        this.preciption_relation_id = preciption_relation_id;
        this.medicine_id = medicine_id;
        this.frequency = frequency;
        this.duration = duration;
        this.duration_type = duration_type;
        this.qty = qty;
        this.note = note;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPreciption_relation_id() {
        return preciption_relation_id;
    }

    public void setPreciption_relation_id(String preciption_relation_id) {
        this.preciption_relation_id = preciption_relation_id;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}

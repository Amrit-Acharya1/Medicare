package com.acharyaamrit.medicare.doctor.model.request;

public class PrescriptionRequest {
    int medicine_id;
    int preciption_relation_id;
    int duration;
    String duration_type;
    int qty;
    int frequency;
    String note;

    public PrescriptionRequest() {
    }

    public PrescriptionRequest(int medicine_id, int preciption_relation_id, int duration, String duration_type, int qty, int frequency, String note) {
        this.medicine_id = medicine_id;
        this.preciption_relation_id = preciption_relation_id;
        this.duration = duration;
        this.duration_type = duration_type;
        this.qty = qty;
        this.frequency = frequency;
        this.note = note;
    }

    public int getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(int medicine_id) {
        this.medicine_id = medicine_id;
    }

    public int getPreciption_relation_id() {
        return preciption_relation_id;
    }

    public void setPreciption_relation_id(int preciption_relation_id) {
        this.preciption_relation_id = preciption_relation_id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDuration_type() {
        return duration_type;
    }

    public void setDuration_type(String duration_type) {
        this.duration_type = duration_type;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

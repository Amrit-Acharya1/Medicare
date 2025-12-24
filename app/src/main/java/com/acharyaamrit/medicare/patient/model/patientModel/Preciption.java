package com.acharyaamrit.medicare.patient.model.patientModel;

import com.google.gson.annotations.SerializedName;

public class Preciption {

    @SerializedName("id")
    int id;
    @SerializedName("prescription_relation_id")
    int prescription_relation_id;
    @SerializedName("medicine_name")
    String medicine_name;
    @SerializedName("frequency")
    String frequency;
    @SerializedName("doasage_unit")
    String doasage_unit;
    @SerializedName("doasage_qty")
    String doasage_qty;
    @SerializedName("qty")
    String qty;
    @SerializedName("company_name")
    String company_name;
    @SerializedName("price")
    String price;

    @SerializedName("interval_days")
    String interval_days;

    @SerializedName("created_at")
    String created_at;

    public Preciption() {
    }

    public Preciption(int id, int prescription_relation_id, String medicine_name, String frequency, String doasage_unit, String doasage_qty, String qty, String company_name, String price,String interval_days ,String created_at) {
        this.id = id;
        this.prescription_relation_id = prescription_relation_id;
        this.medicine_name = medicine_name;
        this.frequency = frequency;
        this.doasage_unit = doasage_unit;
        this.doasage_qty = doasage_qty;
        this.qty = qty;
        this.company_name = company_name;
        this.price = price;
        this.interval_days = interval_days;
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrescription_relation_id() {
        return prescription_relation_id;
    }

    public void setPrescription_relation_id(int prescription_relation_id) {
        this.prescription_relation_id = prescription_relation_id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getDoasage_unit() {
        return doasage_unit;
    }

    public void setDoasage_unit(String doasage_unit) {
        this.doasage_unit = doasage_unit;
    }

    public String getDoasage_qty() {
        return doasage_qty;
    }

    public void setDoasage_qty(String doasage_qty) {
        this.doasage_qty = doasage_qty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInterval_days() {
        return interval_days;
    }

    public void setInterval_days(String interval_days) {
        this.interval_days = interval_days;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
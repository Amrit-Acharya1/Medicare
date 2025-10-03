package com.acharyaamrit.medicare.model.patientModel;

public class Medicine {
    private int id;
    private String medicine_name;


    private String doasage_unit;
    private String doasage_qty;

    private String company_name;
    private String frequency;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
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

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Medicine() {

    }

    public Medicine(int id, String medicine_name, String doasage_unit, String doasage_qty, String company_name, String frequency) {
        this.id = id;
        this.medicine_name = medicine_name;
        this.doasage_unit = doasage_unit;
        this.doasage_qty = doasage_qty;
        this.company_name = company_name;
        this.frequency = frequency;
    }
}

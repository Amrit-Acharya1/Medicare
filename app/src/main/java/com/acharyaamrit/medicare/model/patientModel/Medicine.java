package com.acharyaamrit.medicare.model.patientModel;

public class Medicine {
    private int id;
    private String medicine_name;
    private String note;
    private String qty;

    public Medicine() {
    }

    public Medicine(int id, String medicine_name, String note, String qty) {
        this.id = id;
        this.medicine_name = medicine_name;
        this.note = note;
        this.qty = qty;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}

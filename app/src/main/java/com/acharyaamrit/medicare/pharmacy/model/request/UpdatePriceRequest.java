package com.acharyaamrit.medicare.pharmacy.model.request;

import com.google.gson.annotations.SerializedName;

public class UpdatePriceRequest {
    @SerializedName("price")
    String price;
    @SerializedName("pharmacy_id")
    int pharmacy_id;
    @SerializedName("prescription_id")
    int prescription_id;

    public UpdatePriceRequest() {
    }

    public UpdatePriceRequest(String price, int pharmacy_id, int prescription_id) {
        this.price = price;
        this.pharmacy_id = pharmacy_id;
        this.prescription_id = prescription_id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(int pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    public int getPrescription_id() {
        return prescription_id;
    }

    public void setPrescription_id(int prescription_id) {
        this.prescription_id = prescription_id;
    }
}

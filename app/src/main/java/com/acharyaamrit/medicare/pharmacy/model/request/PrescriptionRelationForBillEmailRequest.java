package com.acharyaamrit.medicare.pharmacy.model.request;

import com.google.gson.annotations.SerializedName;

public class PrescriptionRelationForBillEmailRequest {
    @SerializedName("prescription_relation_id")
    String prescription_relation_id;

    public PrescriptionRelationForBillEmailRequest() {
    }

    public PrescriptionRelationForBillEmailRequest(String prescription_relation_id) {
        this.prescription_relation_id = prescription_relation_id;
    }

    public String getPrescription_relation_id() {
        return prescription_relation_id;
    }

    public void setPrescription_relation_id(String prescription_relation_id) {
        this.prescription_relation_id = prescription_relation_id;
    }
}

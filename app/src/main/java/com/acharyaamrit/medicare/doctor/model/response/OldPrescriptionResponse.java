package com.acharyaamrit.medicare.doctor.model.response;

import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OldPrescriptionResponse {

    @SerializedName("precriptions")
    private List<Preciption> oldPrescription;

    public OldPrescriptionResponse() {
    }

    public OldPrescriptionResponse(List<Preciption> oldPrescription) {
        this.oldPrescription = oldPrescription;
    }

    public List<Preciption> getOldPrescription() {
        return oldPrescription;
    }

    public void setOldPrescription(List<Preciption> oldPrescription) {
        this.oldPrescription = oldPrescription;
    }
}

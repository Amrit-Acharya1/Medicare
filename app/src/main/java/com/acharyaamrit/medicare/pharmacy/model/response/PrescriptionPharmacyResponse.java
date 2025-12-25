package com.acharyaamrit.medicare.pharmacy.model.response;

import com.acharyaamrit.medicare.doctor.model.response.PRelation;
import com.acharyaamrit.medicare.pharmacy.model.PrescriptionPharmacy;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrescriptionPharmacyResponse {
    @SerializedName("preciptions")
    private List<PrescriptionPharmacy> pRelationPharmacyList;

    public PrescriptionPharmacyResponse() {
    }

    public PrescriptionPharmacyResponse(List<PrescriptionPharmacy> pRelationPharmacyList) {
        this.pRelationPharmacyList = pRelationPharmacyList;
    }

    public List<PrescriptionPharmacy> getpRelationPharmacyList() {
        return pRelationPharmacyList;
    }

    public void setpRelationPharmacyList(List<PrescriptionPharmacy> pRelationPharmacyList) {
        this.pRelationPharmacyList = pRelationPharmacyList;
    }
}

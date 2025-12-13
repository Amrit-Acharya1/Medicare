package com.acharyaamrit.medicare.patient.model.response;

import com.acharyaamrit.medicare.patient.model.patientModel.PharmacyMap;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NearbyPharmacyResponse {
    @SerializedName("pharmacies")
    private List<PharmacyMap> pharmacy_map;

    public NearbyPharmacyResponse(List<PharmacyMap> pharmacy_map) {
        this.pharmacy_map = pharmacy_map;
    }

    public List<PharmacyMap> getPharmacy_map() {
        return pharmacy_map;
    }

    public void setPharmacy_map(List<PharmacyMap> pharmacy_map) {
        this.pharmacy_map = pharmacy_map;
    }

    public NearbyPharmacyResponse() {
    }
}

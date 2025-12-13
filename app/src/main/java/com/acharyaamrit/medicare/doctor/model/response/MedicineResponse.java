package com.acharyaamrit.medicare.doctor.model.response;

import com.acharyaamrit.medicare.doctor.model.Medicine;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MedicineResponse {

    @SerializedName("medicines")
    private List<Medicine> medicines;

    public MedicineResponse() {
    }

    public MedicineResponse(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }
}

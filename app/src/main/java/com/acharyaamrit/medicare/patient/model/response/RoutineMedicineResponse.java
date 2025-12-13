package com.acharyaamrit.medicare.patient.model.response;

import com.acharyaamrit.medicare.patient.model.patientModel.RoutineMedicine;
import com.google.gson.annotations.SerializedName;

public class RoutineMedicineResponse {

    @SerializedName("routine")
    private RoutineMedicine routineMedicine;

    public RoutineMedicineResponse(RoutineMedicine routineMedicine) {
        this.routineMedicine = routineMedicine;
    }

    public RoutineMedicineResponse() {
    }

    public RoutineMedicine getRoutineMedicine() {
        return routineMedicine;
    }

    public void setRoutineMedicine(RoutineMedicine routineMedicine) {
        this.routineMedicine = routineMedicine;
    }
}

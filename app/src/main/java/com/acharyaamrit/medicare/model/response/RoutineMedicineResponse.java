package com.acharyaamrit.medicare.model.response;

import com.acharyaamrit.medicare.model.patientModel.RoutineMedicine;
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

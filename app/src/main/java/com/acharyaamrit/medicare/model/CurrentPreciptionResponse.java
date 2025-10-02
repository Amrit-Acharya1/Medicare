package com.acharyaamrit.medicare.model;

import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrentPreciptionResponse {

    @SerializedName("preciption")
    private CurrentPreciption currentPreciption;



    public CurrentPreciptionResponse() {
    }

    public CurrentPreciptionResponse(CurrentPreciption currentPreciption) {
        this.currentPreciption = currentPreciption;

    }

    public CurrentPreciption getCurrentPreciption() {
        return currentPreciption;
    }

    public void setCurrentPreciption(CurrentPreciption currentPreciption) {
        this.currentPreciption = currentPreciption;
    }


}

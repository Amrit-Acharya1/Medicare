package com.acharyaamrit.medicare.model.response;

import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.google.gson.annotations.SerializedName;

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

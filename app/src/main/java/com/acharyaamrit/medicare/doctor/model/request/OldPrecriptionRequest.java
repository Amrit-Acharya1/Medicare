package com.acharyaamrit.medicare.doctor.model.request;

import com.google.gson.annotations.SerializedName;

public class OldPrecriptionRequest {
    @SerializedName("preciption_relation_id")
    private String preciption_relation_id;

    public OldPrecriptionRequest() {
    }

    public OldPrecriptionRequest(String preciption_relation_id) {
        this.preciption_relation_id = preciption_relation_id;
    }

    public String getPreciption_relation_id() {
        return preciption_relation_id;
    }

    public void setPreciption_relation_id(String preciption_relation_id) {
        this.preciption_relation_id = preciption_relation_id;
    }
}

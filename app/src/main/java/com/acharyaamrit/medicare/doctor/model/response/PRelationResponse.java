package com.acharyaamrit.medicare.doctor.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PRelationResponse {
    @SerializedName("preciptions")
    private List<PRelation> pRelationList;

    public PRelationResponse() {
    }

    public PRelationResponse(List<PRelation> pRelationList) {
        this.pRelationList = pRelationList;
    }

    public List<PRelation> getpRelationList() {
        return pRelationList;
    }

    public void setpRelationList(List<PRelation> pRelationList) {
        this.pRelationList = pRelationList;
    }
}

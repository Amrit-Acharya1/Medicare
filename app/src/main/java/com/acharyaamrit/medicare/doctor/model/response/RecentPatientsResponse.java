package com.acharyaamrit.medicare.doctor.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecentPatientsResponse {

    @SerializedName("recent_patients")
    private List<RecentPatient> recentPatientList;

    public RecentPatientsResponse() {
    }

    public RecentPatientsResponse(List<RecentPatient> recentPatientList) {
        this.recentPatientList = recentPatientList;
    }

    public List<RecentPatient> getRecentPatientList() {
        return recentPatientList;
    }

    public void setRecentPatientList(List<RecentPatient> recentPatientList) {
        this.recentPatientList = recentPatientList;
    }
}

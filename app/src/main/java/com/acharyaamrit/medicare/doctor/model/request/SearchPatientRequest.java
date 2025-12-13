package com.acharyaamrit.medicare.doctor.model.request;

public class SearchPatientRequest {
    private String search;



    public SearchPatientRequest(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}

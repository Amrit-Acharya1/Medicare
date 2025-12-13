package com.acharyaamrit.medicare.doctor.model.request;

public class MedicineRequest {
    String search;

    public MedicineRequest(String search) {
        this.search = search;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}

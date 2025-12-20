package com.acharyaamrit.medicare.doctor.model.request;

public class MedicineRequest {
    private String search;
    private int offset;
    private int limit;

    public MedicineRequest(String search) {
        this.search = search;
        this.offset = 0;
        this.limit = 30;
    }

    public MedicineRequest(String search, int offset, int limit) {
        this.search = search;
        this.offset = offset;
        this.limit = limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
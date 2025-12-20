package com.acharyaamrit.medicare.doctor.model.response;

import com.acharyaamrit.medicare.doctor.model.Medicine;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MedicineResponse {

    @SerializedName("medicines")
    private List<Medicine> medicines;

    @SerializedName("hasMore")
    private boolean hasMore;

    @SerializedName("nextOffset")
    private int nextOffset;

    @SerializedName("totalCount")
    private Integer totalCount;

    public MedicineResponse() {
    }

    public MedicineResponse(List<Medicine> medicines, boolean hasMore, int nextOffset, Integer totalCount) {
        this.medicines = medicines;
        this.hasMore = hasMore;
        this.nextOffset = nextOffset;
        this.totalCount = totalCount;
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<Medicine> medicines) {
        this.medicines = medicines;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
package com.acharyaamrit.medicare.pharmacy.listener;

public interface OnMedicineDispatchedListener {
    void onMedicineDispatched(int remainingCount, double priceAdded);
}
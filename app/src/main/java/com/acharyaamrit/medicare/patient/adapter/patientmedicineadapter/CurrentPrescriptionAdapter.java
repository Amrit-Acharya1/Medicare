package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;

import java.util.List;


public class CurrentPrescriptionAdapter extends RecyclerView.Adapter<CurrentPrescriptionAdapter.ViewHolder>{

    List<Preciption> currentPrescriptionList;
    public Context context;

    public CurrentPrescriptionAdapter(List<Preciption> currentPrescriptionList, Context context) {
        this.currentPrescriptionList = currentPrescriptionList;
        this.context = context;
    }

    @NonNull
    @Override
    public CurrentPrescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_current_prescription, parent, false);

        return new CurrentPrescriptionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrentPrescriptionAdapter.ViewHolder holder, int position) {
        Preciption currentPrescription = currentPrescriptionList.get(position);

        holder.textMedicineName.setText(currentPrescription.getMedicine_name());
        holder.textMedicineDoseQty.setText(currentPrescription.getDoasage_qty());
        holder.textMedicineDoseUnit.setText(currentPrescription.getDoasage_unit());
        holder.textDosageFrequency.setText(currentPrescription.getFrequency());
        holder.textManufacturer.setText(currentPrescription.getCompany_name());
        holder.medicineQty.setText("X" + currentPrescription.getQty());
//        holder.medicinePrice.setText(currentPrescription.getPrice());

    }

    @Override
    public int getItemCount() {
        return currentPrescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textMedicineName, textMedicineDoseQty, textMedicineDoseUnit, textDosageFrequency, textManufacturer, medicineQty, medicinePrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMedicineName = itemView.findViewById(R.id.textMedicineName);
            textMedicineDoseQty = itemView.findViewById(R.id.textMedicineDoseQty);
            textMedicineDoseUnit = itemView.findViewById(R.id.textMedicineDoseUnit);
            textDosageFrequency = itemView.findViewById(R.id.textDosageFrequency);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
            medicineQty = itemView.findViewById(R.id.medicineQty);
//            medicinePrice = itemView.findViewById(R.id.medicinePrice);


        }
    }
}

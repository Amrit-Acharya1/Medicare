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

public class DetailUserTimelineAdapter extends RecyclerView.Adapter<DetailUserTimelineAdapter.ViewHolder> {

Context context;
List<Preciption> preciptions;
    public DetailUserTimelineAdapter(List<Preciption> preciptions, Context context) {
        this.preciptions = preciptions;
        this.context = context;
    }
    public DetailUserTimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_current_prescription, parent, false);
        return new DetailUserTimelineAdapter.ViewHolder(view);
    }


    public void onBindViewHolder(@NonNull DetailUserTimelineAdapter.ViewHolder holder, int position) {
        Preciption preciption = preciptions.get(position);
        holder.textMedicineName.setText(preciption.getMedicine_name());
        holder.medicineQty.setText("x "+preciption.getQty());
        holder.textMedicineDoseQty.setText(preciption.getDoasage_qty());
        holder.textMedicineDoseUnit.setText(preciption.getDoasage_unit());
        holder.textDosageFrequency.setText(preciption.getFrequency());
        holder.textManufacturer.setText(preciption.getCompany_name());
//        holder.medicinePrice.setText(preciption.getPrice());



    }

    public int getItemCount() {
        return preciptions.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textMedicineName, medicineQty ,textMedicineDoseQty,textMedicineDoseUnit,textDosageFrequency,textManufacturer,medicinePrice,totalPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMedicineName = itemView.findViewById(R.id.textMedicineName);
            medicineQty = itemView.findViewById(R.id.medicineQty);
            textMedicineDoseQty = itemView.findViewById(R.id.textMedicineDoseQty);
            textMedicineDoseUnit = itemView.findViewById(R.id.textMedicineDoseUnit);
            textDosageFrequency = itemView.findViewById(R.id.textDosageFrequency);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
//            medicinePrice = itemView.findViewById(R.id.medicinePrice);

        }
    }
}

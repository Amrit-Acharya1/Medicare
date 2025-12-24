package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.annotation.SuppressLint;
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


    @SuppressLint("UseCompatLoadingForDrawables")
    public void onBindViewHolder(@NonNull DetailUserTimelineAdapter.ViewHolder holder, int position) {
        Preciption preciption = preciptions.get(position);
        holder.textMedicineName.setText(preciption.getMedicine_name());
        holder.medicineQty.setText("x "+preciption.getQty());
        holder.textMedicineDoseQty.setText(preciption.getDoasage_qty());
        holder.textMedicineDoseUnit.setText(preciption.getDoasage_unit());
        String freqTxt = preciption.getFrequency();
        holder.textDosageFrequency.setText(freqTxt+ " Times a day");
        holder.textManufacturer.setText(preciption.getCompany_name());
//        holder.medicinePrice.setText(preciption.getPrice());
        int freq = Integer.parseInt(freqTxt);
        if(freq>=3){
            holder.tagMorning.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());
            holder.tagAfternoon.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());
            holder.tagNight.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());

        }else if(freq == 2){
            holder.tagMorning.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());
            holder.tagNight.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());
            holder.tagAfternoon.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_inactive).mutate());

        }else{
            holder.tagMorning.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_active).mutate());
            holder.tagAfternoon.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_inactive).mutate());
            holder.tagNight.setBackground(context.getResources().getDrawable(R.drawable.bg_schedule_tag_inactive).mutate());
        }


    }

    public int getItemCount() {
        return preciptions.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView textMedicineName, medicineQty ,textMedicineDoseQty,textMedicineDoseUnit,textDosageFrequency,textManufacturer,tagMorning, tagAfternoon, tagNight;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMedicineName = itemView.findViewById(R.id.textMedicineName);
            medicineQty = itemView.findViewById(R.id.medicineQty);
            textMedicineDoseQty = itemView.findViewById(R.id.textDosage);
            textMedicineDoseUnit = itemView.findViewById(R.id.textDoasageUnit);
            textDosageFrequency = itemView.findViewById(R.id.textFrequency);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
            tagMorning= itemView.findViewById(R.id.tagMorning);
            tagAfternoon = itemView.findViewById(R.id.tagAfternoon);
            tagNight = itemView.findViewById(R.id.tagNight);

//            medicinePrice = itemView.findViewById(R.id.medicinePrice);

        }
    }
}

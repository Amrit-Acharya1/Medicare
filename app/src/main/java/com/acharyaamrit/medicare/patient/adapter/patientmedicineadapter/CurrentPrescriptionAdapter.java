package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.bumptech.glide.load.resource.drawable.DrawableResource;

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull CurrentPrescriptionAdapter.ViewHolder holder, int position) {
        Preciption currentPrescription = currentPrescriptionList.get(position);

        holder.textMedicineName.setText(currentPrescription.getMedicine_name());
        holder.textMedicineDoseQty.setText(currentPrescription.getDoasage_qty());
        holder.textMedicineDoseUnit.setText(currentPrescription.getDoasage_unit());
        String freqTxt = currentPrescription.getFrequency();
        holder.textDosageFrequency.setText(freqTxt+ " Times a day");
        holder.textManufacturer.setText(currentPrescription.getCompany_name());
        holder.medicineQty.setText("X " + currentPrescription.getQty());

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

        String pres = currentPrescription.getPharmacy_name();
        if (pres == null) {
            holder.claim_badge.setCardBackgroundColor(Color.parseColor("#FEE2E2"));
            holder.tv_claim.setText("Unclaim");
            holder.tv_claim.setTextColor(Color.parseColor("#EF4444"));
        } else {
            holder.claim_badge.setCardBackgroundColor(Color.parseColor("#D1FAE5"));
            holder.tv_claim.setText("Claimed");
            holder.tv_claim.setTextColor(Color.parseColor("#2E7CF6"));
        }

//        holder.medicinePrice.setText(currentPrescription.getPrice());

    }

    @Override
    public int getItemCount() {
        return currentPrescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_claim,textMedicineName, textMedicineDoseQty, textMedicineDoseUnit, textDosageFrequency, textManufacturer, medicineQty, tagMorning, tagAfternoon, tagNight;
        CardView claim_badge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textMedicineName = itemView.findViewById(R.id.textMedicineName);
            textMedicineDoseQty = itemView.findViewById(R.id.textDosage);
            textMedicineDoseUnit = itemView.findViewById(R.id.textDoasageUnit);
            textDosageFrequency = itemView.findViewById(R.id.textFrequency);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
            medicineQty = itemView.findViewById(R.id.medicineQty);
            tagMorning= itemView.findViewById(R.id.tagMorning);
            tagAfternoon = itemView.findViewById(R.id.tagAfternoon);
            tagNight = itemView.findViewById(R.id.tagNight);
            tv_claim = itemView.findViewById(R.id.tv_claim);
            claim_badge = itemView.findViewById(R.id.claim_badge);

//            medicinePrice = itemView.findViewById(R.id.medicinePrice);


        }
    }
}

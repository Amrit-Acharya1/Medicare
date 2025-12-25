package com.acharyaamrit.medicare.pharmacy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.pharmacy.PrescriptionDetailsActivity;
import com.acharyaamrit.medicare.pharmacy.model.PrescriptionPharmacy;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailPrescriptionAdapter extends RecyclerView.Adapter<DetailPrescriptionAdapter.ViewHolder>{

    private List<Preciption> prescriptionList;
    private Context context;

    public DetailPrescriptionAdapter(List<Preciption> prescriptionList, Context context) {
        this.prescriptionList = prescriptionList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPrescriptionAdapter.ViewHolder holder, int position) {
        Preciption preciption = prescriptionList.get(position);

        holder.tvMedicineName.setText(preciption.getMedicine_name());
        holder.tvDosageForm.setText(preciption.getDoasage_qty()+" "+preciption.getDoasage_unit());
        holder.tvDuration.setText("Duration: "+preciption.getDuration()+" "+ preciption.getDuration_type());
        holder.tvCompanyName.setText(preciption.getCompany_name());
        holder.tvCategory.setText("X "+preciption.getQty());







    }

    @NonNull
    @Override
    public DetailPrescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_list_pharmacy, parent, false);
        return new DetailPrescriptionAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicineName, tvDosageForm, tvDuration,tvCompanyName, tvCategory;
        CardView notAvailableBtn;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosageForm = itemView.findViewById(R.id.tvDosageForm);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            notAvailableBtn = itemView.findViewById(R.id.notAvailableBtn);



        }

    }
}

package com.acharyaamrit.medicare.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.model.patientModel.Medicine;

import java.util.ArrayList;
import java.util.List;

public class EveningAdapter extends RecyclerView.Adapter<EveningAdapter.ViewHolder>{
    private List<Medicine> medicineList;
    private Context context;

    public EveningAdapter(List<Medicine> medicineList, Context context) {
        this.medicineList = medicineList;
        this.context = context;
    }

    @NonNull
    @Override
    public EveningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LinearLayout.inflate(context, R.layout.item_homepage_layout, null);
        return new EveningAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EveningAdapter.ViewHolder holder, int position) {
        if (medicineList == null || position >= medicineList.size()) {
            return;
        }

        Medicine medicine = medicineList.get(position);
        if (medicine == null) {
            return;
        }

        // Safely set medicine name
        if (medicine.getMedicine_name() != null) {
            holder.textViewMedicineName.setText(medicine.getMedicine_name());
        } else {
            holder.textViewMedicineName.setText("Unknown Medicine");
        }

        // Safely set dosage quantity
        holder.textViewMedicineDoseQty.setText(String.valueOf(medicine.getDoasage_qty()));

        // Safely set dosage unit
        if (medicine.getDoasage_unit() != null) {
            holder.textViewMedicineDoseUnit.setText(medicine.getDoasage_unit());
        } else {
            holder.textViewMedicineDoseUnit.setText("");
        }
        // Safely set frequency/ID
        if (medicine.getFrequency() != null) {
            holder.textViewDosageFrequency.setText(medicine.getFrequency());
        } else {
            holder.textViewDosageFrequency.setText("");
        }

        // Optional: Set manufacturer if the field exists
        if (medicine.getCompany_name() != null) {
            holder.textViewManufacturer.setText(medicine.getCompany_name());
        }
        if (position == medicineList.size() - 1){
            holder.line_view.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }
    public void updateData(List<Medicine> newMedicineList) {
        this.medicineList = newMedicineList != null ? newMedicineList : new ArrayList<>();
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textViewMedicineName;
        TextView textViewMedicineDoseQty;
        TextView textViewMedicineDoseUnit;
        TextView textViewDosageFrequency;
        TextView textViewManufacturer;
        View line_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageMedicineIcon);
            textViewMedicineName = itemView.findViewById(R.id.textMedicineName);
            textViewMedicineDoseQty = itemView.findViewById(R.id.textMedicineDoseQty);
            textViewMedicineDoseUnit = itemView.findViewById(R.id.textMedicineDoseUnit);
            textViewDosageFrequency = itemView.findViewById(R.id.textDosageFrequency);
            textViewManufacturer = itemView.findViewById(R.id.textManufacturer);
            line_view = itemView.findViewById(R.id.line_view);
        }
    }
}

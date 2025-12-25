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

public class PharmacyPrescriptionAdaptor extends RecyclerView.Adapter<PharmacyPrescriptionAdaptor.ViewHolder>{

    private List<PrescriptionPharmacy> prescriptionList;
    private Context context;

    public PharmacyPrescriptionAdaptor(List<PrescriptionPharmacy> prescriptionList, Context context) {
        this.prescriptionList = prescriptionList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull PharmacyPrescriptionAdaptor.ViewHolder holder, int position) {
        PrescriptionPharmacy prescription = prescriptionList.get(position);

        holder.tv_prescription_id.setText(String.valueOf(prescription.getId()));
        holder.tv_doctor_name.setText(prescription.getDoctor_name());
        holder.tv_patient_id.setText("Patient ID: "+prescription.getPatient_id());
        String createdAt = prescription.getCreated_at();
        SimpleDateFormat inputFormat =
                new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

        Date date = null;
        try {
            date = inputFormat.parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

        String month = monthFormat.format(date);
        String day = dayFormat.format(date);
        String time = timeFormat.format(date);

        holder.tv_date_day.setText(day);
        holder.tv_date_month.setText(month);
        holder.tv_rx_id.setText(String.valueOf(prescription.getId()));
        holder.tv_pid.setText(prescription.getPatient_id());
        holder.tv_time.setText(time);
        holder.btn_view_prescription.setOnClickListener(v->{
            List<Preciption> pres = prescription.getPrescriptionList();
            Gson gson = new Gson();
            String presJson = gson.toJson(pres);
            Intent intent = new Intent(context, PrescriptionDetailsActivity.class);
            intent.putExtra("prescription_id", String.valueOf(prescription.getId()));
            intent.putExtra("doctor_name",prescription.getDoctor_name());
            intent.putExtra("patient_id",prescription.getPatient_id());
            intent.putExtra("day",day);
            intent.putExtra("month",month);
            intent.putExtra("time",time);
            intent.putExtra("presList", presJson);
            context.startActivity(intent);

        });



    }
    @NonNull
    @Override
    public PharmacyPrescriptionAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_prescription_card_pharmacy, parent, false);
        return new PharmacyPrescriptionAdaptor.ViewHolder(view);
    }


    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_prescription_id,tv_doctor_name,tv_patient_id,tv_date_day,tv_date_month,tv_rx_id,tv_pid,tv_time;
        Button btn_view_prescription,btn_print;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_prescription_id = itemView.findViewById(R.id.tv_prescription_id);
            tv_doctor_name = itemView.findViewById(R.id.tv_doctor_name);
            tv_patient_id = itemView.findViewById(R.id.tv_patient_id);
            tv_date_day = itemView.findViewById(R.id.tv_date_day);
            tv_date_month = itemView.findViewById(R.id.tv_date_month);
            tv_rx_id = itemView.findViewById(R.id.tv_rx_id);
            tv_pid = itemView.findViewById(R.id.tv_pid);
            tv_time = itemView.findViewById(R.id.tv_time);
            btn_view_prescription = itemView.findViewById(R.id.btn_view_prescription);
//            btn_print = itemView.findViewById(R.id.btn_print);


        }

    }
}

package com.acharyaamrit.medicare.doctor.adapter;

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
import com.acharyaamrit.medicare.doctor.DoctorPatientDetailActivity;
import com.acharyaamrit.medicare.doctor.MedicineSearch;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchPatientAdapter extends RecyclerView.Adapter<SearchPatientAdapter.ViewHolder>{

    private final List<Patient> patientList;
    private final Context context;

    public SearchPatientAdapter(List<Patient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull SearchPatientAdapter.ViewHolder holder, int position) {
        Patient patient = patientList.get(position);
        String pName = String.valueOf(patient.getName());
        String pId = String.valueOf(patient.getPatient_id());
        String pAddress = String.valueOf(patient.getAddress());
        String pBloodGroup = String.valueOf(patient.getBlood_group());
        String pAge = String.valueOf(patient.getDob());
        String pGender;
        if(String.valueOf(patient.getGender()).equals("1")){
            pGender = "Male";
        }else{
            pGender = "Female";
        }
        String pPhone = String.valueOf(patient.getContact());

        holder.name.setText(pName);
        holder.patientId.setText(pId);
        holder.address.setText(pAddress);
        holder.bloodGroup.setText(pBloodGroup);
        holder.age.setText(calculateAge(pAge));
        holder.gender.setText(pGender);
        holder.phone.setText(pPhone);
        holder.btn_view_details.setOnClickListener(v->{
            Intent intent = new Intent(context, DoctorPatientDetailActivity.class);
            intent.putExtra("patient_id", pId);
            intent.putExtra("patient_name", pName);
            intent.putExtra("patient_address", pAddress);
            intent.putExtra("patient_blood_group", pBloodGroup);
            intent.putExtra("patient_age", calculateAge(pAge));
            intent.putExtra("patient_gender", pGender);
            intent.putExtra("patient_phone", pPhone);
            context.startActivity(intent);

        });
        holder.patient_card.setOnClickListener(v->{
            Intent intent = new Intent(context, DoctorPatientDetailActivity.class);
            intent.putExtra("patient_id", pId);
            intent.putExtra("patient_name", pName);
            intent.putExtra("patient_address", pAddress);
            intent.putExtra("patient_blood_group", pBloodGroup);
            intent.putExtra("patient_age", calculateAge(pAge));
            intent.putExtra("patient_gender", pGender);
            intent.putExtra("patient_phone", pPhone);
            context.startActivity(intent);
        });


        holder.btn_prescribe.setOnClickListener(v->{
            Intent intent = new Intent(context, MedicineSearch.class);
            intent.putExtra("patient_id", pId);
            intent.putExtra("patient_name", pName);
            context.startActivity(intent);
        });



    }



    @NonNull
    @Override
    public SearchPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_card, parent, false);
        return new SearchPatientAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, patientId, address, bloodGroup, age, gender,phone;
        CardView patient_card;
        Button btn_prescribe, btn_view_details;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tv_name);
            patientId = itemView.findViewById(R.id.tv_patient_id);
            address = itemView.findViewById(R.id.tv_location);
            bloodGroup = itemView.findViewById(R.id.tv_blood_group);
            age = itemView.findViewById(R.id.tv_age);
            gender = itemView.findViewById(R.id.tv_gender);
            btn_prescribe = itemView.findViewById(R.id.btn_prescribe);
            btn_view_details = itemView.findViewById(R.id.btn_view_details);
            phone = itemView.findViewById(R.id.tv_phone);
            patient_card=itemView.findViewById(R.id.patient_card);

        }

    }
    public String calculateAge(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(dob);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            // If today's date is before the birthday this year, subtract one
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age); // convert int to String
        } catch (ParseException e) {
            e.printStackTrace();
            return "0"; // fallback in case of error
        }
    }
}

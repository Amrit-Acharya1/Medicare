package com.acharyaamrit.medicare.doctor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.adapter.notification.NotificationAdapter;
import com.acharyaamrit.medicare.doctor.model.response.RecentPatient;

import java.util.List;

public class RecientPatientAdapter extends RecyclerView.Adapter<RecientPatientAdapter.ViewHolder> {

    private final List<RecentPatient> patientList;
    private final Context context;

    public RecientPatientAdapter(List<RecentPatient> patientList, Context context) {
        this.patientList = patientList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull RecientPatientAdapter.ViewHolder holder, int position) {

        RecentPatient patient = patientList.get(position);
        String pName = String.valueOf(patient.getName());
        String pTime = String.valueOf(patient.getCreated_at());

        holder.name.setText(pName);
        holder.time.setText(pTime);


    }

    @NonNull
    @Override
    public RecientPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recient_patient, parent, false);
        return new RecientPatientAdapter.ViewHolder(view);
    }
    @Override
    public int getItemCount() {
        return patientList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.patientName);
            time = itemView.findViewById(R.id.recentPTime);


        }

    }
}

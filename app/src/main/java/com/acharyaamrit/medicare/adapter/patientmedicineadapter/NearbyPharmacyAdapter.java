package com.acharyaamrit.medicare.adapter.patientmedicineadapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.model.patientModel.PharmacyMap;

import java.util.List;

public class NearbyPharmacyAdapter extends RecyclerView.Adapter<NearbyPharmacyAdapter.ViewHolder>{

    List<PharmacyMap> pharmacyMapList;
    private Context context;

    public NearbyPharmacyAdapter(List<PharmacyMap> pharmacyMapList, Context context) {
        this.pharmacyMapList = pharmacyMapList;
        this.context = context;
    }

    @NonNull
    @Override
    public NearbyPharmacyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nerarby_pharmacy_location, parent, false);
        return new NearbyPharmacyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyPharmacyAdapter.ViewHolder holder, int position) {
        PharmacyMap pharmacyMap = pharmacyMapList.get(position);
        holder.pharmacy_name.setText(pharmacyMap.getPharmacy_name());

        //converted to km
        String distance = pharmacyMap.getDistance();
        String formatted = distance.length() > 4 ? distance.substring(0, 4) : distance;
        holder.pharmacy_distance.setText(formatted + " Km");

        holder.pharmacy_visit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "visiting", Toast.LENGTH_SHORT).show();
            }
        });

        if (pharmacyMap.getContact() != null && !pharmacyMap.getContact().isEmpty()) {
            // Show call button
            holder.pharmacy_call_btn.setVisibility(View.VISIBLE);
            // Restore normal weights
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.pharmacy_visit_btn.getLayoutParams();
            params.weight = 1;
            holder.pharmacy_visit_btn.setLayoutParams(params);
        } else {
            // Hide call button
            holder.pharmacy_call_btn.setVisibility(View.GONE);
            // Make visit button full width
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.pharmacy_visit_btn.getLayoutParams();
            params.weight = 2;
            holder.pharmacy_visit_btn.setLayoutParams(params);
        }

        holder.pharmacy_call_btn.setOnClickListener(v -> {
            if (pharmacyMap.getContact() != null) {
                String phoneNumber = "tel:" + pharmacyMap.getContact();
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse(phoneNumber));
                context.startActivity(dialIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pharmacyMapList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView pharmacy_image;
        TextView pharmacy_name;
        TextView pharmacy_distance;
        AppCompatButton pharmacy_visit_btn;
        AppCompatButton pharmacy_call_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pharmacy_image = itemView.findViewById(R.id.pharmacy_image);

            pharmacy_name = itemView.findViewById(R.id.pharmacy_name);
            pharmacy_distance = itemView.findViewById(R.id.pharmacy_distance);
            pharmacy_visit_btn = itemView.findViewById(R.id.pharmacy_visit_btn);
            pharmacy_call_btn = itemView.findViewById(R.id.pharmacy_call_btn);

        }



    }
}

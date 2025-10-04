package com.acharyaamrit.medicare.adapter.patientmedicineadapter;

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
        holder.pharmacy_distance.setText(pharmacyMap.getDistance());

        holder.pharmacy_visit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "visiting", Toast.LENGTH_SHORT).show();
            }
        });

        holder.pharmacy_call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //redirect to phone call
                if (pharmacyMap.getContact() != null) {
                    String phoneNumber = "tel:" + pharmacyMap.getContact();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse(phoneNumber));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        context.startActivity(callIntent);
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                    }
                }
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

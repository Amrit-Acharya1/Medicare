package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.patient.MapFragment;
import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.acharyaamrit.medicare.patient.model.patientModel.PharmacyMap;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nerarby_pharmacy_location, parent, false);
        return new NearbyPharmacyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyPharmacyAdapter.ViewHolder holder, int position) {
        PharmacyMap pharmacyMap = pharmacyMapList.get(position);
        holder.pharmacy_name.setText(pharmacyMap.getPharmacy_name());

        String self = pharmacyMap.getSelf();

        if(self.equals("1")){
            holder.pharmacy_star.setVisibility(VISIBLE);
        }

        //converted to km
        holder.pharmacy_description.setText(pharmacyMap.getDescription());


        holder.pharmacy_visit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
                View bottomSheetView = LayoutInflater.from(v.getContext()).inflate(R.layout.item_choose_map, null);
                bottomSheetDialog.setContentView(bottomSheetView);

                // Find buttons in bottom sheet
                View inAppMapBtn = bottomSheetView.findViewById(R.id.btnInAppMap);
                View googleMapBtn = bottomSheetView.findViewById(R.id.btnGoogleMap);

                // Get patient location from SharedPreferences
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("user_preference", Context.MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);

                DatabaseHelper dbHelper = new DatabaseHelper(v.getContext());
                Patient patient = dbHelper.getPatientByToken(token);

                String patientLat = patient.getLat();
                String patientLon = patient.getLongt();

                String pharmacyLat = pharmacyMap.getLat();
                String pharmacyLon = pharmacyMap.getLongt();

                String pharmacyName = pharmacyMap.getPharmacy_name();

                // In-App Map Button Click
                inAppMapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();

                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment mapFragment = new MapFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("pharmacy_name", pharmacyName);
                        bundle.putString("latitude", pharmacyLat);
                        bundle.putString("longitude", pharmacyLon);
                        mapFragment.setArguments(bundle);

                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, mapFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });

                // Google Map Button Click
                googleMapBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();

                        if (patientLat != null && patientLon != null) {
                            // Open Google Maps with directions from patient location to pharmacy
                            String uri = String.format(
                                    "https://www.google.com/maps/dir/?api=1&origin=%s,%s&destination=%s,%s&travelmode=driving",
                                    patientLat, patientLon, pharmacyLat, pharmacyLon
                            );

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            intent.setPackage("com.google.android.apps.maps");

                            // Check if Google Maps is installed
                            if (intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                                v.getContext().startActivity(intent);
                            } else {
                                // If Google Maps not installed, open in browser
                                intent.setPackage(null);
                                v.getContext().startActivity(intent);
                            }
                        } else {
                            // If patient location not available, just show pharmacy location
                            String uri = String.format(
                                    "https://www.google.com/maps/search/?api=1&query=%s,%s",
                                    pharmacyLat, pharmacyLon
                            );

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            v.getContext().startActivity(intent);
                        }
                    }
                });

                bottomSheetDialog.show();
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
            params.rightMargin = 0;
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
        TextView pharmacy_description;
        ImageView pharmacy_star;
        AppCompatButton pharmacy_visit_btn;
        AppCompatButton pharmacy_call_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pharmacy_image = itemView.findViewById(R.id.pharmacy_image);

            pharmacy_name = itemView.findViewById(R.id.pharmacy_name);
            pharmacy_description = itemView.findViewById(R.id.pharmacy_description);
            pharmacy_visit_btn = itemView.findViewById(R.id.pharmacy_visit_btn);
            pharmacy_call_btn = itemView.findViewById(R.id.pharmacy_call_btn);
            pharmacy_star = itemView.findViewById(R.id.pharmacy_star_icon);

        }



    }
}

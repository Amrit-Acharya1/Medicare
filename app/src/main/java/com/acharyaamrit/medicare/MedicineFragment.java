package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.adapter.patientmedicineadapter.CurrentPrescriptionAdapter;
import com.acharyaamrit.medicare.adapter.patientmedicineadapter.NearbyPharmacyAdapter;
import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.patientModel.PharmacyMap;
import com.acharyaamrit.medicare.model.response.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.acharyaamrit.medicare.model.response.NearbyPharmacyResponse;
import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicineFragment extends Fragment {
    private TextView doctor_name, totalPrice, name, pid;
    private LottieAnimationView lottieAnimationView;
    private ConstraintLayout notificationButton;

    public MedicineFragment() {
        // Required empty public constructor
    }
    @SuppressLint({"NotifyDataSetChanged", "CutPasteId"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.currentPrescription_medicine);
        RecyclerView nearby_location_patient = view.findViewById(R.id.nearby_location_patient);
        lottieAnimationView = view.findViewById(R.id.loading_map);


        doctor_name = view.findViewById(R.id.doctor_name);
        totalPrice = view.findViewById(R.id.totalPrice);
        name = view.findViewById(R.id.name);
        pid = view.findViewById(R.id.pid);
        notificationButton = view.findViewById(R.id.notificationButton);


        nearby_location_patient.setVisibility(GONE);

        DatabaseHelper dbHelper2 = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TextView total_price = view.findViewById(R.id.totalPrice);

        Patient patient = dbHelper2.getPatientByToken(token);

        if(patient != null){
            name.setText(patient.getName());
            pid.setText("PID: "+ String.valueOf(patient.getPatient_id()));
        }


        CurrentPreciptionResponse currentPreciptionResponse = dbHelper2.getCurrentPreciptionWithItems();
        CurrentPreciption currentPreciption = currentPreciptionResponse.getCurrentPreciption();



        if(currentPreciption != null){
            List<Preciption> preciptionList = currentPreciption.getPreciptionList();

            CurrentPrescriptionAdapter adapter = new CurrentPrescriptionAdapter(preciptionList, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            doctor_name.setText(String.format("Prescribed by Dr. %s", currentPreciption.getDoctor_name()));




            double totalPrice = 0;

            for(Preciption preciption : preciptionList){
                totalPrice = totalPrice + Double.parseDouble(preciption.getPrice());
            }

            total_price.setText(String.format("Rs. %.2f", totalPrice));

        }else{
            view.findViewById(R.id.current_prescription).setVisibility(GONE);
        }
//        nearby_location_patient.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        nearby_location_patient.setLayoutManager(layoutManager);

// Attach a PagerSnapHelper for carousel-like snapping
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(nearby_location_patient);

        fetchNearbyPharmacyFromApi(token, nearby_location_patient);

        return view;
    }

    private void fetchNearbyPharmacyFromApi(String token, RecyclerView nearby_location_patient) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<NearbyPharmacyResponse> call = apiService.getNearbyPharmancy("Bearer " + token);

        call.enqueue(new Callback<NearbyPharmacyResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<NearbyPharmacyResponse> call, Response<NearbyPharmacyResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        NearbyPharmacyResponse nearbyPharmacyResponse = response.body();
                        List<PharmacyMap> nearbyPharmacies = nearbyPharmacyResponse.getPharmacy_map();

                        NearbyPharmacyAdapter adapter = new NearbyPharmacyAdapter(nearbyPharmacies, getContext());

                        nearby_location_patient.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        nearby_location_patient.setVisibility(VISIBLE);
                        lottieAnimationView.setVisibility(GONE);

                    }catch (Exception e){}
                }
            }

            @Override
            public void onFailure(Call<NearbyPharmacyResponse> call, Throwable t) {
                Toast.makeText(getContext(), "No Internet Connection, Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof PatientHomepageActivity) {
            ((PatientHomepageActivity) getActivity()).disableSwipeRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof PatientHomepageActivity) {
            ((PatientHomepageActivity) getActivity()).enableSwipeRefresh();
        }
    }

}
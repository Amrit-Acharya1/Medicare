package com.acharyaamrit.medicare.pharmacy;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.doctor.model.request.PRelationRequest;
import com.acharyaamrit.medicare.doctor.model.response.SearchPatientResponse;
import com.acharyaamrit.medicare.pharmacy.adapter.PharmacyPrescriptionAdaptor;
import com.acharyaamrit.medicare.pharmacy.model.PrescriptionPharmacy;
import com.acharyaamrit.medicare.pharmacy.model.response.PrescriptionPharmacyResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DispatchMedicineActivity extends AppCompatActivity {
    TextView tv_name_patient,tv_pid_patient;
    CardView btnBack;
    LinearLayout noMedicine_pharmacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dispatch_medicine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializedView();
        Intent intent = getIntent();
        String patientName = intent.getStringExtra("patient_name");
        String patientId = intent.getStringExtra("patient_id");
        setUpHeader(patientName,patientId);
        setupListeners();
        loadPrescriptins(patientId);


    }



    private void setUpHeader( String patientName,String patientId ){
        tv_name_patient.setText(patientName);
        tv_pid_patient.setText("PID: "+patientId);
    }
    private void initializedView() {
        tv_name_patient = findViewById(R.id.tv_name_patient);
        tv_pid_patient = findViewById(R.id.tv_pid_patient);
        btnBack = findViewById(R.id.btnBack);
        noMedicine_pharmacy = findViewById(R.id.noMedicine_pharmacy);
    }
    private void setupListeners() {
        btnBack.setOnClickListener(v->{
            finish();
        });
    }
    private void loadPrescriptins(String pid){
        RecyclerView rv_prescription = findViewById(R.id.rv_patients_prescription);
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PRelationRequest pRelationRequest = new PRelationRequest(Integer.parseInt(pid));
        Call<PrescriptionPharmacyResponse> call = apiService.fetchPrecriptionForPatientByPharmacy("Bearer " + token,pRelationRequest);
        call.enqueue(new Callback<PrescriptionPharmacyResponse>() {
            @Override
            public void onResponse(Call<PrescriptionPharmacyResponse> call, retrofit2.Response<PrescriptionPharmacyResponse> response){

                if (response.isSuccessful() && response.body() != null) {
                    try{

                        List<PrescriptionPharmacy> prescriptionPharmacyResponse = response.body().getpRelationPharmacyList();
                        if(prescriptionPharmacyResponse.isEmpty()){
                            noMedicine_pharmacy.setVisibility(VISIBLE);
                        }else{

                        noMedicine_pharmacy.setVisibility(GONE);
                        }
                        PharmacyPrescriptionAdaptor adapter = new PharmacyPrescriptionAdaptor(prescriptionPharmacyResponse, DispatchMedicineActivity.this);
                        rv_prescription.setLayoutManager(new LinearLayoutManager(DispatchMedicineActivity.this));
                        rv_prescription.setAdapter(adapter);



                    } catch (Exception e) {
                        Toast.makeText(DispatchMedicineActivity.this, "Failed to Fetch Prescription", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    noMedicine_pharmacy.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PrescriptionPharmacyResponse> call, Throwable t) {
                noMedicine_pharmacy.setVisibility(VISIBLE);

            }

        });


    }
}
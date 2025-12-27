package com.acharyaamrit.medicare.pharmacy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.doctor.model.request.OldPrecriptionRequest;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.pharmacy.adapter.DetailPrescriptionAdapter;
import com.acharyaamrit.medicare.pharmacy.model.request.PrescriptionRelationForBillEmailRequest;
import com.acharyaamrit.medicare.pharmacy.model.response.PrescriptionPharmacyResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;

public class PrescriptionDetailsActivity extends AppCompatActivity implements DetailPrescriptionAdapter.OnMedicineDispatchedListener {

    TextView tv_prescription_id, tv_doctor_name, tv_patient_id, tv_date_day, tv_date_month, tv_total_medicines, tv_time, tv_medicine_count, tv_total_amount;
    CardView btnBack;
    Button btn_dispatch_all;
    RecyclerView recyclerView;

    // Track total amount
    private double totalAmount = 0.0;
    private int totalMedicinesDispatched = 0;
    private int initialMedicineCount = 0;

    // Decimal formatter for currency
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_prescription_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializedView();
        setUpview();
        setUpListeners();
    }

    private void setUpListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btn_dispatch_all.setOnClickListener(v -> {
            if (totalMedicinesDispatched == 0) {
                Toast.makeText(this, "No medicines have been dispatched yet", Toast.LENGTH_SHORT).show();
                return;
            }

            // Handle dispatch all - you can add your logic here
            // For example, show a summary dialog or navigate to a confirmation screen
            showDispatchSummary();
        });
    }

    private void showDispatchSummary() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Dispatch Summary")
                .setMessage("Total Medicines Dispatched: " + totalMedicinesDispatched +
                        "\nTotal Amount: Rs. " + decimalFormat.format(totalAmount))
                .setPositiveButton("Confirm", (dialog, which) -> {
                    sendBillMail();
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void sendBillMail() {
        String presRelId = getIntent().getStringExtra("prescription_id");
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PrescriptionRelationForBillEmailRequest request = new PrescriptionRelationForBillEmailRequest(presRelId);
        Call<UserResponse> call = apiService.sendBillEmail("Bearer " + token, request);
        call.enqueue(new retrofit2.Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(PrescriptionDetailsActivity.this, "Medicine Dispatch | Email Sent Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PrescriptionDetailsActivity.this, "Cannot Send Email", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(PrescriptionDetailsActivity.this, "NO Internet", Toast.LENGTH_SHORT).show();

            }

        });

    }

    private void initializedView() {
        tv_prescription_id = findViewById(R.id.tv_prescription_id);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        tv_date_day = findViewById(R.id.tv_date_day);
        tv_date_month = findViewById(R.id.tv_date_month);
        tv_total_medicines = findViewById(R.id.tv_total_medicines);
        tv_time = findViewById(R.id.tv_time);
        tv_medicine_count = findViewById(R.id.tv_medicine_count);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        btnBack = findViewById(R.id.btnBack);
        btn_dispatch_all = findViewById(R.id.btn_dispatch_all);
        recyclerView = findViewById(R.id.rv_medicines_pharmacy);
    }

    private void setUpview() {
        String prescriptionId = getIntent().getStringExtra("prescription_id");
        String doctorName = getIntent().getStringExtra("doctor_name");
        String patientId = getIntent().getStringExtra("patient_id");
        String day = getIntent().getStringExtra("day");
        String month = getIntent().getStringExtra("month");
        String time = getIntent().getStringExtra("time");
        String presListJson = getIntent().getStringExtra("presList");

        Gson gson = new Gson();
        List<Preciption> presList = gson.fromJson(presListJson, new TypeToken<List<Preciption>>() {}.getType());

        // Store initial count
        initialMedicineCount = presList.size();

        tv_prescription_id.setText("Rx ID: " + prescriptionId);
        tv_doctor_name.setText(doctorName);
        tv_patient_id.setText("Patient ID: " + patientId);
        tv_date_day.setText(day);
        tv_date_month.setText(month);
        tv_time.setText(time);

        // Initialize counts and amount
        updateMedicineCount(presList.size());
        updateTotalAmount();

        // Pass 'this' as the listener
        DetailPrescriptionAdapter adapter = new DetailPrescriptionAdapter(presList, PrescriptionDetailsActivity.this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(PrescriptionDetailsActivity.this));
        recyclerView.setAdapter(adapter);
    }

    // Implement the callback method
    @Override
    public void onMedicineDispatched(int remainingCount, double priceAdded) {
        // Update medicine count
        updateMedicineCount(remainingCount);

        // Update total amount
        totalAmount += priceAdded;
        totalMedicinesDispatched++;
        updateTotalAmount();

        // Check if all medicines are dispatched
        if (remainingCount == 0) {
            // Show completion message
            Toast.makeText(this, "All medicines have been priced!", Toast.LENGTH_SHORT).show();

            // Change button text to indicate completion
            btn_dispatch_all.setText("Complete Dispatch (Rs. " + decimalFormat.format(totalAmount) + ")");
        }
    }

    private void updateMedicineCount(int count) {
        tv_total_medicines.setText(String.valueOf(count));
        tv_medicine_count.setText(count + " items remaining");
    }

    private void updateTotalAmount() {
        tv_total_amount.setText("Rs. " + decimalFormat.format(totalAmount));
    }
}
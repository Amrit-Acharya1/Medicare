package com.acharyaamrit.medicare.pharmacy;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.pharmacy.adapter.DetailPrescriptionAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class PrescriptionDetailsActivity extends AppCompatActivity {
TextView tv_prescription_id, tv_doctor_name, tv_patient_id,tv_date_day, tv_date_month,tv_total_medicines, tv_time,tv_medicine_count;
CardView btnBack;

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
        btnBack.setOnClickListener(v->{
            finish();
        });
    }


    private void initializedView() {
        tv_prescription_id = findViewById(R.id.tv_prescription_id);
        tv_doctor_name = findViewById(R.id.tv_doctor_name);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        tv_date_day = findViewById(R.id.tv_date_day);
        tv_date_month = findViewById(R.id.tv_date_month);
        tv_total_medicines= findViewById(R.id.tv_total_medicines);
        tv_time = findViewById(R.id.tv_time);
        tv_medicine_count = findViewById(R.id.tv_medicine_count);
        btnBack = findViewById(R.id.btnBack);

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
        tv_prescription_id.setText("Rx ID: "+prescriptionId);
        tv_doctor_name.setText(doctorName);
        tv_patient_id.setText("Patient ID: "+patientId);
        tv_date_day.setText(day);
        tv_date_month.setText(month);
        tv_time.setText(time);
        tv_total_medicines.setText(String.valueOf(presList.size()));
        tv_medicine_count.setText(String.valueOf(presList.size())+" items");

        DetailPrescriptionAdapter adapter = new DetailPrescriptionAdapter(presList,PrescriptionDetailsActivity.this);
        RecyclerView recyclerView = findViewById(R.id.rv_medicines_pharmacy);
        recyclerView.setLayoutManager(new LinearLayoutManager(PrescriptionDetailsActivity.this));
        recyclerView.setAdapter(adapter);



    }
}
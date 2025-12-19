package com.acharyaamrit.medicare.doctor;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.UserTimelineActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.controller.api.FetchUserTimelineApi;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.acharyaamrit.medicare.common.model.request.TimelineRequest;
import com.acharyaamrit.medicare.common.model.response.TimelineResponse;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.UserTimelineAdapter;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorPatientDetailActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView tv_name, tv_patient_id, tv_blood_group, tv_age, tv_gender, tv_phone,tv_location;
    ConstraintLayout timelineMain;
    Button btn_prescribe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_patient_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializedView();
        setUpListeners();
        populateDetail();

    }
    private void initializedView(){
        toolbar = findViewById(R.id.toolbar);
        tv_name = findViewById(R.id.tv_name);
        tv_patient_id = findViewById(R.id.tv_patient_id);
        tv_blood_group = findViewById(R.id.tv_blood_group);
        tv_age = findViewById(R.id.tv_age);
        tv_gender = findViewById(R.id.tv_gender);
        tv_phone = findViewById(R.id.tv_phone);
        tv_location = findViewById(R.id.tv_location);
        timelineMain = findViewById(R.id.timelineMain);
        btn_prescribe = findViewById(R.id.btn_prescribe);
    }
    private void setUpListeners(){
        toolbar.setOnClickListener(v->{
            finish();
        });
        btn_prescribe.setOnClickListener(v->{
            Intent intent = new Intent(DoctorPatientDetailActivity.this, MedicineSearch.class);
            intent.putExtra("patient_id",getIntent().getStringExtra("patient_id"));
            intent.putExtra("patient_name",getIntent().getStringExtra("patient_name"));
            startActivity(intent);
        });

    }
    private void populateDetail(){
        Intent intent = getIntent();
        String name = intent.getStringExtra("patient_name");
        String patientId = intent.getStringExtra("patient_id");
        String bloodGroup = intent.getStringExtra("patient_blood_group");
        String age = intent.getStringExtra("patient_age");
        String gender = intent.getStringExtra("patient_gender");
        String phone = intent.getStringExtra("patient_phone");
        String address = intent.getStringExtra("patient_address");

        tv_name.setText(name);
        tv_patient_id.setText("PID: " + patientId);
        tv_blood_group.setText(bloodGroup);
        tv_age.setText(age);
        tv_gender.setText(gender);
        tv_phone.setText(phone);
        tv_location.setText(address);
        fetchTimeLine(patientId);

    }
    private void fetchTimeLine(String patient_id){

        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        TimelineRequest timelineRequest = new TimelineRequest(Integer.parseInt(patient_id));

        Call<TimelineResponse> call = apiService.getPatientTimeline("Bearer " + token, timelineRequest);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    try{

                        List<TimelineItem> timelineItems = response.body().getTimelineItemsList();
                        if (!timelineItems.isEmpty()) {
                            timelineMain.setVisibility(VISIBLE);
                            UserTimelineAdapter userTimelineAdapter = new UserTimelineAdapter(timelineItems, DoctorPatientDetailActivity.this);
                            RecyclerView recyclerView = findViewById(R.id.timelineRecycler);
                            recyclerView.setLayoutManager(new LinearLayoutManager(DoctorPatientDetailActivity.this));
                            recyclerView.setAdapter(userTimelineAdapter);
                        }else{
                            timelineMain.setVisibility(GONE);

                        }


                    } catch (Exception e) {
                        Toast.makeText(DoctorPatientDetailActivity.this, "Error Fetching Timeline", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {

            }
        });
    }





}
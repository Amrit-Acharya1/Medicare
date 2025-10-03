package com.acharyaamrit.medicare;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.RoutineMedicineResponse;
import com.acharyaamrit.medicare.model.UserResponse;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientHomepageActivity extends AppCompatActivity {

//    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left,0, systemBars.right, systemBars.bottom);
            return insets;
        });
        LottieAnimationView lottieAnimationView = findViewById(R.id.loading_lottie);

        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        storeToDatabase(token);
        storeRoutineMedicine(token);


        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lottieAnimationView.setVisibility(GONE);

                findViewById(R.id.home_button_background)
                        .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_selected_back));

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();


                findViewById(R.id.home_button).setOnClickListener(v -> {
                    findViewById(R.id.home_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_selected_back));

                    findViewById(R.id.medicine_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));

                    findViewById(R.id.profile_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new HomeFragment())
                            .commit();
                });

                findViewById(R.id.qr_button).setOnClickListener(v -> {
                    // Handle QR button click
                });


                findViewById(R.id.medicine_button).setOnClickListener(v -> {
                    findViewById(R.id.medicine_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_selected_back));
                    findViewById(R.id.home_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));
                    findViewById(R.id.profile_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new MedicineFragment())
                            .commit();

                });
                findViewById(R.id.profile_button).setOnClickListener(v -> {
                    findViewById(R.id.profile_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottom_selected_back));
                    findViewById(R.id.home_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));
                    findViewById(R.id.medicine_button_background)
                            .setBackground(ContextCompat.getDrawable(getApplicationContext(), R.color.blue));
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new ProfileFragment())
                            .commit();

                });
            }
        }, 2000);


    }

    private void storeRoutineMedicine(String token) {

        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Call<RoutineMedicineResponse> call = apiService.getRoutineMedicine("Bearer " + token);
            call.enqueue(new Callback<RoutineMedicineResponse>() {
                @Override
                public void onResponse(Call<RoutineMedicineResponse> call, Response<RoutineMedicineResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {


                        RoutineMedicineResponse routineMedicineResponse = response.body();

                        Gson gson = new Gson();
                        String routineJson = gson.toJson(routineMedicineResponse);


                        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("routine_medicine_data", routineJson);
                        editor.apply();


                    }else{
                        try {
//                            String errorJson = response.errorBody().string();
//                            Gson gson = new Gson();
//                            UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);
//
//                            String title = errorResponse.getTitle();
//                            String message = errorResponse.getMessage();
//
//
////                            if(title.equalsIgnoreCase("Unauthenticated")){
////
////                                SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
////                                SharedPreferences.Editor editor = sharedPreferences.edit();
////                                editor.clear();
////                                editor.apply();
////                                Intent intent = new Intent(PatientHomepageActivity.this, LoginActivity.class);
////                                startActivity(intent);
////                                finish();
////                            }


                        } catch (Exception e) {

                            new AlertDialog.Builder(PatientHomepageActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Unexpected error bbb: " + response.code())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<RoutineMedicineResponse> call, Throwable t) {
                    //already handle in preception api
                }
            });


        }catch(Exception e){
        }



    }

    private void storeToDatabase(String token) {
        try{

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Call<CurrentPreciptionResponse> call = apiService.getCurrentPreciption("Bearer " + token);
            call.enqueue(new Callback<CurrentPreciptionResponse>() {

                @Override
                public void onResponse(Call<CurrentPreciptionResponse> call, Response<CurrentPreciptionResponse> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            CurrentPreciptionResponse prescriptionResponse = response.body();
                            CurrentPreciption currentPreciption = prescriptionResponse.getCurrentPreciption();

                            if (currentPreciption == null) {
                                Toast.makeText(PatientHomepageActivity.this, "No prescription data available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Delete old data first
                            dbHelper.deleteCurrentPreciption();
                            dbHelper.deletePreciptionItem();

                            // Insert CurrentPreciption
                            long prescriptionResult = dbHelper.insertCurrentPreciption(currentPreciption);

                            if (prescriptionResult == -1) {
                                Toast.makeText(PatientHomepageActivity.this, "Failed to insert prescription", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // IMPORTANT: Use the newly inserted prescription ID (local database ID)
                            int localPrescriptionId = (int) prescriptionResult;

                            // Insert Preciption items
                            List<Preciption> preciptionItems = currentPreciption.getPreciptionList();
                            if (preciptionItems != null && !preciptionItems.isEmpty()) {
                                int successCount = 0;

                                for (Preciption item : preciptionItems) {
                                    // Set the correct local prescription_relation_id
                                    item.setPreciption_relation_id(String.valueOf(localPrescriptionId));

                                    long itemResult = dbHelper.insertPreciptionItem(item);
                                    if (itemResult != -1) {
                                        successCount++;
                                    }
                                }

                                if (successCount > 0) {
                                    Toast.makeText(PatientHomepageActivity.this,
                                            successCount + " prescription items saved successfully",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(PatientHomepageActivity.this,
                                            "Failed to save prescription items",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(PatientHomepageActivity.this,
                                        "Prescription saved but no items found",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Toast.makeText(PatientHomepageActivity.this,
                                    "Error processing data: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            String errorJson = response.errorBody().string();
                            Gson gson = new Gson();
                            UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);

                            String title = errorResponse.getTitle();
                            String message = errorResponse.getMessage();

                            if (message.equalsIgnoreCase("No preciption Found")) {

                                //here to code the part where there is no current preciption......
                            }
                            if(title.equalsIgnoreCase("Unauthenticated")){

                                SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();
                                Intent intent = new Intent(PatientHomepageActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                            if (message.equalsIgnoreCase("No Patient Found")) {

                                new AlertDialog.Builder(PatientHomepageActivity.this)
                                        .setTitle(title)
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .show();
                            }
                        } catch (Exception e) {

                            new AlertDialog.Builder(PatientHomepageActivity.this)
                                    .setTitle("Error")
                                    .setMessage("Unexpected error: " + response.code())
                                    .setPositiveButton("OK", null)
                                    .show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<CurrentPreciptionResponse> call, Throwable t) {

                    Toast.makeText(PatientHomepageActivity.this, "No Internet Connection, Please Connect to Internet", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception e){
        }
    }
}
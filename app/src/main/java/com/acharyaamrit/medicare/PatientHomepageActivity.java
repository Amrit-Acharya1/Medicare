package com.acharyaamrit.medicare;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.response.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.response.RoutineMedicineResponse;
import com.acharyaamrit.medicare.model.response.UserResponse;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientHomepageActivity extends AppCompatActivity {

    private LottieAnimationView lottieAnimationView;
    private final AtomicInteger pendingApiCalls = new AtomicInteger(2);

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_homepage);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        lottieAnimationView = findViewById(R.id.loading_lottie);

        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            navigateToLogin();
            return;
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            pendingApiCalls.set(2);
            fetchCurrentPrescription(token);
            fetchRoutineMedicine(token);
            runOnUiThread(this::loadHomeFragment);
            swipeRefreshLayout.setRefreshing(false);
        });

        // Start both API calls
        fetchCurrentPrescription(token);
        fetchRoutineMedicine(token);
    }

    /**
     * Called when an API call completes (success or failure)
     * Initializes UI when all calls are done
     */
    private synchronized void onApiCallComplete() {
        int remaining = pendingApiCalls.decrementAndGet();

        if (remaining == 0 && !isInitialized) {
            isInitialized = true;
            initializeUI();
        }
    }

    /**
     * Initialize UI elements and fragment navigation
     */
    private void initializeUI() {
        runOnUiThread(() -> {
            lottieAnimationView.setVisibility(GONE);

            // Set initial fragment
            loadHomeFragment();

            // Setup bottom navigation
            setupBottomNavigation();
        });
    }

    /**
     * Setup bottom navigation click listeners
     */
    private void setupBottomNavigation() {
        findViewById(R.id.home_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.home_button_background);
            loadHomeFragment();
        });

        findViewById(R.id.qr_button).setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(R.layout.item_bottom_sheet_qr);
            bottomSheetDialog.show();
        });

        findViewById(R.id.medicine_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.medicine_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MedicineFragment())
                    .commit();
        });

        findViewById(R.id.profile_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.profile_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileFragment())
                    .commit();
        });
    }

    /**
     * Load home fragment and set as selected
     */
    private void loadHomeFragment() {
        setSelectedBackground(R.id.home_button_background);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();
    }

    /**
     * Set selected bottom navigation background
     */
    private void setSelectedBackground(int selectedId) {
        // Reset all backgrounds
        findViewById(R.id.home_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.blue));
        findViewById(R.id.medicine_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.blue));
        findViewById(R.id.profile_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.blue));

        // Set selected background
        findViewById(selectedId)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));
    }

    /**
     * Fetch current prescription from API
     */
    private void fetchCurrentPrescription(String token) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Call<CurrentPreciptionResponse> call = apiService.getCurrentPreciption("Bearer " + token);
            call.enqueue(new Callback<CurrentPreciptionResponse>() {
                @Override
                public void onResponse(Call<CurrentPreciptionResponse> call, Response<CurrentPreciptionResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        handlePrescriptionSuccess(response.body(), dbHelper);
                    } else {
                        handlePrescriptionError(response);
                    }
                    onApiCallComplete();
                }

                @Override
                public void onFailure(Call<CurrentPreciptionResponse> call, Throwable t) {
                    showToast("No Internet Connection, Please Connect to Internet");
                    onApiCallComplete();
                }
            });
        } catch (Exception e) {
            onApiCallComplete();
        }
    }

    /**
     * Handle successful prescription response
     */
    private void handlePrescriptionSuccess(CurrentPreciptionResponse response, DatabaseHelper dbHelper) {
        try {
            CurrentPreciption currentPreciption = response.getCurrentPreciption();

            if (currentPreciption == null) {
                showToast("No prescription data available");
                return;
            }

            // Clear old data
            dbHelper.deleteCurrentPreciption();
            dbHelper.deletePreciptionItem();

            // Insert current prescription
            long prescriptionResult = dbHelper.insertCurrentPreciption(currentPreciption);

            if (prescriptionResult == -1) {
                showToast("Failed to insert prescription");
                return;
            }

            // Insert prescription items with local prescription ID
            int localPrescriptionId = (int) prescriptionResult;
            List<Preciption> preciptionItems = currentPreciption.getPreciptionList();

            if (preciptionItems != null && !preciptionItems.isEmpty()) {
                int successCount = 0;
                for (Preciption item : preciptionItems) {
                    item.setPrescription_relation_id(localPrescriptionId);
                    if (dbHelper.insertPreciptionItem(item) != -1) {
                        successCount++;
                    }
                }

                if (successCount > 0) {
                    showToast(successCount + " prescription items saved successfully");
                }
            }
        } catch (Exception e) {
            showToast("Error processing data: " + e.getMessage());
        }
    }

    /**
     * Handle prescription API error
     */
    private void handlePrescriptionError(Response<CurrentPreciptionResponse> response) {
        try {
            String errorJson = response.errorBody().string();
            Gson gson = new Gson();
            UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);

            String title = errorResponse.getTitle();
            String message = errorResponse.getMessage();


            if (message.equalsIgnoreCase("No preciption Found")){
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.deleteCurrentPreciption();
                dbHelper.deletePreciptionItem();
            }

            if ("Unauthenticated".equalsIgnoreCase(title)) {
                navigateToLogin();
            } else if ("No Patient Found".equalsIgnoreCase(message)) {
                showErrorDialog(title, message);
            }
        } catch (Exception e) {
            showErrorDialog("Error", "Unexpected error: " + response.code());
        }
    }

    /**
     * Fetch routine medicine from API
     */
    private void fetchRoutineMedicine(String token) {
        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            Call<RoutineMedicineResponse> call = apiService.getRoutineMedicine("Bearer " + token);
            call.enqueue(new Callback<RoutineMedicineResponse>() {
                @Override
                public void onResponse(Call<RoutineMedicineResponse> call, Response<RoutineMedicineResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        saveRoutineMedicineToPreferences(response.body());
                    }
                    onApiCallComplete();
                }

                @Override
                public void onFailure(Call<RoutineMedicineResponse> call, Throwable t) {
                    // Silently fail - routine medicine is not critical
                    onApiCallComplete();
                }
            });
        } catch (Exception e) {
            onApiCallComplete();
        }
    }

    /**
     * Save routine medicine to SharedPreferences
     */
    private void saveRoutineMedicineToPreferences(RoutineMedicineResponse response) {
        try {
            Gson gson = new Gson();
            String routineJson = gson.toJson(response);

            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            sharedPreferences.edit()
                    .putString("routine_medicine_data", routineJson)
                    .apply();
        } catch (Exception e) {
            // Log error but don't show to user
        }
    }

    /**
     * Navigate to login activity
     */
    private void navigateToLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Show toast message on UI thread
     */
    private void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    /**
     * Show error dialog on UI thread
     */
    private void showErrorDialog(String title, String message) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show());
    }
}
package com.acharyaamrit.medicare.pharmacy;

import static android.view.View.GONE;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.doctor.CaptureActivityPortrait;
import com.acharyaamrit.medicare.doctor.DoctorHomeFragment;
import com.acharyaamrit.medicare.doctor.DoctorPatientFragment;
import com.acharyaamrit.medicare.doctor.DoctorProfileFragment;
import com.acharyaamrit.medicare.doctor.model.request.OldPrecriptionRequest;
import com.acharyaamrit.medicare.doctor.model.response.SearchPatientResponse;
import com.acharyaamrit.medicare.patient.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.pharmacy.adapter.PharmacySearchPatientAdapter;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.acharyaamrit.medicare.pharmacy.model.PrescriptionPharmacy;
import com.acharyaamrit.medicare.pharmacy.model.response.PrescriptionPharmacyResponse;
import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;

public class PharmacyHomeActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    DatabaseHelper databaseHelper;
    private final AtomicInteger pendingApiCalls = new AtomicInteger(1);
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;

    // Camera Permission Launcher
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private boolean isInitialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pharmacy_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        qrScannerLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        String scannedData = result.getContents();
                        handleScannedQRCode(scannedData);
                    } else {
                        Toast.makeText(PharmacyHomeActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openQRScanner();
                    } else {
                        Toast.makeText(PharmacyHomeActivity.this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                    }
                }
        );


        lottieAnimationView = findViewById(R.id.loading_lottie);

        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        if (token == null) {
            navigateToLogin();
            return;
        }


        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        onApiCallComplete();


        swipeRefreshLayout.setOnRefreshListener(() -> {
            pendingApiCalls.set(2);
            runOnUiThread(this::loadHomeFragment);
            swipeRefreshLayout.setRefreshing(false);
        });





    }

    private synchronized void onApiCallComplete() {
        int remaining = pendingApiCalls.decrementAndGet();

        if (remaining == 0 && !isInitialized) {
            isInitialized = true;
            initializeUI();
        }
    }
    private void initializeUI() {
        runOnUiThread(() -> {
            lottieAnimationView.setVisibility(GONE);

            // Set initial fragment
            loadHomeFragment();

            // Setup bottom navigation
            setupBottomNavigation();

        });
    }

    private void loadHomeFragment() {
        setSelectedBackground(R.id.home_button_background);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new PharmacyHomeFragment())
                .addToBackStack("PharmacyPatient")
                .commit();
    }
    private void navigateToLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupBottomNavigation() {
        findViewById(R.id.home_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.home_button_background);
            loadHomeFragment();
        });

        findViewById(R.id.chat).setOnClickListener(v -> {
        //chat here
        });

        findViewById(R.id.qr_button).setOnClickListener(v -> {
//            setSelectedBackground(R.id.qr_button_background);

            checkCameraPermissionAndScan();
        });

        findViewById(R.id.profile_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.profile_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new PharmacyProfileFragment())
                    .addToBackStack("PharmacyPatient")
                    .commit();
        });
    }

    public void setSelectedBackground(int selectedId) {
        // Reset all backgrounds
        findViewById(R.id.home_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.purple_300));
        findViewById(R.id.qr_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.purple_300));
        findViewById(R.id.profile_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.purple_300));

        // Set selected background
        findViewById(selectedId)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));
    }
    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(PharmacyHomeActivity.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openQRScanner();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
    private void openQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Prescription QR Code");
        options.setCameraId(0); // Use back camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivityPortrait.class);

        qrScannerLauncher.launch(options);
    }

    private void handleScannedQRCode(String scannedData) {

        fetchPrescriptionFromQr(scannedData);


        Toast.makeText(PharmacyHomeActivity.this, "Scanned: " + scannedData, Toast.LENGTH_SHORT).show();
    }

    private void fetchPrescriptionFromQr(String scannedData) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        OldPrecriptionRequest request = new OldPrecriptionRequest(scannedData);
        Call<PrescriptionPharmacyResponse> call = apiService.fetchPrescriptionByQr("Bearer " + token, request);
        call.enqueue(new Callback<PrescriptionPharmacyResponse>() {
            @Override
            public void onResponse(Call<PrescriptionPharmacyResponse> call, retrofit2.Response<PrescriptionPharmacyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {

                        PrescriptionPharmacy currentPreciption = response.body().getpRelationPharmacyList().get(0);
                        String createdAt = currentPreciption.getCreated_at();
                        SimpleDateFormat inputFormat =
                                new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.ENGLISH);

                        Date date = null;
                        try {
                            date = inputFormat.parse(createdAt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
                        SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.ENGLISH);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                        String month = monthFormat.format(date);
                        String day = dayFormat.format(date);
                        String time = timeFormat.format(date);

                        List<Preciption> pres = currentPreciption.getPrescriptionList();
                        Gson gson = new Gson();
                        String presJson = gson.toJson(pres);
                        Intent intent = new Intent(PharmacyHomeActivity.this, PrescriptionDetailsActivity.class);
                        intent.putExtra("prescription_id", String.valueOf(currentPreciption.getId()));
                        intent.putExtra("doctor_name",currentPreciption.getDoctor_name());
                        intent.putExtra("patient_id",currentPreciption.getPatient_id());
                        intent.putExtra("day",day);
                        intent.putExtra("month",month);
                        intent.putExtra("time",time);
                        intent.putExtra("presList", presJson);
                        startActivity(intent);

                       
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PharmacyHomeActivity.this, "Failed to execute", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PharmacyHomeActivity.this, "No Active Prescriptions Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PrescriptionPharmacyResponse> call, Throwable t) {
            }
        });
        




    }


    public void disableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(false);
    }

    public void enableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(true);
    }
}
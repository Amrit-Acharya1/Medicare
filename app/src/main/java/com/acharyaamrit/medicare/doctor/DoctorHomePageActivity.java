package com.acharyaamrit.medicare.doctor;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.airbnb.lottie.LottieAnimationView;

import java.util.concurrent.atomic.AtomicInteger;

public class DoctorHomePageActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    private final AtomicInteger pendingApiCalls = new AtomicInteger(2);

    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isInitialized = false;

    private TextView name, did, specialist;

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_home_page);
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

        onApiCallComplete();
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
    private void loadHomeFragment() {
        setSelectedBackground(R.id.home_button_background);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new DoctorHomeFragment())
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

        findViewById(R.id.camera_button).setOnClickListener(v -> {

//            showQrBottomSheet();
//            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//            bottomSheetDialog.setContentView(R.layout.item_bottom_sheet_qr);
//            bottomSheetDialog.show()

        });

        findViewById(R.id.medicine_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.medicine_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new DoctorPatientFragment())
                    .addToBackStack("DoctorPatient")
                    .commit();
        });

        findViewById(R.id.profile_button).setOnClickListener(v -> {
            setSelectedBackground(R.id.profile_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new DoctorProfileFragment())
                    .addToBackStack("DoctorPatient")
                    .commit();
        });
    }

    public void setSelectedBackground(int selectedId) {
        // Reset all backgrounds
        findViewById(R.id.home_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.green));
        findViewById(R.id.medicine_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.green));
        findViewById(R.id.profile_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.color.green));

        // Set selected background
        findViewById(selectedId)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));
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

    public void disableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(false);
    }

    public void enableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(true);
    }
}
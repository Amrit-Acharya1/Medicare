package com.acharyaamrit.medicare.pharmacy;

import static android.view.View.GONE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

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
import com.acharyaamrit.medicare.doctor.DoctorHomeFragment;
import com.acharyaamrit.medicare.doctor.DoctorPatientFragment;
import com.acharyaamrit.medicare.doctor.DoctorProfileFragment;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.airbnb.lottie.LottieAnimationView;

import java.util.concurrent.atomic.AtomicInteger;

public class PharmacyHomeActivity extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    DatabaseHelper databaseHelper;
    private final AtomicInteger pendingApiCalls = new AtomicInteger(1);
    private SwipeRefreshLayout swipeRefreshLayout;
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
            setSelectedBackground(R.id.qr_button_background);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new PharmacyProfileFragment())
                    .addToBackStack("PharmacyPatient")
                    .commit();
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

    public void disableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(false);
    }

    public void enableSwipeRefresh() {
        swipeRefreshLayout.setEnabled(true);
    }
}
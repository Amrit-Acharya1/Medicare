package com.acharyaamrit.medicare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.acharyaamrit.medicare.onboardingadapters.OnboardingPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class OnBoardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private AppCompatButton btnNext;
    private OnboardingPagerAdapter adapter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if onboarding has been shown before
        prefs = getSharedPreferences("MedicarePrefs", MODE_PRIVATE);
        boolean hasSeenOnboarding = prefs.getBoolean("hasSeenOnboarding", false);
        if (hasSeenOnboarding) {

            //assign the token in new variable
            prefs = getSharedPreferences("user_preference", MODE_PRIVATE);
            String token = prefs.getString("token", null);
            String user_type = prefs.getString("user_type", null);

            if (token == null){
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else {

                //user type nikanla parxa
                if (user_type.equals("3")){
                    startActivity(new Intent(this, PatientHomepageActivity.class));
                    finish();
                }else if (user_type.equals("5")){
                    Toast.makeText(this, "Clinic", Toast.LENGTH_SHORT).show();
                } else if (user_type.equals("2")) {
                    Toast.makeText(this, "Doctor", Toast.LENGTH_SHORT).show();
                } else if (user_type.equals("4")) {
                    Toast.makeText(this, "Pharmacy", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "something went wrong !", Toast.LENGTH_SHORT).show();
                }


            }
            return;
        }

        setContentView(R.layout.activity_on_boarding);

        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);

        List<Integer> layouts = new ArrayList<>();
        layouts.add(R.layout.onboarding_screen1);
        layouts.add(R.layout.onboarding_screen2);
        layouts.add(R.layout.onboarding_screen3);

        adapter = new OnboardingPagerAdapter(this, layouts);
        viewPager.setAdapter(adapter);

        // Set up page change listener to update button text
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == layouts.size() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < layouts.size() - 1) {
                viewPager.setCurrentItem(current + 1);
            } else {
                // Mark onboarding as seen
                prefs.edit().putBoolean("hasSeenOnboarding", true).apply();

                // End of onboarding
                startActivity(new Intent(OnBoardingActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
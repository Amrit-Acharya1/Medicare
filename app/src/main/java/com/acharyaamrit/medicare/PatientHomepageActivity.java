package com.acharyaamrit.medicare;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.Patient;

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

        findViewById(R.id.home_button_background)
                .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();


        findViewById(R.id.home_button).setOnClickListener(v -> {
            findViewById(R.id.home_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));

            findViewById(R.id.medicine_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));

            findViewById(R.id.profile_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
        });

        findViewById(R.id.qr_button).setOnClickListener(v -> {
            // Handle QR button click
        });


        findViewById(R.id.medicine_button).setOnClickListener(v -> {
            findViewById(R.id.medicine_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));
            findViewById(R.id.home_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));
            findViewById(R.id.profile_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new MedicineFragment())
                    .commit();

        });
        findViewById(R.id.profile_button).setOnClickListener(v -> {
            findViewById(R.id.profile_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.drawable.bottom_selected_back));
            findViewById(R.id.home_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));
            findViewById(R.id.medicine_button_background)
                    .setBackground(ContextCompat.getDrawable(this, R.color.blue));
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileFragment())
                    .commit();

        });
    }
}
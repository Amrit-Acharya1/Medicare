package com.acharyaamrit.medicare.clicnic;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.clicnic.model.Clicnic;

public class ClicnicHomePageActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clicnic_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        TextView clicnicText = findViewById(R.id.clicnicText);

        Clicnic clicnic = databaseHelper.getClicnicByToken(token);
        if(clicnic != null) {

            clicnicText.setText(clicnic.getName());
        }
    }
}
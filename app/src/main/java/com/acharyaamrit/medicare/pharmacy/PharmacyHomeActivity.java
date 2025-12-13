package com.acharyaamrit.medicare.pharmacy;

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
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;

public class PharmacyHomeActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pharmacy_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseHelper = new DatabaseHelper(this);
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        TextView pharmacyText = findViewById(R.id.pharmacyText);
       Pharmacy pharmacy =  databaseHelper.getPharmacyByToken(token);

       if(pharmacy != null){
           pharmacyText.setText(pharmacy.getName());
       }




    }
}
package com.acharyaamrit.medicare;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PatientRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());

        findViewById(R.id.register_patient_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationFunction();
            }
        });



    }

    private void validationFunction() {
        String email = ((EditText) findViewById(R.id.patient_registration_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.patient_registration_password)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.patient_registration_confirm_password)).getText().toString();

        if (email.isEmpty()) {
            ((EditText) findViewById(R.id.patient_registration_email)).setError("Email cannot be empty");
        }
        else if (password.isEmpty()) {
            ((EditText) findViewById(R.id.patient_registration_password)).setError("Password cannot be empty");
        }
        else if (confirmPassword.isEmpty()) {
            ((EditText) findViewById(R.id.patient_registration_confirm_password)).setError("Confirm password cannot be empty");
        }
        else if (!password.equals(confirmPassword)) {
            ((EditText) findViewById(R.id.patient_registration_confirm_password)).setError("Password does not match");
        }
        else {
            //backend API ko code here
            finish();
        }

    }
}
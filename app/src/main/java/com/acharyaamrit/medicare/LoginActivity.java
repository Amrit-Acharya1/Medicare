package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
               startActivity(intent);
            }
        });
        findViewById(R.id.register_patient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PatientRegistrationActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.register_clinic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ClinicRegistrationActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationFunction();
            }
        });


    }

    private void validationFunction() {
        String email = ((EditText) findViewById(R.id.email_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_login)).getText().toString();
        if (email.isEmpty()) {
            ((EditText) findViewById(R.id.email_login)).setError("Email cannot be empty");
        }
        else if (password.isEmpty()) {
            ((EditText) findViewById(R.id.password_login)).setError("Password cannot be empty");
        }
        else {
            //backend API ko code here
            finish();
        }

    }
}
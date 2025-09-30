package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OtpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.back_icon).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());

        findViewById(R.id.send_code_btn).setOnClickListener(v -> {
            validationFunction();
        });

    }

    private void validationFunction() {
        String otp = ((EditText) findViewById(R.id.otp_input)).getText().toString();
        if (otp.length() == 6) {
            Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            ((EditText) findViewById(R.id.otp_input)).setError("Invalid OTP");
        }

    }
}
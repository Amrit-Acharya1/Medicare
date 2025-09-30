package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordResetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.back_icon).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.password_reset).setOnClickListener(v -> {
            validationFunction();
        });

    }

    private void validationFunction() {
        String password = ((EditText) findViewById(R.id.new_password)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.conform_password)).getText().toString();

        if (password.equals(confirmPassword)) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            ((EditText) findViewById(R.id.new_password)).setError("Password does not match");
        }

    }
}
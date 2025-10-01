package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.model.OtpRequest;
import com.acharyaamrit.medicare.model.OtpResponse;
import com.acharyaamrit.medicare.model.PasswordResetRequest;
import com.acharyaamrit.medicare.model.PasswordResetResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        String otp = getIntent().getStringExtra("otp");

        findViewById(R.id.back_icon).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.password_reset).setOnClickListener(v -> {
            validationFunction(otp);
        });

    }

    private void validationFunction(String otp) {
        String password = ((EditText) findViewById(R.id.new_password)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.conform_password)).getText().toString();

        if (password.equals(confirmPassword)) {

            resetPassword(otp, password);

//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            finish();
        }
        else {
            ((EditText) findViewById(R.id.new_password)).setError("Password does not match");
        }

    }

    private void resetPassword(String otp, String password) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        PasswordResetRequest request = new PasswordResetRequest(otp, password);

        Call<PasswordResetResponse> call = apiService.resetPassword(request);
        call.enqueue(new Callback<PasswordResetResponse>() {
            @Override
            public void onResponse(Call<PasswordResetResponse> call, Response<PasswordResetResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Success (200)
                    String title = response.body().getTitle();
                    String message = response.body().getMessage();

                    Toast.makeText(PasswordResetActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    try {
                        // ✅ Error case (like 404)
                        String errorJson = response.errorBody().string();

                        // Parse JSON error into your OtpResponse
                        Gson gson = new Gson();
                        OtpResponse errorResponse = gson.fromJson(errorJson, OtpResponse.class);

                        String title = errorResponse.getTitle();
                        String message = errorResponse.getMessage();

                        AlertDialog alertDialog = new AlertDialog.Builder(PasswordResetActivity.this)
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .create();
                        alertDialog.show();

                    } catch (Exception e) {
                        e.printStackTrace();
//                        Toast.makeText(ForgetPasswordActivity.this,
//                                "Unexpected error: " + response.code(),
//                                Toast.LENGTH_SHORT).show();
                        AlertDialog alertDialog = new AlertDialog.Builder(PasswordResetActivity.this)
                                .setTitle("Unexpected error")
                                .setMessage("Unexpected error: " + response.code())
                                .setPositiveButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                }
            }


            @Override
            public void onFailure(Call<PasswordResetResponse> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(PasswordResetActivity.this)
                        .setTitle("Error")
                        .setMessage("Error: " + t.getMessage())
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.show();
            }
        });
    }
}
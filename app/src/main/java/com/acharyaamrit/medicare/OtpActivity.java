package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.model.ApiResponseTitleSuccess;
import com.acharyaamrit.medicare.model.OtpValidateRequest;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        String email = getIntent().getStringExtra("email");

        TextView verificationEmailText = findViewById(R.id.verification_email_text);

        verificationEmailText.setText(email);

        findViewById(R.id.back_icon).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());

        findViewById(R.id.send_code_btn).setOnClickListener(v -> {
            validationFunction(email);
        });

    }

    private void validationFunction(String email) {
        String otp = ((EditText) findViewById(R.id.otp_input)).getText().toString();
        if (otp.length() == 6) {

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            OtpValidateRequest request = new OtpValidateRequest(email, otp);

            Call<ApiResponseTitleSuccess> call = apiService.validateOtp(request);

            call.enqueue(new Callback<ApiResponseTitleSuccess>() {
                @Override
                public void onResponse(Call<ApiResponseTitleSuccess> call, Response<ApiResponseTitleSuccess> response) {
                    if (response.isSuccessful() && response.body() != null){
                        String title = response.body().getTitle();
                        String message = response.body().getMessage();

                        Intent intent = new Intent(getApplicationContext(), PasswordResetActivity.class);

                        intent.putExtra("otp", otp);

                        startActivity(intent);
                        finish();
                        
                    }else {
                        try {
                            try {
                                String errorJson = response.errorBody().string();

                                // Parse JSON error into your OtpResponse
                                Gson gson = new Gson();
                                ApiResponseTitleSuccess errorResponse = gson.fromJson(errorJson, ApiResponseTitleSuccess.class);

                                String title = errorResponse.getTitle();
                                String message = errorResponse.getMessage();

                                AlertDialog alertDialog = new AlertDialog.Builder(OtpActivity.this)
                                        .setTitle(title)
                                        .setMessage(message)
                                        .setPositiveButton("OK", null)
                                        .create();
                                alertDialog.show();


                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(OtpActivity.this,
                                        "Unexpected error: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseTitleSuccess> call, Throwable t) {
                    Toast.makeText(OtpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {
            ((EditText) findViewById(R.id.otp_input)).setError("Invalid OTP");
        }

    }
}
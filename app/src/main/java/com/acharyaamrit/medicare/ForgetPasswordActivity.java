package com.acharyaamrit.medicare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.back_icon).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());
        emailInput = findViewById(R.id.email_input);

        findViewById(R.id.send_code_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               validationFunction();
            }
        });








    }

    private void validationFunction() {
        String email = emailInput.getText().toString().trim();
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            emailInput.requestFocus();
            return;
        }

        otpSend(email);

    }

    private void otpSend(String email) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        OtpRequest request = new OtpRequest(email);

        Call<OtpResponse> call = apiService.sendOtp(request);
        call.enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Success (200)
                    String title = response.body().getTitle();
                    String message = response.body().getMessage();

                    Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(ForgetPasswordActivity.this, OtpActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                } else {
                    try {
                        // ✅ Error case (like 404)
                        String errorJson = response.errorBody().string();

                        // Parse JSON error into your OtpResponse
                        Gson gson = new Gson();
                        OtpResponse errorResponse = gson.fromJson(errorJson, OtpResponse.class);

                        String title = errorResponse.getTitle();
                        String message = errorResponse.getMessage();

                        AlertDialog alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this)
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
                        AlertDialog alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this)
                                .setTitle("Unexpected error")
                                .setMessage("Unexpected error: " + response.code())
                                .setPositiveButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                }
            }


            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(ForgetPasswordActivity.this)
                        .setTitle("Error")
                        .setMessage("Error: " + t.getMessage())
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.show();
            }
        });
    }


}
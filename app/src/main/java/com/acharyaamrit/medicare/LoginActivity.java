package com.acharyaamrit.medicare;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
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
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.UserResponse;
import com.acharyaamrit.medicare.model.UserRequest;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
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
                Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);

                intent.putExtra("userType", "3");

                startActivity(intent);
            }
        });
        findViewById(R.id.register_clinic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, UserRegistrationActivity.class);

                intent.putExtra("userType", "5");

                startActivity(intent);
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging...");
        progressDialog.setCancelable(false);

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                validationFunction();
            }
        });


    }

    private void validationFunction() {
        String email = ((EditText) findViewById(R.id.email_login)).getText().toString();
        String password = ((EditText) findViewById(R.id.password_login)).getText().toString();
        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(
                getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        if (email.isEmpty()) {
            ((EditText) findViewById(R.id.email_login)).setError("Email cannot be empty");
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ((EditText) findViewById(R.id.email_login)).setError("Please enter a valid email address");
            ((EditText) findViewById(R.id.email_login)).requestFocus();
            return;
        }
        else if (password.isEmpty()) {
            ((EditText) findViewById(R.id.password_login)).setError("Password cannot be empty");
        }
        else {
            //backend API ko code here
            login(email, password, deviceId);
        }

    }

    private void login(String email, String password, String deviceId) {
        String fcm_token = getFcmToken();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.loginUser(new UserRequest(email, password, fcm_token, deviceId));

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Success (200)

                    Patient patient = response.body().getUser();
                    String token = response.body().getToken();

                    Intent intentHome = new Intent(LoginActivity.this, HomepageActivity.class);
                    startActivity(intentHome);
                    finish();

                } else {
                    try {
                        // ✅ Error case (like 404)
                        String errorJson = response.errorBody().string();

                        // Parse JSON error into your OtpResponse
                        Gson gson = new Gson();
                        UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);

                        String title = errorResponse.getTitle();
                        String message = errorResponse.getMessage();

                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
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
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("Unexpected error")
                                .setMessage("Unexpected error: " + response.code())
                                .setPositiveButton("OK", null)
                                .create();
                        alertDialog.show();
                    }
                }
            }


            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Error")
                        .setMessage("Error: " + t.getMessage())
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.show();
            }
        });

    }
    private String getFcmToken() {

        return "kri4394032kmfrwelmerwfgj0u";
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss dialog to prevent memory leaks
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
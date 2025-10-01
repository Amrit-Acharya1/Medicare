package com.acharyaamrit.medicare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.acharyaamrit.medicare.model.UserRegisterRequest;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegistrationActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText emailEditText = findViewById(R.id.patient_registration_email);
        TextInputEditText passwordEditText = findViewById(R.id.patient_registration_password);
        TextInputEditText confirmPasswordEditText = findViewById(R.id.patient_registration_confirm_password);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());
        findViewById(R.id.back_to_login).setOnClickListener(v -> finish());

        Intent intent = getIntent();
        int userType = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("userType")));

        TextView topicTextView = findViewById(R.id.topic_text);

        if (userType == 3) {
            topicTextView.setText("Register as patient");
        } else if (userType == 5) {
            topicTextView.setText("Register as clinic");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);

        findViewById(R.id.register_patient_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationFunction(userType);
            }
        });



    }

    private void validationFunction(int userType) {
        String email = ((EditText) findViewById(R.id.patient_registration_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.patient_registration_password)).getText().toString();
        String confirmPassword = ((EditText) findViewById(R.id.patient_registration_confirm_password)).getText().toString();
        String name = ((EditText) findViewById(R.id.patient_registration_name)).getText().toString();

        if (email.isEmpty()) {
            ((EditText) findViewById(R.id.patient_registration_email)).setError("Email cannot be empty");
        } else if (name.isEmpty()){
            ((EditText) findViewById(R.id.patient_registration_name)).setError("Name cannot be empty");
        } else if (password.isEmpty()) {
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
            progressDialog.show();
            userResister( userType, name, email, password);

        }

    }

    private void userResister(int userType, String name, String email, String password) {
        //backend API ko code here

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        UserRegisterRequest request = new UserRegisterRequest(userType, name, email, password);

        Call<ApiResponseTitleSuccess> call = apiService.registerUser(request);

        call.enqueue(new Callback<ApiResponseTitleSuccess>() {
            @Override
            public void onResponse(Call<ApiResponseTitleSuccess> call, Response<ApiResponseTitleSuccess> response) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.isSuccessful() && response.body() != null){

                    String title = response.body().getTitle();
                    String message = response.body().getMessage();



                    finish();
                    
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(UserRegistrationActivity.this)
                            .setTitle("Error")
                            .setMessage("Something went wrong")
                            .setPositiveButton("OK", null)
                            .create();
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseTitleSuccess> call, Throwable t) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(UserRegistrationActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();

                AlertDialog alertDialog = new AlertDialog.Builder(UserRegistrationActivity.this)
                        .setTitle("Unexpected error")
                        .setMessage("Unexpected error: " + t.getMessage())
                        .setPositiveButton("OK", null)
                        .create();
                alertDialog.show();

            }
        });


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
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


            finish();
        }

    }


    private void login(String email, String password, String deviceId) {
        String fcm_token = getFcmToken();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.loginUser(new UserRequest(email, password, fcm_token, deviceId));

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    UserResponse userResponse = response.body();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Something went wrong");
                alertDialog.show();


            }
        });

    }
    private String getFcmToken() {



        return "kri4394032kmfrwelmerwfgj0u";
    }

}
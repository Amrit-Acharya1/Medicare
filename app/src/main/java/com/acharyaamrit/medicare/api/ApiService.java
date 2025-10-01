package com.acharyaamrit.medicare.api;

import com.acharyaamrit.medicare.model.OtpRequest;
import com.acharyaamrit.medicare.model.OtpResponse;
import com.acharyaamrit.medicare.model.OtpValidateRequest;
import com.acharyaamrit.medicare.model.OtpValidateResponse;
import com.acharyaamrit.medicare.model.PasswordResetRequest;
import com.acharyaamrit.medicare.model.PasswordResetResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {

//    @GET("users")
//    Call<List<User>> getUsers();   // Example: GET users
//
//    @GET("users/{id}")
//    Call<User> getUserById(@Path("id") int userId);
//
//    @POST("users")
//    Call<User> createUser(@Body User user);

    @POST("otp")
    Call<OtpResponse> sendOtp(@Body OtpRequest otpRequest);

    @POST("otp/validate")
    Call<OtpValidateResponse> validateOtp(@Body OtpValidateRequest request);

    @PUT("password/reset")
    Call<PasswordResetResponse> resetPassword(@Body PasswordResetRequest request);
}
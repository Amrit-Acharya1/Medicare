package com.acharyaamrit.medicare.api;

import com.acharyaamrit.medicare.model.ApiResponseTitleSuccess;
import com.acharyaamrit.medicare.model.OtpRequest;
import com.acharyaamrit.medicare.model.OtpValidateRequest;
import com.acharyaamrit.medicare.model.PasswordResetRequest;
import com.acharyaamrit.medicare.model.UserRegisterRequest;

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
    Call<ApiResponseTitleSuccess> sendOtp(@Body OtpRequest otpRequest);

    @POST("otp/validate")
    Call<ApiResponseTitleSuccess> validateOtp(@Body OtpValidateRequest request);

    @PUT("password/reset")
    Call<ApiResponseTitleSuccess> resetPassword(@Body PasswordResetRequest request);

    @POST("register/user")
    Call<ApiResponseTitleSuccess> registerUser(@Body UserRegisterRequest request);
}
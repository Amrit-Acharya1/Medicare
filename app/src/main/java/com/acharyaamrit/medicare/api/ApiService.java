package com.acharyaamrit.medicare.api;

import com.acharyaamrit.medicare.model.ApiResponseTitleSuccess;
import com.acharyaamrit.medicare.model.response.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.OtpRequest;
import com.acharyaamrit.medicare.model.OtpValidateRequest;
import com.acharyaamrit.medicare.model.PasswordResetRequest;
import com.acharyaamrit.medicare.model.response.NearbyPharmacyResponse;
import com.acharyaamrit.medicare.model.response.NoticeResponse;
import com.acharyaamrit.medicare.model.response.RoutineMedicineResponse;
import com.acharyaamrit.medicare.model.UserRegisterRequest;
import com.acharyaamrit.medicare.model.UserRequest;
import com.acharyaamrit.medicare.model.response.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
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

    @POST("login")
    Call<UserResponse> loginUser(@Body UserRequest request);
    @GET("fetchCurrentPreciptions")
    Call<CurrentPreciptionResponse> getCurrentPreciption(
            @Header("Authorization") String bearerToken
    );

    @GET("logout")
    Call<UserResponse> logout(
            @Header("Authorization") String bearerToken
    );

    @GET("getRoutineMedicine")
    Call<RoutineMedicineResponse> getRoutineMedicine(
            @Header("Authorization") String bearerToken
    );

    @GET("getPharmacy")
    Call<NearbyPharmacyResponse> getNearbyPharmancy(
            @Header("Authorization") String bearerToken
    );
    @GET("notices")
    Call<NoticeResponse> getNotices(
            @Header("Authorization") String bearerToken
    );
}
package com.acharyaamrit.medicare.common.api;

import com.acharyaamrit.medicare.common.model.ApiResponseTitleSuccess;
import com.acharyaamrit.medicare.doctor.model.request.DoctorUpdateRequest;
import com.acharyaamrit.medicare.doctor.model.request.MedicineRequest;
import com.acharyaamrit.medicare.doctor.model.request.OldPrecriptionRequest;
import com.acharyaamrit.medicare.doctor.model.request.PRelationRequest;
import com.acharyaamrit.medicare.doctor.model.request.PrescriptionRelationRequest;
import com.acharyaamrit.medicare.doctor.model.request.PrescriptionRequest;
import com.acharyaamrit.medicare.doctor.model.request.SearchPatientRequest;
import com.acharyaamrit.medicare.doctor.model.response.MedicineResponse;
import com.acharyaamrit.medicare.doctor.model.response.OldPrescriptionResponse;
import com.acharyaamrit.medicare.doctor.model.response.PRelationResponse;
import com.acharyaamrit.medicare.doctor.model.response.PrescriptionRelationResponse;
import com.acharyaamrit.medicare.doctor.model.response.RecentPatientsResponse;
import com.acharyaamrit.medicare.doctor.model.response.SearchPatientResponse;
import com.acharyaamrit.medicare.patient.model.request.PatientUpdateRequest;
import com.acharyaamrit.medicare.common.model.request.TimelineRequest;
import com.acharyaamrit.medicare.patient.model.request.UserLocationUpdateRequest;
import com.acharyaamrit.medicare.patient.model.response.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.common.model.request.OtpRequest;
import com.acharyaamrit.medicare.common.model.request.OtpValidateRequest;
import com.acharyaamrit.medicare.common.model.request.PasswordResetRequest;
import com.acharyaamrit.medicare.patient.model.response.NearbyPharmacyResponse;
import com.acharyaamrit.medicare.common.model.response.NoticeResponse;
import com.acharyaamrit.medicare.patient.model.response.PatientDocumentResponse;
import com.acharyaamrit.medicare.patient.model.response.RoutineMedicineResponse;
import com.acharyaamrit.medicare.common.model.request.UserRegisterRequest;
import com.acharyaamrit.medicare.common.model.request.UserRequest;
import com.acharyaamrit.medicare.common.model.response.TimelineResponse;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.pharmacy.model.request.PharmacyUpdateRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

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

    @PUT("update/patient")
    Call<UserResponse> updatePatient(
            @Header("Authorization") String bearerToken,
            @Body PatientUpdateRequest request
    );
    @PUT("update/doctor")
    Call<UserResponse> updateDoctor(
            @Header("Authorization") String bearerToken,
            @Body DoctorUpdateRequest request
    );
    @PUT("update/pharmacy")
    Call<UserResponse> updatePharmacy(
            @Header("Authorization") String bearerToken,
            @Body PharmacyUpdateRequest request
    );
    @PUT("user/location")
    Call<UserResponse> updateUserLocation(
            @Header("Authorization") String bearerToken,
            @Body UserLocationUpdateRequest request
    );
    @POST("fetchPatientTimeline")
    Call<TimelineResponse> getPatientTimeline(
            @Header("Authorization") String bearerToken,
            @Body TimelineRequest request
            );

    @GET("document")
    Call<PatientDocumentResponse> getDocument(
            @Header("Authorization") String bearerToken,
            @Query("patient_id") String patientId
    );

    @Multipart
    @POST("document/upload")
    Call<UserResponse> uploadDocument(
            @Header("Authorization") String bearerToken,
            @Part MultipartBody.Part document,
            @Part("preciption_relation_id") RequestBody preciption_relation_id,// file part
            @Part("document_type") RequestBody docType,
            @Part("patient_id")    RequestBody patientId,
            @Part("doctor_id") RequestBody doctorId);


    @GET("recentPatients")
    Call<RecentPatientsResponse> fetchPatientRecent(
            @Header("Authorization") String bearerToken
    );

    @GET("todaycases")
    Call<UserResponse> fetchTodayCase(
            @Header("Authorization") String bearerToken
    );

    @POST("getPatient")
    Call<SearchPatientResponse> searchPatient(
            @Header("Authorization") String bearerToken,
            @Body SearchPatientRequest request
    );

    @POST("medicine/search")
    Call<MedicineResponse> searchMedicine(
            @Header("Authorization") String bearerToken,
            @Body MedicineRequest request
    );

    @POST("storePreciption")
    Call<UserResponse> addPrescription(
            @Header("Authorization") String bearerToken,
            @Body PrescriptionRequest request
    );

    @POST("storePreciptionRelation")
    Call<PrescriptionRelationResponse> addPrescriptionRelation(
            @Header("Authorization") String bearerToken,
            @Body PrescriptionRelationRequest request
    );

    @POST("fetchPreciptionRelationForDoctor")
    Call<PRelationResponse> fetchPrecriptionForPatient(
            @Header("Authorization") String bearerToken,
            @Body PRelationRequest request
    );

    @POST("fetchPrecription")
    Call<OldPrescriptionResponse> fetchOldPrescriptionItem(
            @Header("Authorization") String bearerToken,
            @Body OldPrecriptionRequest request
    );

}
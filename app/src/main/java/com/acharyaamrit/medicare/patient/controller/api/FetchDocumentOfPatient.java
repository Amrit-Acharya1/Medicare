package com.acharyaamrit.medicare.patient.controller.api;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.patient.model.response.PatientDocumentResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchDocumentOfPatient {

    private final String token;
    private final String patient_id;

    public FetchDocumentOfPatient(String token, String patient_id) {
        this.token = token;
        this.patient_id = patient_id;
    }

    public interface DocumentCallback{
        void onSuccess();
        void onFailure(String error);
    }
    public void fetchDocument(Context context, DocumentCallback callback){
        if (context == null) {
            if (callback != null) {
                callback.onFailure("Context is null");
            }
            return;
        }
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<PatientDocumentResponse> call = apiService.getDocument("Bearer " + token, patient_id);
        call.enqueue(new Callback<PatientDocumentResponse>() {
            @Override
            public void onResponse(Call<PatientDocumentResponse> call, Response<PatientDocumentResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    try{
                        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String document = gson.toJson(response.body().getDocuments());
                        editor.putString("document", document);
                        editor.apply();

                        if (callback != null) {
                            callback.onSuccess();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) {
                            callback.onFailure("Database error: " + e.getMessage());
                        }
                    }
                }else {
                    if (callback != null) {
                        callback.onFailure("Server error: " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientDocumentResponse> call, Throwable t) {

            }
        });

    }
}

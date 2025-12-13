package com.acharyaamrit.medicare.common.controller.api;

import android.content.Context;

import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.request.TimelineRequest;
import com.acharyaamrit.medicare.common.model.response.TimelineResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchUserTimelineApi {

    private String token;
    private int patient_id;

    public interface TimelineCallback {
        void onSuccess();
        void onFailure(String error);
    }
    public FetchUserTimelineApi(String token, int patient_id) {
        this.token = token;
        this.patient_id = patient_id;
    }

    public void storeTimeline(Context context, TimelineCallback callback){
        if (context == null) {
            if (callback != null) {
                callback.onFailure("Context is null");
            }
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        TimelineRequest timelineRequest = new TimelineRequest(patient_id);

        Call<TimelineResponse> call = apiService.getPatientTimeline("Bearer " + token, timelineRequest);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        Gson gson = new Gson();
                        String timeline = gson.toJson(response.body());
                        DatabaseHelper dbhelper = new DatabaseHelper(context);
                        dbhelper.deleteTimeline();
                        dbhelper.insertTimeline(dbhelper, timeline);

                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Log the error
                        if (callback != null) {
                            callback.onFailure("Database error: " + e.getMessage());
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure("Server error: " + response.code() + " - " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {
                t.printStackTrace(); // Log the error
                if (callback != null) {
                    callback.onFailure("Network error: " + t.getMessage());
                }
            }
        });
    }
}

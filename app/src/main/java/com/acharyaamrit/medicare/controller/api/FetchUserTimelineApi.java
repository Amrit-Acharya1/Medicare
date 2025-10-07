package com.acharyaamrit.medicare.controller.api;

import android.content.Context;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.response.NearbyPharmacyResponse;
import com.acharyaamrit.medicare.model.response.TimelineResponse;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchUserTimelineApi {

    private String token;
    private int patient_id;

    public FetchUserTimelineApi(String token, int patient_id) {
        this.token = token;
        this.patient_id = patient_id;
    }

    public void storeTimeline(Context context){
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<TimelineResponse> call = apiService.getPatientTimeline("Bearer " + token, patient_id);
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(Call<TimelineResponse> call, Response<TimelineResponse> response) {
                try {

                if (response.isSuccessful() && response.body() != null){

                Gson gson = new Gson();
                String timeline = gson.toJson(response);
                DatabaseHelper dbhelper = new DatabaseHelper(context);
                dbhelper.deleteTimeline();
                dbhelper.insertTimeline(dbhelper, timeline);
                }
                }catch (Exception e){}

            }

            @Override
            public void onFailure(Call<TimelineResponse> call, Throwable t) {

            }
        });
    }
}

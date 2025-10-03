package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.UserResponse;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MedicineFragment extends Fragment {

    public MedicineFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        storeToDatabase(token);

        DatabaseHelper dbHelper2 = new DatabaseHelper(getContext());


        CurrentPreciptionResponse currentPreciptionResponse = dbHelper2.getCurrentPreciptionWithItems();
        CurrentPreciption currentPreciption = currentPreciptionResponse.getCurrentPreciption();

        Toast.makeText(getContext(), currentPreciption.getDoctor_id(), Toast.LENGTH_SHORT).show();

        return view;
    }

    private void storeToDatabase(String token) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<CurrentPreciptionResponse> call = apiService.getCurrentPreciption("Bearer " + token);
        call.enqueue(new Callback<CurrentPreciptionResponse>(){

            @Override
            public void onResponse(Call<CurrentPreciptionResponse> call, Response<CurrentPreciptionResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        CurrentPreciptionResponse prescriptionResponse = response.body();
                        CurrentPreciption currentPreciption = prescriptionResponse.getCurrentPreciption();

                        if (currentPreciption == null) {
                            Toast.makeText(getContext(), "No prescription data available", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Delete old data first
                        dbHelper.deleteCurrentPreciption();
                        dbHelper.deletePreciptionItem();

                        // Insert CurrentPreciption
                        long prescriptionResult = dbHelper.insertCurrentPreciption(currentPreciption);

                        if (prescriptionResult == -1) {
                            Toast.makeText(getContext(), "Failed to insert prescription", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // IMPORTANT: Use the newly inserted prescription ID (local database ID)
                        int localPrescriptionId = (int) prescriptionResult;

                        // Insert Preciption items
                        List<Preciption> preciptionItems = currentPreciption.getPreciptionList();
                        if (preciptionItems != null && !preciptionItems.isEmpty()) {
                            int successCount = 0;

                            for (Preciption item : preciptionItems) {
                                // Set the correct local prescription_relation_id
                                item.setPreciption_relation_id(String.valueOf(localPrescriptionId));

                                long itemResult = dbHelper.insertPreciptionItem(item);
                                if (itemResult != -1) {
                                    successCount++;
                                }
                            }

                            if (successCount > 0) {
                                Toast.makeText(getContext(),
                                        successCount + " prescription items saved successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(),
                                        "Failed to save prescription items",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Prescription saved but no items found",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(getContext(),
                                "Error processing data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorJson = response.errorBody().string();
                        Gson gson = new Gson();
                        UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);

                        String title = errorResponse.getTitle();
                        String message = errorResponse.getMessage();

                        if(message.equalsIgnoreCase("No preciption Found")){

                            //here to code the part where there is no current preciption......
                        }

                        if(message.equalsIgnoreCase("No Patient Found")){

                        new AlertDialog.Builder(getContext())
                                .setTitle(title)
                                .setMessage(message)
                                .setPositiveButton("OK", null)
                                .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new AlertDialog.Builder(getContext())
                                .setTitle("Error")
                                .setMessage("Unexpected error: " + response.code())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CurrentPreciptionResponse> call, Throwable t) {
//                new AlertDialog.Builder(getContext())
//                        .setTitle("Network Error")
//                        .setMessage("Failed to fetch data ):")
//                        .setPositiveButton("OK", null)
//                        .show();
                Toast.makeText(getContext(), "No Internet Connection, Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
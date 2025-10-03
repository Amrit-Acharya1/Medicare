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



        DatabaseHelper dbHelper2 = new DatabaseHelper(getContext());


        CurrentPreciptionResponse currentPreciptionResponse = dbHelper2.getCurrentPreciptionWithItems();
        CurrentPreciption currentPreciption = currentPreciptionResponse.getCurrentPreciption();

        Toast.makeText(getContext(), currentPreciption.getDoctor_id(), Toast.LENGTH_SHORT).show();

        return view;
    }


}
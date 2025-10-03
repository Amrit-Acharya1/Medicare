package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acharyaamrit.medicare.model.RoutineMedicineResponse;
import com.acharyaamrit.medicare.model.patientModel.Medicine;
import com.acharyaamrit.medicare.model.patientModel.RoutineMedicine;
import com.google.gson.Gson;

import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String routineJson = sharedPreferences.getString("routine_medicine_data", null);


        if(routineJson != null){

            Gson gson = new Gson();
            RoutineMedicineResponse routineMedicineResponse = gson.fromJson(routineJson,RoutineMedicineResponse.class);


            RoutineMedicine routineMedicine = routineMedicineResponse.getRoutineMedicine();

            List<Medicine> morningMedicine = routineMedicine.getMorning();

            if (morningMedicine != null && !morningMedicine.isEmpty()) {
                StringBuilder builder = new StringBuilder();
                for (Medicine m : morningMedicine) {
                    builder.append(m.getMedicine_name()).append(", "); // or m.getNote() or any field
                }
                // Remove last comma
                String message = builder.toString();
                if (message.endsWith(", ")) {
                    message = message.substring(0, message.length() - 2);
                }

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }

        }


        return view;






    }
}
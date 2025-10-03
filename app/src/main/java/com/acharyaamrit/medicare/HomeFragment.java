package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acharyaamrit.medicare.adapter.AfternoonAdapter;
import com.acharyaamrit.medicare.adapter.EveningAdapter;
import com.acharyaamrit.medicare.adapter.MorningAdapter;
import com.acharyaamrit.medicare.adapter.NightAdapter;
import com.acharyaamrit.medicare.model.RoutineMedicineResponse;
import com.acharyaamrit.medicare.model.patientModel.Medicine;
import com.acharyaamrit.medicare.model.patientModel.RoutineMedicine;
import com.google.gson.Gson;

import java.util.List;

public class HomeFragment extends Fragment {

    private MorningAdapter morningAdapter;

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

        RecyclerView morningRecycler = view.findViewById(R.id.morningRecycler);
        RecyclerView afternoonRecycler = view.findViewById(R.id.afternoonRecycler);
        RecyclerView eveningRecycler = view.findViewById(R.id.eveningRecycler);
        RecyclerView nightRecycler = view.findViewById(R.id.nightRecycler);



        morningRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        afternoonRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        eveningRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        nightRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        if(routineJson != null){

            Gson gson = new Gson();
            RoutineMedicineResponse routineMedicineResponse = gson.fromJson(routineJson,RoutineMedicineResponse.class);


            RoutineMedicine routineMedicine = routineMedicineResponse.getRoutineMedicine();


            List<Medicine> morningMedicine = routineMedicine.getMorning();

            if (!morningMedicine.isEmpty()){
                morningAdapter = new MorningAdapter(morningMedicine, getContext());
                morningRecycler.setAdapter(morningAdapter);

            }else{
                view.findViewById(R.id.morningLayout).setVisibility(GONE);
            }

            List<Medicine> afternoonMedicine = routineMedicine.getAfternoon();

            if (!afternoonMedicine.isEmpty()){
                AfternoonAdapter afternoonAdapter = new AfternoonAdapter(afternoonMedicine, getContext());
                afternoonRecycler.setAdapter(afternoonAdapter);
            }else{
                view.findViewById(R.id.afternoonLayout).setVisibility(GONE);
            }


            List<Medicine> eveningMedicine = routineMedicine.getEvening();

            if (!eveningMedicine.isEmpty()){
                EveningAdapter eveningAdapter = new EveningAdapter(eveningMedicine, getContext());
                eveningRecycler.setAdapter(eveningAdapter);
            }else{
                view.findViewById(R.id.eveningLayout).setVisibility(GONE);
            }

            List<Medicine> nightMedicine = routineMedicine.getNight();

            if (!nightMedicine.isEmpty()){
                NightAdapter nightAdapter = new NightAdapter(nightMedicine, getContext());
                nightRecycler.setAdapter(nightAdapter);
            }else{
                view.findViewById(R.id.nightLayout).setVisibility(GONE);
            }

            if (morningMedicine.isEmpty() && afternoonMedicine.isEmpty() && eveningMedicine.isEmpty() && nightMedicine.isEmpty()){
                view.findViewById(R.id.no_routine_medicine).setVisibility(VISIBLE);
            }else{
                view.findViewById(R.id.no_routine_medicine).setVisibility(GONE);
            }



        }


        return view;






    }
}
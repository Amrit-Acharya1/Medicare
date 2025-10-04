package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;
import  com.acharyaamrit.medicare.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.UserResponse;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        AppCompatButton btn = view.findViewById(R.id.logout_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                logout(token);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }


        });

       Patient patient = dbHelper.getPatientByToken(token);

       if(patient != null){
           TextView name = view.findViewById(R.id.name);
           TextView address = view.findViewById(R.id.address);
           TextView age = view.findViewById(R.id.age);
           TextView blood_group = view.findViewById(R.id.blood_group);
           TextView gender = view.findViewById(R.id.gender);
           TextView email = view.findViewById(R.id.email);
           TextView contact = view.findViewById(R.id.contact);
           TextView dob = view.findViewById(R.id.dob);
           TextView address2 = view.findViewById(R.id.address2);
           TextView emergency_contact = view.findViewById(R.id.emergency_contact);




           name.setText(patient.getName() !=null  ? patient.getName(): "xxxx");
           address.setText(patient.getAddress() !=null  ? patient.getAddress(): "xxxx");
           age.setText(patient.getDob() != null ? calculateAge(patient.getDob()): "xxxx");
           blood_group.setText(patient.getBlood_group() !=null  ? patient.getBlood_group(): "xxxx");
           gender.setText(patient.getGender() !=null  ? (patient.getGender().equals("1") ?"Male" : "Female"): "xxxx");
           email.setText(patient.getEmail()!=null  ? patient.getEmail(): "xxxx");
           contact.setText(patient.getContact()!=null  ? patient.getContact(): "xxxx");
           dob.setText(patient.getDob()!=null  ? patient.getDob(): "xxxx");
           address2.setText(patient.getAddress()!=null  ? patient.getAddress(): "xxxx");
           emergency_contact.setText(patient.getEmergency_contact()!=null  ? patient.getEmergency_contact(): "xxxxx" );
       }



        return view;
    }

    private void logout(String token) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<UserResponse> call = apiService.logout("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {

                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(getContext(), "No Internet Connection, Please Connect to Internet", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public String calculateAge(String dob) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        try {
            Date birthDate = sdf.parse(dob);

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birthDate);

            Calendar today = Calendar.getInstance();

            int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

            // If today's date is before the birthday this year, subtract one
            if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return String.valueOf(age); // convert int to String
        } catch (ParseException e) {
            e.printStackTrace();
            return "0"; // fallback in case of error
        }
    }

}
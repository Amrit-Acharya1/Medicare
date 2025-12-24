package com.acharyaamrit.medicare.pharmacy;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.doctor.DoctorHomePageActivity;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.request.DoctorUpdateRequest;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.acharyaamrit.medicare.pharmacy.model.request.PharmacyUpdateRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PharmacyProfileFragment extends Fragment {



    public PharmacyProfileFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_pharmacy_profile, container, false);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        AppCompatButton btn = view.findViewById(R.id.logout_button);

        AppCompatButton edit_profile = view.findViewById(R.id.edit_profile);

        SwitchMaterial notificationOn = view.findViewById(R.id.notificationOn);

        notificationOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "turn on", Toast.LENGTH_SHORT).show();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                bottomSheetDialog.setContentView(R.layout.item_edit_profile_pharmacy_layout);

                AppCompatButton saveBtn = bottomSheetDialog.findViewById(R.id.btnSave);
                AppCompatButton btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);


                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                //editText
                EditText fullName = bottomSheetDialog.findViewById(R.id.editTextName);
                EditText phoneNumber = bottomSheetDialog.findViewById(R.id.editTextPhone);
                EditText location = bottomSheetDialog.findViewById(R.id.editTextAddress);
                EditText edPan = bottomSheetDialog.findViewById(R.id.edPan);



                //Populate Data in Edittext
                Pharmacy pharmacy = dbHelper.getPharmacyByToken(token);

                EditText editTextDate = bottomSheetDialog.findViewById(R.id.editTextDOB);

                editTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(),
                                (view, selectedYear, selectedMonth, selectedDay) -> {
                                    String selectedDate = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDay;
                                    editTextDate.setText(selectedDate);
                                },
                                year, month, day
                        );

                        datePickerDialog.show();
                    }
                });

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isValid = true;

                        // Validate Full Name
                        String name = fullName.getText().toString().trim();
                        if (name.isEmpty()) {
                            fullName.setError("Name is required");
                            isValid = false;
                        } else {
                            fullName.setError(null);
                        }

                        // Validate Phone
                        String phone = phoneNumber.getText().toString().trim();
                        if (phone.length() != 10) {
                            phoneNumber.setError("Phone must be exactly 10 digits");
                            isValid = false;
                        } else {
                            phoneNumber.setError(null);
                        }

                        String dob = editTextDate.getText().toString().trim();
                        if (dob.isEmpty()) {
                            editTextDate.setError("Established Date is required");
                            isValid = false;
                        } else {
                            editTextDate.setError(null);
                        }



                        // Validate Address
                        String address = location.getText().toString().trim();
                        if (address.isEmpty()) {
                            location.setError("Address is required");
                            isValid = false;
                        } else {
                            location.setError(null);
                        }

                        // Validate speciality
                        String panText = edPan.getText().toString().trim();
                        if (panText.isEmpty()) {
                            edPan.setError("Pan No is required");
                            isValid = false;
                        } else {
                            edPan.setError(null);
                        }

                        if (isValid) {
                            // Proceed with saving

                            updatePharmacyProfile(name, phone, dob, address, panText);
                            fullName.setText("");
                            phoneNumber.setText("");
                            editTextDate.setText("");
                            location.setText("");
                            edPan.setText("");



                            bottomSheetDialog.dismiss();
                        }
                    }
                });


                if (pharmacy != null){
                    fullName.setText(pharmacy.getName());
                    phoneNumber.setText(pharmacy.getContact());
                    location.setText(pharmacy.getAddress());
                    edPan.setText(pharmacy.getPan_no());
                    editTextDate.setText(pharmacy.getDob());
                }

                bottomSheetDialog.show();
            }
        });
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

        loadPharmacy(token, dbHelper, view);
        return view;
    }

    private void updatePharmacyProfile(String name, String phone, String dob,String address, String panno) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PharmacyUpdateRequest request = new PharmacyUpdateRequest(address, phone,dob, panno);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Call<UserResponse> call = apiService.updatePharmacy("Bearer " + token, request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    try {
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();

                        if (response.body().getMessage().equals("User updated successfully")){
                            DatabaseHelper dbHelperTwo = new DatabaseHelper(getContext());
                            Pharmacy pharmacy = response.body().getPharmacy();

                            dbHelperTwo.insertPharmacy(pharmacy, token);

                            loadPharmacy(token, dbHelperTwo, getView());
                        }


                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }else{
                    try {
                        Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });


    }

    private void loadPharmacy(String token, DatabaseHelper dbHelper, View view){

        Pharmacy pharmacy = dbHelper.getPharmacyByToken(token);
        if(pharmacy != null){
            TextView name = view.findViewById(R.id.name);
            TextView address = view.findViewById(R.id.address);
            TextView pharmacy_id = view.findViewById(R.id.pharmacy_id);
            TextView pan_no = view.findViewById(R.id.pan_no);
            TextView email = view.findViewById(R.id.email);
            TextView contact = view.findViewById(R.id.contact);
            TextView panTxt = view.findViewById(R.id.panTxt);
            TextView address2 = view.findViewById(R.id.address2);
            TextView status = view.findViewById(R.id.status);




            name.setText(pharmacy.getName() !=null  ? pharmacy.getName(): "xxxx");
            address.setText(pharmacy.getAddress() !=null  ? pharmacy.getAddress(): "xxxx");
            pan_no.setText(pharmacy.getPan_no() !=null  ? pharmacy.getPan_no(): "xxxx");
            pharmacy_id.setText(String.valueOf(pharmacy.getPharmacy_id()));
            email.setText(pharmacy.getEmail()!=null  ? pharmacy.getEmail(): "xxxx");
            contact.setText(pharmacy.getContact()!=null  ? pharmacy.getContact(): "xxxx");
            panTxt.setText(pharmacy.getPan_no()!=null  ? pharmacy.getPan_no(): "xxxx");
            address2.setText(pharmacy.getAddress()!=null  ? pharmacy.getAddress(): "xxxx");
            status.setText("Open");
        }
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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof PharmacyHomeActivity) {
            ((PharmacyHomeActivity) getActivity()).disableSwipeRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof PharmacyHomeActivity) {
            ((PharmacyHomeActivity) getActivity()).enableSwipeRefresh();
        }
    }
}
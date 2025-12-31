package com.acharyaamrit.medicare.doctor;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.utils.CustomAlertDialog;
import com.acharyaamrit.medicare.common.utils.FileUtils;
import com.acharyaamrit.medicare.common.utils.ImagePickerBottomSheet;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.request.DoctorUpdateRequest;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorProfileFragment extends Fragment {
    ImageView doctorProfileImage;
    private ImagePickerBottomSheet bottomSheet;
    private String currentImageUrl;

    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        AppCompatButton btn = view.findViewById(R.id.logout_button);

        AppCompatButton edit_profile = view.findViewById(R.id.edit_profile);

        SwitchMaterial notificationOn = view.findViewById(R.id.notificationOn);
        ImageView editProfileImage = view.findViewById(R.id.editDoctorProfileImage);

        doctorProfileImage = view.findViewById(R.id.doctorProfileImage);

        editProfileImage.setOnClickListener(v -> {
            showImagePickerBottomSheet();

        });
        doctorProfileImage.setOnClickListener(v -> {
            showImagePickerBottomSheet();

        });

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
                bottomSheetDialog.setContentView(R.layout.item_edit_profile_doctor_layout);

                AppCompatButton saveBtn = bottomSheetDialog.findViewById(R.id.btnSave);
                AppCompatButton btnCancel = bottomSheetDialog.findViewById(R.id.btnCancel);
                Spinner spinnerGender = bottomSheetDialog.findViewById(R.id.spinnerGender);

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });

                // editText
                EditText fullName = bottomSheetDialog.findViewById(R.id.editTextName);
                EditText phoneNumber = bottomSheetDialog.findViewById(R.id.editTextPhone);
                EditText location = bottomSheetDialog.findViewById(R.id.editTextAddress);
                EditText speciality = bottomSheetDialog.findViewById(R.id.speciality);

                List<String> genderList = new ArrayList<>();
                genderList.add("male");
                genderList.add("female");

                ArrayAdapter genderAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item,
                        genderList);

                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerGender.setAdapter(genderAdapter);

                // Populate Data in Edittext
                Doctor doctor = dbHelper.getDoctorByToken(token);

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
                                year, month, day);

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

                        // Validate DOB
                        String dob = editTextDate.getText().toString().trim();
                        if (dob.isEmpty()) {
                            editTextDate.setError("Date of Birth is required");
                            isValid = false;
                        } else {
                            editTextDate.setError(null);
                        }

                        // Validate Gender
                        if (spinnerGender.getSelectedItem() == null) {
                            Toast.makeText(getContext(), "Gender is required", Toast.LENGTH_SHORT).show();
                            isValid = false;
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
                        String specialityText = speciality.getText().toString().trim();
                        if (specialityText.isEmpty()) {
                            speciality.setError("speciality is required");
                            isValid = false;
                        } else {
                            speciality.setError(null);
                        }

                        if (isValid) {
                            // Proceed with saving

                            String gender = spinnerGender.getSelectedItem().toString();

                            int genderInt = 1;

                            if (gender.equals("male")) {
                                genderInt = 1;
                            } else {
                                genderInt = 0;
                            }
                            updateDoctorProfile(name, phone, dob, genderInt, address, specialityText);
                            fullName.setText("");
                            phoneNumber.setText("");
                            editTextDate.setText("");
                            location.setText("");
                            speciality.setText("");
                            spinnerGender.setSelection(0);

                            bottomSheetDialog.dismiss();
                        }
                    }
                });

                if (doctor != null) {
                    fullName.setText(doctor.getName());
                    phoneNumber.setText(doctor.getContact());
                    location.setText(doctor.getAddress());
                    speciality.setText(doctor.getSpeciality());

                    spinnerGender.setSelection(doctor.getGender().equals("1") ? 0 : 1);
                    editTextDate.setText(doctor.getDob());
                }

                bottomSheetDialog.show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CustomAlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setAlertType(CustomAlertDialog.AlertType.CONFIRMATION)
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

        loadDoctor(token, dbHelper, view);

        return view;
    }

    private void showImagePickerBottomSheet() {
        bottomSheet = ImagePickerBottomSheet.newInstance(currentImageUrl);

        bottomSheet.setOnImageSelectedListener(new ImagePickerBottomSheet.OnImageSelectedListener() {
            @Override
            public void onImageSelected(Uri imageUri) {

            }

            @Override
            public void onUploadClicked(Uri imageUri) {
                uploadProfileImage(imageUri);
            }
        });

        bottomSheet.show(getChildFragmentManager(), "ImagePicker");
    }

    private void uploadProfileImage(Uri imageUri) {
        try {
            // Get file from URI
            File file = FileUtils.getFileFromUri(requireContext(), imageUri);

            if (file == null) {
                bottomSheet.onUploadError("Failed to process image");
                return;
            }

            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference",
                    MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);

            // FIX 1: Get actual MIME type from ContentResolver
            String mimeType = requireContext().getContentResolver().getType(imageUri);
            if (mimeType == null) {
                // Fallback based on file extension
                String extension = getFileExtension(file.getName());
                mimeType = getMimeTypeFromExtension(extension);
            }

            RequestBody fileReqBody = RequestBody.create(
                    MediaType.parse(mimeType),
                    file);

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                    "image", // This must match Laravel's expected field name
                    file.getName(),
                    fileReqBody);

            apiService.updateProfileImage("Bearer " + token, imagePart).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                            Doctor doctor = dbHelper.getDoctorByToken(token);
                            String newUrl = response.body().getDoctor().getImage();

                            doctor.setImage(newUrl);
                            dbHelper.insertDoctor(doctor, token);
                            currentImageUrl = newUrl;

                            Glide.with(requireContext())
                                    .load(doctor.getImage())
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(doctorProfileImage);

                            if (bottomSheet != null) {
                                bottomSheet.onUploadSuccess();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (bottomSheet != null) {
                                bottomSheet.onUploadError("Error saving image");
                            }
                        }
                    } else {
                        // FIX 2: Log the error response for debugging
                        String errorMessage = "Upload failed";
                        try {
                            if (response.errorBody() != null) {
                                errorMessage = response.errorBody().string();
                                android.util.Log.e("UploadError",
                                        "Code: " + response.code() + " Body: " + errorMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (bottomSheet != null) {
                            bottomSheet.onUploadError("Upload failed: " + response.code());
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    android.util.Log.e("UploadError", "Network error: " + t.getMessage());
                    if (bottomSheet != null) {
                        bottomSheet.onUploadError("Network error. Please try again.");
                    }
                }
            });

        } catch (Exception e) {
            android.util.Log.e("UploadError", "Exception: " + e.getMessage());
            if (bottomSheet != null) {
                bottomSheet.onUploadError("Error: " + e.getMessage());
            }
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot >= 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "jpg";
    }

    private String getMimeTypeFromExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "image/jpeg";
        }
    }

    private void loadDoctor(String token, DatabaseHelper dbHelper, View view) {

        Doctor doctor = dbHelper.getDoctorByToken(token);
        if (doctor != null) {
            TextView name = view.findViewById(R.id.name);
            TextView address = view.findViewById(R.id.address);
            TextView age = view.findViewById(R.id.age);
            TextView gender = view.findViewById(R.id.gender);
            TextView email = view.findViewById(R.id.email);
            TextView contact = view.findViewById(R.id.contact);
            TextView dob = view.findViewById(R.id.dob);
            TextView address2 = view.findViewById(R.id.address2);
            TextView speciality = view.findViewById(R.id.specialityTv);

            name.setText(doctor.getName() != null ? doctor.getName() : "xxxx");
            address.setText(doctor.getAddress() != null ? doctor.getAddress() : "xxxx");
            age.setText(doctor.getDob() != null ? calculateAge(doctor.getDob()) : "xxxx");
            gender.setText(doctor.getGender() != null ? (doctor.getGender().equals("1") ? "Male" : "Female") : "xxxx");
            email.setText(doctor.getEmail() != null ? doctor.getEmail() : "xxxx");
            contact.setText(doctor.getContact() != null ? doctor.getContact() : "xxxx");
            dob.setText(doctor.getDob() != null ? doctor.getDob() : "xxxx");
            address2.setText(doctor.getAddress() != null ? doctor.getAddress() : "xxxx");
            speciality.setText(doctor.getSpeciality() != null ? doctor.getSpeciality() : "xxxxx");
            if (doctor.getImage() != null && !doctor.getImage().isEmpty()) {
                currentImageUrl = doctor.getImage();

                Glide.with(requireContext())
                        .load(doctor.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(doctorProfileImage);
            } else {
                currentImageUrl = null;
                doctorProfileImage.setImageResource(R.drawable.logo);
            }
        }
    }

    private void updateDoctorProfile(String name, String phone, String dob, int gender, String address,
            String speciality) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        DoctorUpdateRequest request = new DoctorUpdateRequest(name, address, phone, dob, gender, speciality);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Call<UserResponse> call = apiService.updateDoctor("Bearer " + token, request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();

                        if (response.body().getMessage().equals("User updated successfully")) {
                            DatabaseHelper dbHelperTwo = new DatabaseHelper(getContext());

                            dbHelperTwo.insertDoctor(response.body().getDoctor(), token);

                            loadDoctor(token, dbHelperTwo, getView());
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });

    }

    private void logout(String token) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<UserResponse> call = apiService.logout("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    try {

                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference",
                                MODE_PRIVATE);
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
                Toast.makeText(getContext(), "No Internet Connection, Please Connect to Internet", Toast.LENGTH_SHORT)
                        .show();

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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof DoctorHomePageActivity) {
            ((DoctorHomePageActivity) getActivity()).disableSwipeRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof DoctorHomePageActivity) {
            ((DoctorHomePageActivity) getActivity()).enableSwipeRefresh();
        }
    }

}
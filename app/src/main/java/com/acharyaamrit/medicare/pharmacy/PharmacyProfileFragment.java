package com.acharyaamrit.medicare.pharmacy;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.common.utils.CustomAlertDialog;
import com.acharyaamrit.medicare.common.utils.FileUtils;
import com.acharyaamrit.medicare.common.utils.ImagePickerBottomSheet;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.acharyaamrit.medicare.pharmacy.model.request.PharmacyUpdateRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.io.File;
import java.util.Calendar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PharmacyProfileFragment extends Fragment {

    private ImagePickerBottomSheet bottomSheet;
    ImageView pharmacyProfileImage;
    private String currentImageUrl;
    private LinearLayout privacy_policy, termsAndCondition, helpAndSupport;
    private SwitchMaterial notificationOn;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                notificationOn.setChecked(isGranted);
                if (isGranted) {
                    Toast.makeText(getContext(), "Notifications enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Notifications disabled", Toast.LENGTH_SHORT).show();
                }
            });

    public PharmacyProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pharmacy_profile, container, false);
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        AppCompatButton btn = view.findViewById(R.id.logout_button);

        AppCompatButton edit_profile = view.findViewById(R.id.edit_profile);

        notificationOn = view.findViewById(R.id.notificationOn);
        pharmacyProfileImage = view.findViewById(R.id.pharmacyProfileImage);
        ImageView editPharmacyProfileImage = view.findViewById(R.id.editPharmacyProfileImage);
        privacy_policy = view.findViewById(R.id.privacy_policy);
        termsAndCondition = view.findViewById(R.id.termsAndCondition);
        helpAndSupport = view.findViewById(R.id.helpAndSupport);

        privacy_policy.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://medicare.kritishmovie.xyz/privacypolicy"));
            startActivity(intent);
        });

        termsAndCondition.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://medicare.kritishmovie.xyz/termsandconditions"));
            startActivity(intent);
        });

        helpAndSupport.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://medicare.kritishmovie.xyz/helpandsupport"));
            startActivity(intent);
        });

        editPharmacyProfileImage.setOnClickListener(v -> {
            showImagePickerBottomSheet();

        });
        pharmacyProfileImage.setOnClickListener(v -> {
            showImagePickerBottomSheet();

        });

        updateSwitchState();
        notificationOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;

            if (isChecked) {
                requestNotificationPermission();
            } else {
                openNotificationSettings();
                Toast.makeText(getContext(), "Please disable notifications in settings", Toast.LENGTH_SHORT).show();
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

                // editText
                EditText fullName = bottomSheetDialog.findViewById(R.id.editTextName);
                EditText phoneNumber = bottomSheetDialog.findViewById(R.id.editTextPhone);
                EditText location = bottomSheetDialog.findViewById(R.id.editTextAddress);
                EditText edPan = bottomSheetDialog.findViewById(R.id.edPan);

                // Populate Data in Edittext
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

                if (pharmacy != null) {
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

        loadPharmacy(token, dbHelper, view);
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
                    "image",
                    file.getName(),
                    fileReqBody);

            apiService.updateProfileImage("Bearer " + token, imagePart).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                            Pharmacy pharmacy = dbHelper.getPharmacyByToken(token);
                            String newUrl = response.body().getPharmacy().getImage();
                            pharmacy.setImage(newUrl);
                            dbHelper.insertPharmacy(pharmacy, token);
                            currentImageUrl = newUrl;

                            Glide.with(requireContext())
                                    .load(pharmacy.getImage())
                                    .placeholder(R.drawable.logo)
                                    .error(R.drawable.logo)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(pharmacyProfileImage);

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

    private void updatePharmacyProfile(String name, String phone, String dob, String address, String panno) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        PharmacyUpdateRequest request = new PharmacyUpdateRequest(address, phone, dob, panno);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Call<UserResponse> call = apiService.updatePharmacy("Bearer " + token, request);

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();

                        if (response.body().getMessage().equals("User updated successfully")) {
                            DatabaseHelper dbHelperTwo = new DatabaseHelper(getContext());
                            Pharmacy pharmacy = response.body().getPharmacy();

                            dbHelperTwo.insertPharmacy(pharmacy, token);

                            loadPharmacy(token, dbHelperTwo, getView());
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

    private void loadPharmacy(String token, DatabaseHelper dbHelper, View view) {

        Pharmacy pharmacy = dbHelper.getPharmacyByToken(token);
        if (pharmacy != null) {
            TextView name = view.findViewById(R.id.name);
            TextView address = view.findViewById(R.id.address);
            TextView pharmacy_id = view.findViewById(R.id.pharmacy_id);
            TextView pan_no = view.findViewById(R.id.pan_no);
            TextView email = view.findViewById(R.id.email);
            TextView contact = view.findViewById(R.id.contact);
            TextView panTxt = view.findViewById(R.id.panTxt);
            TextView address2 = view.findViewById(R.id.address2);
            TextView status = view.findViewById(R.id.status);

            name.setText(pharmacy.getName() != null ? pharmacy.getName() : "xxxx");
            address.setText(pharmacy.getAddress() != null ? pharmacy.getAddress() : "xxxx");
            pan_no.setText(pharmacy.getPan_no() != null ? pharmacy.getPan_no() : "xxxx");
            pharmacy_id.setText(String.valueOf(pharmacy.getPharmacy_id()));
            email.setText(pharmacy.getEmail() != null ? pharmacy.getEmail() : "xxxx");
            contact.setText(pharmacy.getContact() != null ? pharmacy.getContact() : "xxxx");
            panTxt.setText(pharmacy.getPan_no() != null ? pharmacy.getPan_no() : "xxxx");
            address2.setText(pharmacy.getAddress() != null ? pharmacy.getAddress() : "xxxx");
            status.setText("Open");

            if (pharmacy.getImage() != null && !pharmacy.getImage().isEmpty()) {
                currentImageUrl = pharmacy.getImage();
                Glide.with(requireContext())
                        .load(pharmacy.getImage())
                        .placeholder(R.drawable.logo)
                        .error(R.drawable.logo)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(pharmacyProfileImage);
            } else {
                currentImageUrl = null;
                pharmacyProfileImage.setImageResource(R.drawable.logo);
            }

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

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof PharmacyHomeActivity) {
            ((PharmacyHomeActivity) getActivity()).disableSwipeRefresh();
        }
        updateSwitchState();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof PharmacyHomeActivity) {
            ((PharmacyHomeActivity) getActivity()).enableSwipeRefresh();
        }
    }
    private void updateSwitchState() {
        boolean isEnabled = areNotificationsEnabled();
        notificationOn.setChecked(isEnabled);
    }
    private boolean areNotificationsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
        } else {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
            return notificationManager.areNotificationsEnabled();
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionRationale();
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                notificationOn.setChecked(true);
            }
        } else {
            if (!areNotificationsEnabled()) {
                openNotificationSettings();
                notificationOn.setChecked(false);
            }
        }
    }
    private void showPermissionRationale() {


        new CustomAlertDialog.Builder(getContext())
                .setTitle("Notification Permission Required")
                .setMessage("Please enable notifications in settings to receive important updates.")
                .setAlertType(CustomAlertDialog.AlertType.CONFIRMATION)
                .setPositiveButton("Open Settings", (dialog, which) -> {
                    openNotificationSettings();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    notificationOn.setChecked(false);
                })
                .show();
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().getPackageName());
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
        }
        startActivity(intent);
    }
}
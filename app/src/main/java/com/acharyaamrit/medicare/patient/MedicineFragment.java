package com.acharyaamrit.medicare.patient;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.common.NotificationActivity;
import com.acharyaamrit.medicare.patient.model.request.PatientDocumentRequest;
import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.CurrentPrescriptionAdapter;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.NearbyPharmacyAdapter;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.UserDocumentAdapter;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.patient.controller.api.FetchDocumentOfPatient;
import com.acharyaamrit.medicare.common.controller.api.UploadDocumentOfPatient;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.acharyaamrit.medicare.patient.model.patientModel.PatientDocument;
import com.acharyaamrit.medicare.patient.model.patientModel.PharmacyMap;
import com.acharyaamrit.medicare.patient.model.response.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.patient.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.patient.model.response.NearbyPharmacyResponse;
import com.acharyaamrit.medicare.common.utils.ImageCompressor;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineFragment extends Fragment {

    // UI Components
    private TextView doctor_name, totalPrice, name, pid, viewAll;
    private LottieAnimationView lottieAnimationView;
    private ConstraintLayout notificationButton;
    private LinearLayout cameraView;
    private RecyclerView userDocumentRecycler;
    private ProgressDialog uploadProgressDialog;

    // Data
    private FetchDocumentOfPatient fetchDocumentOfPatient;
    private Patient currentPatient;
    private String token;
    private File photoFile;
    private String selectedDocType = "prescription";
    private String selectedDoctorId = null;

    // Constants
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_STORAGE = 1002;
    private static final int REQUEST_PICK_IMAGE = 1003;

    public MedicineFragment() {
        // Required empty public constructor
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);

        initializeViews(view);
        initializeData(view);
        setupRecyclerViews(view);
        setupClickListeners();
        loadDocuments();

        return view;
    }

    private void initializeViews(View view) {
        doctor_name = view.findViewById(R.id.doctor_name);
//        totalPrice = view.findViewById(R.id.totalPrice);
        name = view.findViewById(R.id.name);
        pid = view.findViewById(R.id.pid);
        notificationButton = view.findViewById(R.id.notificationButton);
        cameraView = view.findViewById(R.id.camera);
        lottieAnimationView = view.findViewById(R.id.loading_map);
        userDocumentRecycler = view.findViewById(R.id.documentRecycler);
        viewAll = view.findViewById(R.id.tvViewAll);

        // Initialize progress dialog
        uploadProgressDialog = new ProgressDialog(getContext());
        uploadProgressDialog.setTitle("Uploading Document");
        uploadProgressDialog.setMessage("Please wait...");
        uploadProgressDialog.setCancelable(false);
    }

    private void initializeData(View view) {  // ✅ Accept view parameter
        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences("user_preference", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(getContext(), "Session expired. Please login again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        currentPatient = dbHelper.getPatientByToken(token);

        if (currentPatient != null) {
            name.setText(currentPatient.getName());
            pid.setText("PID: " + currentPatient.getPatient_id());

            // ✅ Use the view parameter instead of getView()
            TextView icontext = view.findViewById(R.id.iconText);
            if (icontext != null) {
                icontext.setText(String.valueOf(currentPatient.getName().charAt(0)));
            }
        }
    }

    private void setupRecyclerViews(View view) {
        // Current Prescription RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.currentPrescription_medicine);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadCurrentPrescription(recyclerView, view);

        // Nearby Pharmacy RecyclerView
        RecyclerView nearby_location_patient = view.findViewById(R.id.nearby_location_patient);
        nearby_location_patient.setVisibility(GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false);
        nearby_location_patient.setLayoutManager(layoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(nearby_location_patient);
        fetchNearbyPharmacyFromApi(token, nearby_location_patient);
    }

    private void setupClickListeners() {
        cameraView.setOnClickListener(v -> showDocumentUploadOptions());

        notificationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            intent.putExtra("token", token);
            startActivity(intent);
        });

        viewAll.setOnClickListener(v ->{
            DetailDocumentFragment docFragment = new DetailDocumentFragment();


            Bundle args = new Bundle();
            args.putString("patientId", String.valueOf(currentPatient.getPatient_id()));   // ← replace with your key
            args.putString("token", token);
            docFragment.setArguments(args);


            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainer, docFragment, "DOC_FRAGMENT")
                    .addToBackStack(null)
                    .commit();
        });


    }

    /**
     * Show bottom sheet with document upload options
     */
    private void showDocumentUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(
                R.layout.bottom_sheet_upload_document, null);

        LinearLayout takePhotoLayout = bottomSheetView.findViewById(R.id.takePhotoLayout);
        LinearLayout chooseGalleryLayout = bottomSheetView.findViewById(R.id.chooseGalleryLayout);

        takePhotoLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(true);
        });

        chooseGalleryLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(false);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Show dialog to select document type
     */
    private void showDocumentTypeDialog(boolean isCamera) {
        String[] documentTypes = {
                "Prescription",
                "Lab Report",
                "Medical Certificate",
                "X-Ray/Scan",
                "Other"
        };

        String[] documentTypeValues = {
                "prescription",
                "lab_report",
                "medical_certificate",
                "scan",
                "other"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Document Type")
                .setItems(documentTypes, (dialog, which) -> {
                    selectedDocType = documentTypeValues[which];
                    if (isCamera) {
                        checkCameraPermissionAndOpen();
                    } else {
                        openGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Check camera permission and open camera
     */
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_STORAGE);
                return;
            }
        }

        dispatchTakePictureIntent();
    }

    /**
     * Open gallery to pick image
     */
    private void openGallery() {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");

        if (pickPhotoIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_IMAGE);
        } else {
            Toast.makeText(getContext(), "No gallery app found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Launch camera intent
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) == null) {
            Toast.makeText(getContext(), "No camera app found", Toast.LENGTH_SHORT).show();
            return;
        }

        photoFile = createImageFile();
        if (photoFile == null) {
            Toast.makeText(getContext(), "Cannot create file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    photoFile
            );


            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(takePictureIntent, REQUEST_CAMERA);

        } catch (IllegalArgumentException e) {
            Toast.makeText(getContext(),
                    "Error: " + e.getMessage() + "\nPlease check FileProvider configuration",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create image file for camera
     */
    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            String imageFileName = "MEDICARE_" + timeStamp + "_";

            // Use external files directory (app-specific, no permission needed on Android 10+)
            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Create directory if it doesn't exist
            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            return imageFile;

        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating image file: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkCameraPermissionAndOpen();
        } else if (requestCode == REQUEST_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CAMERA && photoFile != null && photoFile.exists()) {
            showPreviewAndUpload(photoFile);
        } else if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            File imageFile = ImageCompressor.getFileFromUri(requireContext(), selectedImageUri);
            if (imageFile != null) {
                showPreviewAndUpload(imageFile);
            } else {
                Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Show preview dialog and confirm upload
     */
    private void showPreviewAndUpload(File imageFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_image_preview, null);

        ImageView previewImage = dialogView.findViewById(R.id.previewImage);
        TextView tvDocType = dialogView.findViewById(R.id.tvDocumentType);
        TextView tvFileSize = dialogView.findViewById(R.id.tvFileSize);

        // Show original file size
        double fileSizeKB = imageFile.length() / 1024.0;
        tvFileSize.setText(String.format(Locale.getDefault(), "Size: %.2f KB", fileSizeKB));
        tvDocType.setText("Type: " + selectedDocType);

        // Load image
        Uri imageUri = Uri.fromFile(imageFile);
        previewImage.setImageURI(imageUri);

        builder.setView(dialogView)
                .setTitle("Confirm Upload")
                .setPositiveButton("Upload", (dialog, which) -> {
                    // Compress image before upload
                    File compressedFile = ImageCompressor.compressImage(
                            requireContext(), imageFile, selectedDocType);

                    if (compressedFile != null) {
                        uploadDocument(compressedFile);
                    } else {
                        uploadDocument(imageFile); // Fallback to original
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Delete temporary file
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                })
                .show();
    }

    /**
     * Upload document to server
     */
    private void uploadDocument(File imageFile) {
        if (currentPatient == null) {
            Toast.makeText(getContext(), "Patient information not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        uploadProgressDialog.show();

        // Get doctor ID if available (you can implement doctor selection)
        String doctorId = selectedDoctorId != null ? selectedDoctorId : null;

        PatientDocumentRequest request = new PatientDocumentRequest(
                selectedDocType,
                String.valueOf(currentPatient.getPatient_id()),
                doctorId,
                imageFile
        );

        UploadDocumentOfPatient uploadDocument = new UploadDocumentOfPatient(token, request);
        uploadDocument.uploadDocument(requireContext(),
                new UploadDocumentOfPatient.DocumentCallback() {
                    @Override
                    public void onSuccess() {
                        if (isAdded() && getActivity() != null) {
                            requireActivity().runOnUiThread(() -> {
                                uploadProgressDialog.dismiss();
                                Toast.makeText(requireContext(),
                                        "Document uploaded successfully", Toast.LENGTH_LONG).show();

                                // Refresh document list
                                loadDocuments();

                                // Clean up temporary file
                                if (imageFile.exists()) {
                                    imageFile.delete();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        if (isAdded() && getActivity() != null) {
                            requireActivity().runOnUiThread(() -> {
                                uploadProgressDialog.dismiss();
                                Toast.makeText(requireContext(),
                                        "Upload failed: " + error, Toast.LENGTH_LONG).show();
                            });
                        }
                    }
                });
    }

    /**
     * Load and display documents
     */
    private void loadDocuments() {
        if (currentPatient == null) return;

        SharedPreferences sharedPreferences = requireContext()
                .getSharedPreferences("user_preference", MODE_PRIVATE);

        fetchDocumentOfPatient = new FetchDocumentOfPatient(
                token, String.valueOf(currentPatient.getPatient_id()));

        fetchDocumentOfPatient.fetchDocument(getContext(),
                new FetchDocumentOfPatient.DocumentCallback() {
                    @Override
                    public void onSuccess() {
                        String json = sharedPreferences.getString("document", null);
                        if (json == null) return;

                        Type listType = new TypeToken<List<PatientDocument>>(){}.getType();
                        List<PatientDocument> docs = new Gson().fromJson(json, listType);

                        if (docs != null && !docs.isEmpty()) {
                            userDocumentRecycler.setLayoutManager(
                                    new LinearLayoutManager(getContext()));
                            UserDocumentAdapter adapter = new UserDocumentAdapter(docs, getContext(),false);

                            adapter.setOnDocumentClickListener((document, position) -> {
                                String url = document.getDocument_url();
                                if (url != null && !url.isEmpty()) {
                                    openDocument(url);
                                }
                            });

                            userDocumentRecycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to load documents",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadCurrentPrescription(RecyclerView recyclerView, View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        CurrentPreciptionResponse response = dbHelper.getCurrentPreciptionWithItems();
        CurrentPreciption currentPreciption = response.getCurrentPreciption();

        if (currentPreciption != null) {
            List<Preciption> preciptionList = currentPreciption.getPreciptionList();
            CurrentPrescriptionAdapter adapter = new CurrentPrescriptionAdapter(
                    preciptionList, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            doctor_name.setText(String.format("Prescribed by Dr. %s",
                    currentPreciption.getDoctor_name()));

//            double totalPriceValue = 0;
//            for (Preciption preciption : preciptionList) {
//                totalPriceValue += Double.parseDouble(preciption.getPrice());
//            }
//            totalPrice.setText(String.format("Rs. %.2f", totalPriceValue));
        } else {
            view.findViewById(R.id.current_prescription).setVisibility(GONE);
        }
    }

    private void fetchNearbyPharmacyFromApi(String token, RecyclerView nearby_location_patient) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<NearbyPharmacyResponse> call = apiService.getNearbyPharmancy("Bearer " + token);

        call.enqueue(new Callback<NearbyPharmacyResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<NearbyPharmacyResponse> call,
                                   Response<NearbyPharmacyResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<PharmacyMap> nearbyPharmacies = response.body().getPharmacy_map();
                        NearbyPharmacyAdapter adapter = new NearbyPharmacyAdapter(
                                nearbyPharmacies, getContext());
                        nearby_location_patient.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        nearby_location_patient.setVisibility(VISIBLE);
                        lottieAnimationView.setVisibility(GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NearbyPharmacyResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(),
                            "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof PatientHomepageActivity) {
            ((PatientHomepageActivity) getActivity()).disableSwipeRefresh();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof PatientHomepageActivity) {
            ((PatientHomepageActivity) getActivity()).enableSwipeRefresh();
        }
    }

    // Document opening methods (keep your existing implementation)
    private void openDocument(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(getContext(), "Invalid document URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isImageUrl(url)) {
            openImageViewer(url);
        } else if (isPdfUrl(url)) {
            openPdfViewer(url);
        } else {
            openInBrowser(url);
        }
    }

    private void openImageViewer(String imageUrl) {
        Intent intent = new Intent(getContext(), ImageViewerActivity.class);
        intent.putExtra("IMAGE_URL", imageUrl);
        startActivity(intent);
    }

    private void openPdfViewer(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No PDF viewer installed",
                    Toast.LENGTH_SHORT).show();
            openInBrowser(pdfUrl);
        }
    }

    private void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No browser found", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isImageUrl(String url) {
        String lower = url.toLowerCase(Locale.getDefault());
        return lower.matches(".*\\.(jpg|jpeg|png|gif|webp|bmp)(\\?.*)?$");
    }

    private boolean isPdfUrl(String url) {
        String lower = url.toLowerCase(Locale.getDefault());
        return lower.matches(".*\\.pdf(\\?.*)?$");
    }
}
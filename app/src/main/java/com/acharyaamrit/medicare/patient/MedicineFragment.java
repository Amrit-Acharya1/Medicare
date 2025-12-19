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
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private LinearLayout cameraView, document;
    private RecyclerView userDocumentRecycler;
    private ProgressDialog uploadProgressDialog;

    // Data
    private FetchDocumentOfPatient fetchDocumentOfPatient;
    private Patient currentPatient;
    private String token;
    private File photoFile;
    private String selectedDocType = "prescription";
    private final String selectedDoctorId = null;

    // ML Kit Document Scanner (v16.1.0)
    private GmsDocumentScanner documentScanner;
    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;

    // Constants
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_STORAGE = 1002;
    private static final int REQUEST_PICK_IMAGE = 1003;

    public MedicineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeDocumentScanner();
        setupScannerLauncher();
    }

    /**
     * Initialize ML Kit Document Scanner with optimized settings (v16.1.0)
     * Key difference: v16.1.0 uses slightly different option builder methods
     */
    private void initializeDocumentScanner() {
        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)  // Allow importing from gallery
                .setPageLimit(5)                 // Limit pages for performance
                .setResultFormats(
                        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                        GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                )
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL) // Full mode with UI
                .build();

        documentScanner = GmsDocumentScanning.getClient(options);
    }

    /**
     * Setup ActivityResultLauncher for document scanner
     * Same implementation for both v16.1.0 and v17.0.0-beta1
     */
    private void setupScannerLauncher() {
        scannerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            handleScannerResult(data);
                        }
                    } else {
                        Toast.makeText(getContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Handle scanned document result
     * Compatible with v16.1.0
     */
    private void handleScannerResult(Intent data) {
        GmsDocumentScanningResult result =
                GmsDocumentScanningResult.fromActivityResultIntent(data);

        if (result == null) {
            Toast.makeText(getContext(), "Failed to process document", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the first page (v16.1.0 compatible)
        GmsDocumentScanningResult.Page firstPage = result.getPages().get(0);
        Uri imageUri = firstPage.getImageUri();

        // Process the scanned image in background
        processScannedDocument(imageUri);
    }

    /**
     * Process scanned document asynchronously
     * Same for both versions
     */
    private void processScannedDocument(Uri imageUri) {
        new Thread(() -> {
            try {
                // Convert URI to File
                File scannedFile = convertUriToFile(imageUri);

                if (scannedFile != null && scannedFile.exists()) {
                    // Show preview on main thread
                    requireActivity().runOnUiThread(() ->
                            showPreviewAndUpload(scannedFile));
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    "Failed to process scanned document",
                                    Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Convert URI to File efficiently
     * Same implementation for both versions
     */
    private File convertUriToFile(Uri uri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            String fileName = "SCANNED_" + timeStamp + ".jpg";

            File outputDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File outputFile = new File(outputDir, fileName);

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return outputFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
        name = view.findViewById(R.id.name);
        pid = view.findViewById(R.id.pid);
        notificationButton = view.findViewById(R.id.notificationButton);
        cameraView = view.findViewById(R.id.camera);
        lottieAnimationView = view.findViewById(R.id.loading_map);
        userDocumentRecycler = view.findViewById(R.id.documentRecycler);
        viewAll = view.findViewById(R.id.tvViewAll);
        document = view.findViewById(R.id.document);

        // Initialize progress dialog
        uploadProgressDialog = new ProgressDialog(getContext());
        uploadProgressDialog.setTitle("Uploading Document");
        uploadProgressDialog.setMessage("Please wait...");
        uploadProgressDialog.setCancelable(false);
    }

    private void initializeData(View view) {
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

        viewAll.setOnClickListener(v -> {
            DetailDocumentFragment docFragment = new DetailDocumentFragment();

            Bundle args = new Bundle();
            args.putString("patientId", String.valueOf(currentPatient.getPatient_id()));
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
     * Show bottom sheet with document upload options (Updated with Scanner option)
     */
    private void showDocumentUploadOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(
                R.layout.bottom_sheet_upload_document_v2, null);

        LinearLayout scanDocumentLayout = bottomSheetView.findViewById(R.id.scanDocumentLayout);
        LinearLayout takePhotoLayout = bottomSheetView.findViewById(R.id.takePhotoLayout);
        LinearLayout chooseGalleryLayout = bottomSheetView.findViewById(R.id.chooseGalleryLayout);

        // ML Kit Document Scanner (Recommended)
        scanDocumentLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(true, true); // isScanner = true
        });

        // Traditional Camera
        takePhotoLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(true, false); // isCamera = true, isScanner = false
        });

        // Gallery
        chooseGalleryLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(false, false); // neither camera nor scanner
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Show dialog to select document type (Updated)
     */
    private void showDocumentTypeDialog(boolean isCamera, boolean isScanner) {
        String[] documentTypes = {
                "Prescription",
                "Lab Report",
                "X-Ray/Scan",
                "Other"
        };

        String[] documentTypeValues = {
                "prescription",
                "lab_report",
                "scan",
                "other"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Document Type")
                .setItems(documentTypes, (dialog, which) -> {
                    selectedDocType = documentTypeValues[which];

                    if (isScanner) {
                        startDocumentScanner();
                    } else if (isCamera) {
                        checkCameraPermissionAndOpen();
                    } else {
                        openGallery();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Start ML Kit Document Scanner (v16.1.0)
     * Same implementation as v17.0.0-beta1
     */
    private void startDocumentScanner() {
        Task<IntentSender> scannerTask = documentScanner.getStartScanIntent(requireActivity());

        scannerTask.addOnSuccessListener(intentSender -> {
            try {
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                scannerLauncher.launch(request);
            } catch (Exception e) {
                Toast.makeText(getContext(),
                        "Failed to start scanner: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(),
                    "Scanner not available: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            // Fallback to camera
            showDocumentTypeDialog(true, false);
        });
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        startActivityForResult(
                Intent.createChooser(intent, "Select Image"),
                REQUEST_PICK_IMAGE
        );
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

            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }

            File imageFile = File.createTempFile(
                    imageFileName,
                    ".jpg",
                    storageDir
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

        // Show file size
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
                        uploadDocument(imageFile);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
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

        String doctorId = selectedDoctorId;

        PatientDocumentRequest request = new PatientDocumentRequest(
                null,
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

                                loadDocuments();

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

                        Type listType = new TypeToken<List<PatientDocument>>() {
                        }.getType();
                        List<PatientDocument> docs = new Gson().fromJson(json, listType);

                        if (docs != null && !docs.isEmpty()) {
                            document.setVisibility(VISIBLE);
                            if (docs.size() > 5) {
                                viewAll.setVisibility(VISIBLE);
                            } else {
                                viewAll.setVisibility(GONE);
                            }

                            userDocumentRecycler.setLayoutManager(
                                    new LinearLayoutManager(getContext()));
                            UserDocumentAdapter adapter = new UserDocumentAdapter(docs, getContext(), false);

                            adapter.setOnDocumentClickListener((document, position) -> {
                                String url = document.getDocument_url();
                                if (url != null && !url.isEmpty()) {
                                    openDocument(url);
                                }
                            });

                            userDocumentRecycler.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            viewAll.setVisibility(GONE);
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
                } else {
                    lottieAnimationView.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<NearbyPharmacyResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(),
                            "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                lottieAnimationView.setVisibility(GONE);
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
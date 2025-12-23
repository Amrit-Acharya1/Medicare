package com.acharyaamrit.medicare.doctor;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.controller.api.UploadDocumentOfPatient;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.utils.ImageCompressor;
import com.acharyaamrit.medicare.doctor.adapter.MedicineSearchAdapter;
import com.acharyaamrit.medicare.doctor.adapter.PrescriptionRelationForPatientAdapter;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.Medicine;
import com.acharyaamrit.medicare.doctor.model.request.MedicineRequest;
import com.acharyaamrit.medicare.doctor.model.request.PRelationRequest;
import com.acharyaamrit.medicare.doctor.model.response.MedicineResponse;
import com.acharyaamrit.medicare.doctor.model.response.PRelation;
import com.acharyaamrit.medicare.doctor.model.response.PRelationResponse;
import com.acharyaamrit.medicare.patient.model.request.PatientDocumentRequest;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner;
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning;
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineSearch extends AppCompatActivity {

    private static final int SEARCH_DELAY_MS = 500;
    private static final int MIN_SEARCH_LENGTH = 0;
    private static final int CACHE_MAX_SIZE = 50;
    private static final int ITEMS_PER_PAGE = 30;

    // Document Upload Constants
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_STORAGE = 1002;
    private static final int REQUEST_PICK_IMAGE = 1003;

    private CardView btnBack;
    private TextView tv_name, tv_pid, tvMedicineCount, newPrescription, prescriptionTile;
    private RecyclerView rvMedicines;
    private EditText searchMedicine;
    private NestedScrollView nestedScrollView;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private MedicineSearchAdapter adapter;
    private final Map<String, List<Medicine>> searchCache = new HashMap<>();
    private Call<MedicineResponse> currentCall;
    private String authToken;
    private String token;
    private ApiService apiService;
    private FrameLayout loadingOverlay;
    private FloatingActionButton fab;
    private LinearLayout upload_doc, noMedicine;
    private ProgressBar loadMoreProgress;

    // Pagination state
    private String currentSearchQuery = "";
    private int currentOffset = 0;
    private boolean hasMoreData = false;
    private boolean isLoadingMore = false;
    private int totalCount = 0;

    // Document Upload Variables
    private GmsDocumentScanner documentScanner;
    private ActivityResultLauncher<IntentSenderRequest> scannerLauncher;
    private File photoFile;
    private String selectedDocType = "prescription";
    private ProgressDialog uploadProgressDialog;
    private String patientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeData();
        initializeViews();
        initializeDocumentScanner();
        setupScannerLauncher();

        String name = getIntent().getStringExtra("patient_name");
        patientId = getIntent().getStringExtra("patient_id");

        setupRecyclerView(patientId);
        setupListeners();
        setupScrollListener();
        fetchPrecriptionsForPatient();

        tv_name.setText(name);
        tv_pid.setText("PID: " + patientId);
        noMedicine.setVisibility(VISIBLE);
    }

    private void initializeData() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        authToken = "Bearer " + token;
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btnBack);
        tv_name = findViewById(R.id.tv_name);
        tv_pid = findViewById(R.id.tv_pid);
        searchMedicine = findViewById(R.id.searchMedicine);
        rvMedicines = findViewById(R.id.rvMedicines);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        tvMedicineCount = findViewById(R.id.tvMedicineCount);
        noMedicine = findViewById(R.id.noMedicine);
        prescriptionTile = findViewById(R.id.prescriptionTile);
        fab = findViewById(R.id.fab);
        upload_doc = findViewById(R.id.upload_doc);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        loadMoreProgress = findViewById(R.id.loadMoreProgress);

        // Initialize upload progress dialog
        uploadProgressDialog = new ProgressDialog(this);
        uploadProgressDialog.setTitle("Uploading Document");
        uploadProgressDialog.setMessage("Please wait...");
        uploadProgressDialog.setCancelable(false);
    }

    // ==================== Document Scanner Setup ====================

    /**
     * Initialize ML Kit Document Scanner
     */
    private void initializeDocumentScanner() {
        GmsDocumentScannerOptions options = new GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)
                .setPageLimit(5)
                .setResultFormats(
                        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                        GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                )
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .build();

        documentScanner = GmsDocumentScanning.getClient(options);
    }

    /**
     * Setup ActivityResultLauncher for document scanner
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
                        Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Handle scanned document result
     */
    private void handleScannerResult(Intent data) {
        GmsDocumentScanningResult result =
                GmsDocumentScanningResult.fromActivityResultIntent(data);

        if (result == null) {
            Toast.makeText(this, "Failed to process document", Toast.LENGTH_SHORT).show();
            return;
        }

        GmsDocumentScanningResult.Page firstPage = result.getPages().get(0);
        Uri imageUri = firstPage.getImageUri();

        processScannedDocument(imageUri);
    }

    /**
     * Process scanned document asynchronously
     */
    private void processScannedDocument(Uri imageUri) {
        new Thread(() -> {
            try {
                File scannedFile = convertUriToFile(imageUri);

                if (scannedFile != null && scannedFile.exists()) {
                    runOnUiThread(() -> showPreviewAndUpload(scannedFile));
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Failed to process scanned document",
                                    Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    /**
     * Convert URI to File
     */
    private File convertUriToFile(Uri uri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            String fileName = "SCANNED_" + timeStamp + ".jpg";

            File outputDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File outputFile = new File(outputDir, fileName);

            InputStream inputStream = getContentResolver().openInputStream(uri);
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

    // ==================== Document Upload Methods ====================

    /**
     * Show bottom sheet with document upload options
     */
    private void showDocumentUploadOptions() {
        // Check if prescription relation is selected
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String presId = sharedPreferences.getString("prescriptionRelation_id", null);

        if (presId == null) {
            // Show warning dialog
            new AlertDialog.Builder(this)
                    .setTitle("Select Prescription")
                    .setMessage("Please select a prescription first.")
                    .setPositiveButton("Select", (dialog, which) -> fetchPrecriptionsForPatient())
                    .setNegativeButton("Cancel", null)
                    .show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(
                R.layout.bottom_sheet_upload_document_v2, null);

        LinearLayout scanDocumentLayout = bottomSheetView.findViewById(R.id.scanDocumentLayout);
        LinearLayout takePhotoLayout = bottomSheetView.findViewById(R.id.takePhotoLayout);
        LinearLayout chooseGalleryLayout = bottomSheetView.findViewById(R.id.chooseGalleryLayout);

        // ML Kit Document Scanner
        scanDocumentLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(true, true);
        });

        // Traditional Camera
        takePhotoLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(true, false);
        });

        // Gallery
        chooseGalleryLayout.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showDocumentTypeDialog(false, false);
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    /**
     * Show dialog to select document type
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
     * Start ML Kit Document Scanner
     */
    private void startDocumentScanner() {
        Task<IntentSender> scannerTask = documentScanner.getStartScanIntent(this);

        scannerTask.addOnSuccessListener(intentSender -> {
            try {
                IntentSenderRequest request = new IntentSenderRequest.Builder(intentSender).build();
                scannerLauncher.launch(request);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start scanner: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Scanner not available: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            // Fallback to camera
            showDocumentTypeDialog(true, false);
        });
    }

    /**
     * Check camera permission and open camera
     */
    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            return;
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
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
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_PICK_IMAGE);
    }

    /**
     * Launch camera intent
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            return;
        }

        photoFile = createImageFile();
        if (photoFile == null) {
            Toast.makeText(this, "Cannot create file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri photoURI = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    photoFile
            );

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(takePictureIntent, REQUEST_CAMERA);

        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create image file for camera
     */
    private File createImageFile() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                    .format(new Date());
            String imageFileName = "MEDICARE_DOC_" + timeStamp + "_";

            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            if (storageDir != null && !storageDir.exists()) {
                storageDir.mkdirs();
            }

            return File.createTempFile(imageFileName, ".jpg", storageDir);

        } catch (IOException e) {
            Toast.makeText(this, "Error creating image file: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * Show preview dialog and confirm upload
     */
    private void showPreviewAndUpload(File imageFile) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        // Get prescription relation info
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String presId = sharedPreferences.getString("prescriptionRelation_id", null);

        builder.setView(dialogView)
                .setTitle("Confirm Upload")
                .setMessage("Prescription: " + (presId != null ? "No. " + presId : "New"))
                .setPositiveButton("Upload", (dialog, which) -> {
                    File compressedFile = ImageCompressor.compressImage(
                            this, imageFile, selectedDocType);

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
     * Upload document to server with doctorId and prescriptionRelationId
     */
    private void uploadDocument(File imageFile) {
        if (patientId == null) {
            Toast.makeText(this, "Patient information not found", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadProgressDialog.show();

        // Get doctor ID from database
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        Doctor doctor = databaseHelper.getDoctorByToken(token);
        String doctorId = null;
        if (doctor != null) {
            doctorId = String.valueOf(doctor.getDoctor_id());
        }

        // Get prescription relation ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String prescriptionRelationStr = sharedPreferences.getString("prescriptionRelation_id", null);
        Integer prescriptionRelationId = prescriptionRelationStr != null ? Integer.valueOf(prescriptionRelationStr) : null;

        PatientDocumentRequest request = new PatientDocumentRequest(
                prescriptionRelationId,
                selectedDocType,          // documentType
                patientId,                // patientId (from intent)
                doctorId,                 // doctorId (from database)
                imageFile                 // file
        );

        UploadDocumentOfPatient uploadDocument = new UploadDocumentOfPatient(token, request);
        uploadDocument.uploadDocument(this, new UploadDocumentOfPatient.DocumentCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    uploadProgressDialog.dismiss();
                    Toast.makeText(MedicineSearch.this,
                            "Document uploaded successfully", Toast.LENGTH_LONG).show();

                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    uploadProgressDialog.dismiss();
                    Toast.makeText(MedicineSearch.this,
                            "Upload failed: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
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
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CAMERA && photoFile != null && photoFile.exists()) {
            showPreviewAndUpload(photoFile);
        } else if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            File imageFile = ImageCompressor.getFileFromUri(this, selectedImageUri);
            if (imageFile != null) {
                showPreviewAndUpload(imageFile);
            } else {
                Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void setupRecyclerView(String pid) {
        adapter = new MedicineSearchAdapter(new ArrayList<>(), this, Integer.parseInt(pid));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvMedicines.setLayoutManager(gridLayoutManager);

        rvMedicines.setNestedScrollingEnabled(false);
        rvMedicines.setHasFixedSize(false);
        rvMedicines.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvMedicines.setAdapter(adapter);
        rvMedicines.setItemViewCacheSize(20);
    }

    private void setupScrollListener() {
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

                    if (isLoadingMore || !hasMoreData) {
                        return;
                    }

                    View child = v.getChildAt(0);
                    if (child != null) {
                        int childHeight = child.getHeight();
                        int scrollViewHeight = v.getHeight();
                        int scrollPosition = scrollY + scrollViewHeight;

                        int threshold = 200;

                        if (scrollPosition >= childHeight - threshold) {
                            loadMoreMedicines();
                        }
                    }
                });
    }

    private void showBottomSheet(List<PRelation> prelationList) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(
                R.layout.bottom_sheet_prescription_relation_for_patient, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        newPrescription = bottomSheetView.findViewById(R.id.newPrescription);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.prelationRecycler);

        PrescriptionRelationForPatientAdapter prescriptionRelationForPatientAdapter =
                new PrescriptionRelationForPatientAdapter(prelationList, this, bottomSheetDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(prescriptionRelationForPatientAdapter);

        newPrescription.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("prescriptionRelation_id");
            editor.apply();
            bottomSheetDialog.dismiss();
        });

        

        bottomSheetDialog.setOnDismissListener(dialog -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            String pres_id = sharedPreferences.getString("prescriptionRelation_id", null);
            if (pres_id == null) {
                prescriptionTile.setText("New Prescription");
            } else {
                prescriptionTile.setText("Prescription No: " + pres_id);
            }
        });

        bottomSheetDialog.show();
    }

    private void fetchPrecriptionsForPatient() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int id = Integer.parseInt(getIntent().getStringExtra("patient_id"));
        PRelationRequest pRelationRequest = new PRelationRequest(id);
        Call<PRelationResponse> call = apiService.fetchPrecriptionForPatient("Bearer " + token, pRelationRequest);
        call.enqueue(new Callback<PRelationResponse>() {
            @Override
            public void onResponse(Call<PRelationResponse> call, Response<PRelationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        showBottomSheet(response.body().getpRelationList());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MedicineSearch.this, "Failed to execute", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MedicineSearch.this, "error response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PRelationResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(MedicineSearch.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        // Document upload click listener
        upload_doc.setOnClickListener(v -> showDocumentUploadOptions());

        fab.setOnClickListener(v -> fetchPrecriptionsForPatient());
        btnBack.setOnClickListener(v -> finish());

        searchMedicine.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard();
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                String query = searchMedicine.getText().toString().trim();
                if (query.length() >= MIN_SEARCH_LENGTH) {
                    resetPaginationState();
                    currentSearchQuery = query;

                    noMedicine.setVisibility(GONE);
                    loadingOverlay.setVisibility(VISIBLE);
                    fetchMedicine(query, 0, false);
                }
                return true;
            }
            return false;
        });
    }

    private void resetPaginationState() {
        currentOffset = 0;
        hasMoreData = false;
        isLoadingMore = false;
        totalCount = 0;
        adapter.updateData(new ArrayList<>());
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            ((android.view.inputmethod.InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void loadMoreMedicines() {
        if (isLoadingMore || !hasMoreData || currentSearchQuery.isEmpty()) {
            return;
        }

        isLoadingMore = true;
        showLoadMoreProgress(true);
        fetchMedicine(currentSearchQuery, currentOffset, true);
    }

    private void showLoadMoreProgress(boolean show) {
        if (loadMoreProgress != null) {
            loadMoreProgress.setVisibility(show ? VISIBLE : GONE);
        }
    }

    private void fetchMedicine(String medicine, int offset, boolean isLoadMore) {
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }

        if (medicine.isEmpty()) {
            noMedicine.setVisibility(VISIBLE);
            loadingOverlay.setVisibility(GONE);
            showLoadMoreProgress(false);
            tvMedicineCount.setText("0 items");
            adapter.updateData(new ArrayList<>());
            return;
        }

        MedicineRequest medicineRequest = new MedicineRequest(medicine, offset, ITEMS_PER_PAGE);
        currentCall = apiService.searchMedicine(authToken, medicineRequest);

        currentCall.enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (call.isCanceled()) return;

                loadingOverlay.setVisibility(GONE);
                showLoadMoreProgress(false);
                isLoadingMore = false;

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        MedicineResponse medicineResponse = response.body();
                        List<Medicine> medicines = medicineResponse.getMedicines();
                        hasMoreData = medicineResponse.isHasMore();
                        currentOffset = medicineResponse.getNextOffset();

                        if (medicineResponse.getTotalCount() != null) {
                            totalCount = medicineResponse.getTotalCount();
                        }

                        if (medicines != null && !medicines.isEmpty()) {
                            noMedicine.setVisibility(GONE);

                            if (isLoadMore) {
                                adapter.appendData(medicines);
                            } else {
                                cacheResult(medicine.toLowerCase(), medicines);
                                adapter.updateData(medicines);
                                nestedScrollView.post(() -> nestedScrollView.smoothScrollTo(0, 0));
                            }

                            updateItemCount();

                        } else if (!isLoadMore) {
                            noMedicine.setVisibility(VISIBLE);
                            adapter.updateData(new ArrayList<>());
                            tvMedicineCount.setText("0 items");
                        }
                    } catch (Exception e) {
                        handleError(isLoadMore);
                    }
                } else {
                    handleError(isLoadMore);
                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    loadingOverlay.setVisibility(GONE);
                    showLoadMoreProgress(false);
                    isLoadingMore = false;

                    if (!isLoadMore) {
                        noMedicine.setVisibility(VISIBLE);
                    }
                    Toast.makeText(MedicineSearch.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleError(boolean isLoadMore) {
        if (!isLoadMore) {
            adapter.updateData(new ArrayList<>());
            tvMedicineCount.setText("0 items");
            noMedicine.setVisibility(VISIBLE);
        }
        Toast.makeText(MedicineSearch.this, "Error Fetching Medicines", Toast.LENGTH_SHORT).show();
    }

    private void updateItemCount() {
        int currentCount = adapter.getItemCount();
        if (totalCount > 0) {
            tvMedicineCount.setText(currentCount + " of " + totalCount + " items");
        } else {
            tvMedicineCount.setText(currentCount + " items" + (hasMoreData ? "+" : ""));
        }
    }

    private void cacheResult(String query, List<Medicine> medicines) {
        if (searchCache.size() >= CACHE_MAX_SIZE) {
            String firstKey = searchCache.keySet().iterator().next();
            searchCache.remove(firstKey);
        }
        searchCache.put(query, medicines);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
        searchCache.clear();
    }
}
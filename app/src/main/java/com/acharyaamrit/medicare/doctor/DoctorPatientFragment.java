package com.acharyaamrit.medicare.doctor;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.NotificationActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.doctor.adapter.SearchPatientAdapter;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.request.SearchPatientRequest;
import com.acharyaamrit.medicare.doctor.model.response.SearchPatientResponse;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DoctorPatientFragment extends Fragment {

    EditText searchInput;
    TextView tv_patient_count, name, did, specialist;
    ConstraintLayout notification_icon;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    ImageView noUserImage, scan_icon;
    CardView search_card;

    // QR Scanner Launcher
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;

    // Camera Permission Launcher
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public DoctorPatientFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        qrScannerLauncher = registerForActivityResult(
                new ScanContract(),
                result -> {
                    if (result.getContents() != null) {
                        String scannedData = result.getContents();
                        handleScannedQRCode(scannedData);
                    } else {
                        Toast.makeText(requireContext(), "Scan cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openQRScanner();
                    } else {
                        Toast.makeText(requireContext(), "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_patient, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ((DoctorHomePageActivity) requireActivity())
                .setSelectedBackground(R.id.medicine_button_background);
        initializedView(view);
        noUserImage.setVisibility(VISIBLE);
        setNavData(token);
        setUpListener(view);
        return view;
    }

    public void setNavData(String token) {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        Doctor doctor = databaseHelper.getDoctorByToken(token);
        if (doctor != null) {
            name.setText(doctor.getName());
            did.setText("DID: " + doctor.getDoctor_id());
            specialist.setText((doctor.getSpeciality().isEmpty()) ? "" : doctor.getSpeciality());
        }
    }

    private void initializedView(View view) {
        searchInput = view.findViewById(R.id.search_input);
        tv_patient_count = view.findViewById(R.id.tv_patient_count);
        tv_patient_count.setText("0 Patients Found");
        notification_icon = view.findViewById(R.id.notification_icon);
        noUserImage = view.findViewById(R.id.noUserImage);
        name = view.findViewById(R.id.name);
        did = view.findViewById(R.id.did);
        specialist = view.findViewById(R.id.specialist);
        search_card = view.findViewById(R.id.search_card);
        scan_icon = view.findViewById(R.id.scan_icon);
    }

    private void setUpListener(View view) {
        notification_icon = view.findViewById(R.id.notification_icon);

        search_card.setOnClickListener(v -> {
            searchInput.requestFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });

        // QR Scanner Click Listener
        scan_icon.setOnClickListener(v -> {
            checkCameraPermissionAndScan();
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> searchPatient(s.toString(), view);
                handler.postDelayed(searchRunnable, 1000);
            }
        });

        searchInput.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }
                searchPatient(searchInput.getText().toString(), view);
                return true;
            }
            return false;
        });
    }

    // Check camera permission and open scanner
    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openQRScanner();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Open QR Scanner
    private void openQRScanner() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Patient QR Code");
        options.setCameraId(0); // Use back camera
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureActivityPortrait.class);

        qrScannerLauncher.launch(options);
    }

    // Handle scanned QR code data
    private void handleScannedQRCode(String scannedData) {

        searchInput.setText(scannedData);

        // Or directly search with the scanned data
        searchPatient(scannedData, getView());

        Toast.makeText(requireContext(), "Scanned: " + scannedData, Toast.LENGTH_SHORT).show();
    }

    private void searchPatient(String search, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_patients);

        if (search == null || search.trim().isEmpty()) {
            recyclerView.setAdapter(null);
            noUserImage.setVisibility(VISIBLE);
            tv_patient_count.setText("0 Patients Found");
            return;
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        SearchPatientRequest searchPatientRequest = new SearchPatientRequest(search);
        Call<SearchPatientResponse> call = apiService.searchPatient("Bearer " + token, searchPatientRequest);
        call.enqueue(new Callback<SearchPatientResponse>() {
            @Override
            public void onResponse(Call<SearchPatientResponse> call, retrofit2.Response<SearchPatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        noUserImage.setVisibility(GONE);
                        SearchPatientResponse searchPatientResponse = response.body();
                        List<Patient> lp = searchPatientResponse.getPatients();
                        SearchPatientAdapter searchPatientAdapter = new SearchPatientAdapter(lp, requireContext());
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        recyclerView.setAdapter(searchPatientAdapter);
                        tv_patient_count.setText(lp.size() + " Patients Found");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to execute", Toast.LENGTH_SHORT).show();
                        tv_patient_count.setText("0 Patients Found");
                    }
                } else {
                    tv_patient_count.setText("0 Patients Found");
                }
            }

            @Override
            public void onFailure(Call<SearchPatientResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
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
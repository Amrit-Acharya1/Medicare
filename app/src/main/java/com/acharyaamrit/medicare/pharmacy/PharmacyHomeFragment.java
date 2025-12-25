package com.acharyaamrit.medicare.pharmacy;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.acharyaamrit.medicare.doctor.CaptureActivityPortrait;
import com.acharyaamrit.medicare.doctor.DoctorHomePageActivity;
import com.acharyaamrit.medicare.doctor.adapter.SearchPatientAdapter;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.request.SearchPatientRequest;
import com.acharyaamrit.medicare.doctor.model.response.SearchPatientResponse;
import com.acharyaamrit.medicare.patient.model.patientModel.Patient;
import com.acharyaamrit.medicare.pharmacy.adapter.PharmacySearchPatientAdapter;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class PharmacyHomeFragment extends Fragment {

    EditText searchInput;
    TextView tv_patient_count_pharmacy, name, did,iconText;
    ConstraintLayout notification_icon;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    ImageView  scan_icon;
    CardView search_card;

    // QR Scanner Launcher
    private ActivityResultLauncher<ScanOptions> qrScannerLauncher;

    // Camera Permission Launcher
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    public PharmacyHomeFragment() {
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

        View view =  inflater.inflate(R.layout.fragment_pharmacy_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ((PharmacyHomeActivity) requireActivity())
                .setSelectedBackground(R.id.home_button_background);
        initializedView(view);
        setNavData(token);
        setUpListener(view);
        return view;
    }

    public void setNavData(String token) {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        Pharmacy pharmacy = databaseHelper.getPharmacyByToken(token);
        if (pharmacy != null) {
            iconText.setText(String.valueOf(pharmacy.getName().charAt(0)));
            name.setText(pharmacy.getName());
            did.setText("Pan No: " + pharmacy.getPan_no());

        }
    }
    private void initializedView(View view) {
        searchInput = view.findViewById(R.id.search_input);
        tv_patient_count_pharmacy = view.findViewById(R.id.tv_patient_count_pharmacy);
        tv_patient_count_pharmacy.setText("0 Patients Found");
        notification_icon = view.findViewById(R.id.notification_icon);
        name = view.findViewById(R.id.name);
        did = view.findViewById(R.id.did);
        search_card = view.findViewById(R.id.search_card);
        scan_icon = view.findViewById(R.id.scan_icon);
        iconText = view.findViewById(R.id.iconText);
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
    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            openQRScanner();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
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

    private void handleScannedQRCode(String scannedData) {

        searchInput.setText(scannedData);

        // Or directly search with the scanned data
        searchPatient(scannedData, getView());

        Toast.makeText(requireContext(), "Scanned: " + scannedData, Toast.LENGTH_SHORT).show();
    }

    private void searchPatient(String search, View view) {
        RecyclerView recyclerView = view.findViewById(R.id.rv_patients_pharmacy);

        if (search == null || search.trim().isEmpty()) {
            recyclerView.setAdapter(null);
            tv_patient_count_pharmacy.setText("0 Patients Found");
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

                        SearchPatientResponse searchPatientResponse = response.body();
                        List<Patient> lp = searchPatientResponse.getPatients();
                        PharmacySearchPatientAdapter searchPatientAdapter = new PharmacySearchPatientAdapter(lp, (PharmacyHomeActivity)requireActivity());
                        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                        recyclerView.setAdapter(searchPatientAdapter);
                        tv_patient_count_pharmacy.setText(lp.size() + " Patients Found");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Failed to execute", Toast.LENGTH_SHORT).show();
                        tv_patient_count_pharmacy.setText("0 Patients Found");
                    }
                } else {
                    tv_patient_count_pharmacy.setText("0 Patients Found");
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
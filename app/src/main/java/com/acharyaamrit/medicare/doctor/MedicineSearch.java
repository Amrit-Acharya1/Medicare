package com.acharyaamrit.medicare.doctor;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.doctor.adapter.MedicineSearchAdapter;
import com.acharyaamrit.medicare.doctor.adapter.PrescriptionRelationForPatientAdapter;
import com.acharyaamrit.medicare.doctor.model.Medicine;
import com.acharyaamrit.medicare.doctor.model.request.MedicineRequest;
import com.acharyaamrit.medicare.doctor.model.request.PRelationRequest;
import com.acharyaamrit.medicare.doctor.model.response.MedicineResponse;
import com.acharyaamrit.medicare.doctor.model.response.PRelation;
import com.acharyaamrit.medicare.doctor.model.response.PRelationResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineSearch extends AppCompatActivity {

    private static final int SEARCH_DELAY_MS = 500; // Reduced delay for better UX
    private static final int MIN_SEARCH_LENGTH = 0; // Minimum characters to trigger search
    private static final int CACHE_MAX_SIZE = 50; // Maximum cached searches

    private CardView btnBack;
    private TextView tv_name, tv_pid, tvMedicineCount,newPrescription, prescriptionTile;
    private RecyclerView rvMedicines;
    private EditText searchMedicine;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // Adapter - reuse instead of creating new one each time
    private MedicineSearchAdapter adapter;

    // Cache for search results
    private final Map<String, List<Medicine>> searchCache = new HashMap<>();

    // Current API call - to cancel if new search is triggered
    private Call<MedicineResponse> currentCall;

    // Token - fetch once, reuse
    private String authToken;

    // API Service - create once, reuse
    private ApiService apiService;
    private FrameLayout loadingOverlay, noMedicine;
    private FloatingActionButton fab;

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
        String name = getIntent().getStringExtra("patient_name");
        String pid = getIntent().getStringExtra("patient_id");
        fetchPrecriptionsForPatient();
        setupRecyclerView(pid);
        setupListeners();

        tv_name.setText(name);
        tv_pid.setText("PID: " + pid);
        noMedicine.setVisibility(VISIBLE);

    }

    private void initializeData() {
        // Initialize token once
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        authToken = "Bearer " + sharedPreferences.getString("token", null);

        // Initialize API service once
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
        fab =findViewById(R.id.fab);

    }
    private void showBottomSheet(List<PRelation> prelationList){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_prescription_relation_for_patient, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        newPrescription = bottomSheetView.findViewById(R.id.newPrescription);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.prelationRecycler);

        PrescriptionRelationForPatientAdapter prescriptionRelationForPatientAdapter = new PrescriptionRelationForPatientAdapter(prelationList, this, bottomSheetDialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(prescriptionRelationForPatientAdapter);

        newPrescription.setOnClickListener(v->{
            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("prescriptionRelation_id");
            editor.apply();
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setOnDismissListener(dialog -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            String pres_id = sharedPreferences.getString("prescriptionRelation_id", null);
            if(pres_id == null){
                prescriptionTile.setText("New Prescription");
            }else{
                prescriptionTile.setText("Prescription No: "+ pres_id);
            }
                 });

        bottomSheetDialog.show();


    }

    private void fetchPrecriptionsForPatient(){
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        int id = Integer.parseInt(getIntent().getStringExtra("patient_id"));
        PRelationRequest pRelationRequest = new PRelationRequest(id);
        Call<PRelationResponse> call = apiService.fetchPrecriptionForPatient("Bearer " + token, pRelationRequest );
        call.enqueue(new Callback<PRelationResponse>() {
            @Override
            public void onResponse(Call<PRelationResponse> call, Response<PRelationResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    try {

                        showBottomSheet(response.body().getpRelationList());

                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(MedicineSearch.this, "Failed to execute", Toast.LENGTH_SHORT).show();

                    }
                }else{
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
    private void setupRecyclerView(String pid) {
        adapter = new MedicineSearchAdapter(new ArrayList<>(), this, Integer.parseInt(pid));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        rvMedicines.setLayoutManager(layoutManager);

        rvMedicines.setNestedScrollingEnabled(true);

        rvMedicines.setFocusable(true);
        rvMedicines.setFocusableInTouchMode(true);

        rvMedicines.setHasFixedSize(true);

        rvMedicines.setItemViewCacheSize(20);

        ViewGroup.LayoutParams params = rvMedicines.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        rvMedicines.setLayoutParams(params);

        rvMedicines.setAdapter(adapter);

        RecyclerView.RecycledViewPool pool = new RecyclerView.RecycledViewPool();
        pool.setMaxRecycledViews(0, 30);
        rvMedicines.setRecycledViewPool(pool);
    }

    private void setupListeners() {
        fab.setOnClickListener(v->{
            fetchPrecriptionsForPatient();
        });
        btnBack.setOnClickListener(v -> finish());

//        searchMedicine.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Cancel pending search
//                if (searchRunnable != null) {
//                    handler.removeCallbacks(searchRunnable);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String query = s.toString().trim();
//
//                // Don't search if query is too short
//                if (query.length() < MIN_SEARCH_LENGTH) {
//                    adapter.updateData(new ArrayList<>());
//                    return;
//                }
//
//                // Check cache first
//                if (searchCache.containsKey(query.toLowerCase())) {
//                    adapter.updateData(searchCache.get(query.toLowerCase()));
//                    return;
//                }
//
//                // Debounce the search
//                searchRunnable = () -> fetchMedicine(query);
//                handler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
//            }
//        });

        searchMedicine.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard();
                if (searchRunnable != null) {
                    handler.removeCallbacks(searchRunnable);
                }

                String query = searchMedicine.getText().toString().trim();
                if (query.length() >= MIN_SEARCH_LENGTH) {
                    noMedicine.setVisibility(GONE);
                    loadingOverlay.setVisibility(VISIBLE);
                    fetchMedicine(query);
                }
                return true;
            }
            return false;
        });
    }
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            if (getCurrentFocus() != null) {
                ((android.view.inputmethod.InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    private void fetchMedicine(String medicine) {
        // Cancel previous call if exists
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
        if(medicine.isEmpty()){
            noMedicine.setVisibility(VISIBLE);

            loadingOverlay.setVisibility(GONE);
            tvMedicineCount.setText("0 items");
            adapter.updateData(new ArrayList<>());
            return;
        }



        MedicineRequest medicineRequest = new MedicineRequest(medicine);
        currentCall = apiService.searchMedicine(authToken, medicineRequest);

        currentCall.enqueue(new Callback<MedicineResponse>() {
            @Override
            public void onResponse(Call<MedicineResponse> call, Response<MedicineResponse> response) {
                if (call.isCanceled()) return;

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        MedicineResponse medicineResponse = response.body();
                        List<Medicine> medicines = medicineResponse.getMedicines();

                        loadingOverlay.setVisibility(GONE);

                        if (medicines != null && !medicines.isEmpty()) {

                            cacheResult(medicine.toLowerCase(), medicines);

                            adapter.updateData(medicines);
                        } else {
                            adapter.updateData(new ArrayList<>());
                        }
                        tvMedicineCount.setText(medicines.size()+" items");
                    } catch (Exception e) {
                        tvMedicineCount.setText("0 items");

                        Toast.makeText(MedicineSearch.this, "Error Fetching Medicines", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    adapter.updateData(new ArrayList<>());
                    tvMedicineCount.setText("0 items");
                    loadingOverlay.setVisibility(INVISIBLE);
                    noMedicine.setVisibility(VISIBLE);

                }
            }

            @Override
            public void onFailure(Call<MedicineResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    loadingOverlay.setVisibility(GONE);
                    noMedicine.setVisibility(VISIBLE);

                    Toast.makeText(MedicineSearch.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cacheResult(String query, List<Medicine> medicines) {
        // Limit cache size
        if (searchCache.size() >= CACHE_MAX_SIZE) {
            // Remove first entry (simple LRU-like behavior)
            String firstKey = searchCache.keySet().iterator().next();
            searchCache.remove(firstKey);
        }
        searchCache.put(query, medicines);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        if (currentCall != null && !currentCall.isCanceled()) {
            currentCall.cancel();
        }
        searchCache.clear();
    }
}
package com.acharyaamrit.medicare.doctor.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.doctor.model.Medicine;
import com.acharyaamrit.medicare.doctor.model.request.PrescriptionRelationRequest;
import com.acharyaamrit.medicare.doctor.model.request.PrescriptionRequest;
import com.acharyaamrit.medicare.doctor.model.response.PrescriptionRelationResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicineSearchAdapter extends RecyclerView.Adapter<MedicineSearchAdapter.ViewHolder> {

    private final List<Medicine> medicineList;
    private final Context context;
    private final int pid;
    private OnMedicineClickListener listener;

    private int sessionPrescriptionRelationId = -1;
    private Set<Integer> addedMedicineIds = new HashSet<>();

    public interface OnMedicineClickListener {
        void onAddToPrescription(Medicine medicine, int position);
    }

    public MedicineSearchAdapter(List<Medicine> medicineList, Context context, int pid) {
        this.medicineList = new ArrayList<>(medicineList);
        this.context = context;
        this.pid = pid;
        this.sessionPrescriptionRelationId = -1;
        this.addedMedicineIds = new HashSet<>();
        setHasStableIds(true);
    }

    public void setOnMedicineClickListener(OnMedicineClickListener listener) {
        this.listener = listener;
    }

    public void startNewSession() {
        this.sessionPrescriptionRelationId = -1;
        this.addedMedicineIds.clear();
    }

    public int getSessionPrescriptionRelationId() {
        return sessionPrescriptionRelationId;
    }

    public boolean isMedicineAddedInSession(int medicineId) {
        return addedMedicineIds.contains(medicineId);
    }

    public int getAddedMedicineCount() {
        return addedMedicineIds.size();
    }

    /**
     * Update data using DiffUtil for efficient updates (replaces all data)
     */
    public void updateData(List<Medicine> newMedicines) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(
                new MedicineDiffCallback(this.medicineList, newMedicines));
        this.medicineList.clear();
        this.medicineList.addAll(newMedicines);
        diffResult.dispatchUpdatesTo(this);
    }

    /**
     * Append new data to existing list (for infinite scroll)
     */
    public void appendData(List<Medicine> newMedicines) {
        if (newMedicines == null || newMedicines.isEmpty()) {
            return;
        }

        int startPosition = medicineList.size();

        // Filter out duplicates based on ID
        List<Medicine> uniqueMedicines = new ArrayList<>();
        Set<Integer> existingIds = new HashSet<>();
        for (Medicine m : medicineList) {
            existingIds.add(m.getId());
        }

        for (Medicine medicine : newMedicines) {
            if (!existingIds.contains(medicine.getId())) {
                uniqueMedicines.add(medicine);
                existingIds.add(medicine.getId());
            }
        }

        if (!uniqueMedicines.isEmpty()) {
            medicineList.addAll(uniqueMedicines);
            notifyItemRangeInserted(startPosition, uniqueMedicines.size());
        }
    }

    /**
     * Clear all data
     */
    public void clearData() {
        int size = medicineList.size();
        medicineList.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public long getItemId(int position) {
        Medicine medicine = medicineList.get(position);
        return medicine.getId();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(medicineList.get(position), listener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Medicine medicine = medicineList.get(position);
            holder.bindPartial(medicine, payloads);
        }
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clear();
    }

    void setSessionPrescriptionRelationId(int id) {
        this.sessionPrescriptionRelationId = id;
    }

    void addMedicineToSession(int medicineId) {
        this.addedMedicineIds.add(medicineId);
    }

    int getPatientId() {
        return this.pid;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory, tvDosageForm, tvMedicineName,
                tvGenericName, tvCompanyName, tvCode;
        private final CardView btnAddToPrescription;
        private final ImageView ivMedicineImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDosageForm = itemView.findViewById(R.id.tvDosageForm);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvGenericName = itemView.findViewById(R.id.tvGenericName);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvCode = itemView.findViewById(R.id.tvCode);
            ivMedicineImage = itemView.findViewById(R.id.ivMedicineImage);
            btnAddToPrescription = itemView.findViewById(R.id.btnAddToPrescription);
        }

        public void bind(Medicine medicine, OnMedicineClickListener listener, MedicineSearchAdapter adapter) {
            tvCategory.setText(nullSafe(medicine.getCategory_name()));
            tvDosageForm.setText(nullSafe(medicine.getDoasage_form()));
            tvMedicineName.setText(nullSafe(medicine.getName()));
            tvGenericName.setText(nullSafe(medicine.getGeneric_name()));
            tvCompanyName.setText(nullSafe(medicine.getCompany_name()));
            tvCode.setText(nullSafe(medicine.getCode()));

            // Visual indicator if already added
            if (adapter.isMedicineAddedInSession(medicine.getId())) {
                btnAddToPrescription.setAlpha(0.5f);
            } else {
                btnAddToPrescription.setAlpha(1.0f);
            }

            btnAddToPrescription.setOnClickListener(v -> {
                if (adapter.isMedicineAddedInSession(medicine.getId())) {
                    Toast.makeText(itemView.getContext(),
                            medicine.getName() + " is already added in this prescription",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                showMedicineBottomSheet(medicine, listener, adapter);
            });
        }

        private void showMedicineBottomSheet(Medicine medicine, OnMedicineClickListener listener,
                                             MedicineSearchAdapter adapter) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(itemView.getContext());
            View bottomSheetView = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.item_bottomsheet_medicine_add, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            TextView tvMedicineName = bottomSheetView.findViewById(R.id.tvMedicineName);
            TextView tvManufacturer = bottomSheetView.findViewById(R.id.tvManufacturer);
            TextView tvDosage = bottomSheetView.findViewById(R.id.tvDosage);
            ImageView ivMedicine = bottomSheetView.findViewById(R.id.ivMedicine);

            Button btnMinus = bottomSheetView.findViewById(R.id.btnMinus);
            TextView tvQuantity = bottomSheetView.findViewById(R.id.tvQuantity);
            Button btnPlus = bottomSheetView.findViewById(R.id.btnPlus);

            EditText etFrequency = bottomSheetView.findViewById(R.id.etFrequency);
            EditText etTimePeriod = bottomSheetView.findViewById(R.id.etTimePeriod);
            Spinner spinnerTimePeriod = bottomSheetView.findViewById(R.id.spinnerTimePeriod);
            EditText etNote = bottomSheetView.findViewById(R.id.etNote);
            EditText etInterval = bottomSheetDialog.findViewById(R.id.etInterval);

            Button btnAdd = bottomSheetView.findViewById(R.id.btnAdd);

            tvMedicineName.setText(nullSafe(medicine.getName()));
            tvManufacturer.setText(nullSafe(medicine.getCompany_name()));

            String dosageText = nullSafe(medicine.getDoasage_form());
            if (medicine.getGeneric_name() != null && !medicine.getGeneric_name().isEmpty()) {
                dosageText += " • " + medicine.getGeneric_name();
            }
            tvDosage.setText(dosageText);

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[]{"Days", "Weeks", "Months"});
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerTimePeriod.setAdapter(spinnerAdapter);

            final int[] quantity = {1};

            btnMinus.setOnClickListener(v -> {
                if (quantity[0] > 1) {
                    quantity[0]--;
                    tvQuantity.setText(String.valueOf(quantity[0]));
                }
            });

            btnPlus.setOnClickListener(v -> {
                if (quantity[0] < 99) {
                    quantity[0]++;
                    tvQuantity.setText(String.valueOf(quantity[0]));
                }
            });

            btnAdd.setOnClickListener(v -> {
                String frequency = etFrequency.getText().toString().trim();
                String timePeriod = etTimePeriod.getText().toString().trim();
                String timePeriodUnit = spinnerTimePeriod.getSelectedItem().toString();
                String interval = etInterval.getText().toString();
                String note = etNote.getText().toString().trim();

                if (frequency.isEmpty()) {
                    etFrequency.setError("Frequency is required");
                    etFrequency.requestFocus();
                    return;
                }

                if (timePeriod.isEmpty()) {
                    etTimePeriod.setError("Time period is required");
                    etTimePeriod.requestFocus();
                    return;
                }

                int freq, period;
                try {
                    freq = Integer.parseInt(frequency);
                    if (freq <= 0) {
                        etFrequency.setError("Must be greater than 0");
                        etFrequency.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    etFrequency.setError("Please enter a valid number");
                    etFrequency.requestFocus();
                    return;
                }

                try {
                    period = Integer.parseInt(timePeriod);
                    if (period <= 0) {
                        etTimePeriod.setError("Must be greater than 0");
                        etTimePeriod.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    etTimePeriod.setError("Please enter a valid number");
                    etTimePeriod.requestFocus();
                    return;
                }

                btnAdd.setEnabled(false);
                btnAdd.setText("Adding...");

                SharedPreferences sharedPreferences = itemView.getContext()
                        .getSharedPreferences("user_preference", MODE_PRIVATE);
                String token = sharedPreferences.getString("token", null);
                String prescriptionRelationID = sharedPreferences.getString("prescriptionRelation_id", null);

                if (prescriptionRelationID == null) {
                    createPrescriptionRelationAndSave(token, adapter.getPatientId(), medicine,
                            period, timePeriodUnit, quantity[0], freq, note,interval,
                            listener, bottomSheetDialog, btnAdd, adapter);
                } else {
                    PrescriptionRequest prescriptionRequest = new PrescriptionRequest(
                            medicine.getId(),
                            Integer.parseInt(prescriptionRelationID),
                            period,
                            timePeriodUnit,
                            quantity[0],
                            freq,
                            note,
                            interval

                    );

                    savePrescription(prescriptionRequest, medicine, listener,
                            bottomSheetDialog, btnAdd, adapter);
                }
            });

            bottomSheetDialog.show();
        }

        private void createPrescriptionRelationAndSave(String token, int pid, Medicine medicine,
                                                       int timePeriod, String timePeriodUnit, int quantity, int frequency, String note,
                                                       String interval, OnMedicineClickListener listener, BottomSheetDialog bottomSheetDialog,
                                                       Button btnAdd, MedicineSearchAdapter adapter) {

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            DatabaseHelper dbHelper = new DatabaseHelper(itemView.getContext());
            int doctorId = dbHelper.getDoctorByToken(token).getDoctor_id();
            PrescriptionRelationRequest prescriptionRelationRequest = new PrescriptionRelationRequest(doctorId, pid);

            Call<PrescriptionRelationResponse> call = apiService.addPrescriptionRelation(
                    "Bearer " + token, prescriptionRelationRequest);

            call.enqueue(new Callback<PrescriptionRelationResponse>() {
                @Override
                public void onResponse(Call<PrescriptionRelationResponse> call,
                                       Response<PrescriptionRelationResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            int prescriptionRelationId = response.body().getPreciptionRelation().getId();

                            adapter.setSessionPrescriptionRelationId(prescriptionRelationId);

                            PrescriptionRequest prescriptionRequest = new PrescriptionRequest(
                                    medicine.getId(),
                                    prescriptionRelationId,
                                    timePeriod,
                                    timePeriodUnit,
                                    quantity,
                                    frequency,
                                    note,
                                    interval
                            );

                            savePrescription(prescriptionRequest, medicine, listener,
                                    bottomSheetDialog, btnAdd, adapter);

                        } catch (Exception e) {
                            resetButton(btnAdd);
                            Toast.makeText(itemView.getContext(),
                                    "Error occurred while adding prescription",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        resetButton(btnAdd);
                        Toast.makeText(itemView.getContext(),
                                "Failed to create prescription relation",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PrescriptionRelationResponse> call, Throwable t) {
                    resetButton(btnAdd);
                    Toast.makeText(itemView.getContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void savePrescription(PrescriptionRequest request, Medicine medicine,
                                      OnMedicineClickListener listener, BottomSheetDialog bottomSheetDialog,
                                      Button btnAdd, MedicineSearchAdapter adapter) {

            SharedPreferences sharedPreferences = itemView.getContext()
                    .getSharedPreferences("user_preference", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<UserResponse> call = apiService.addPrescription("Bearer " + token, request);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.addMedicineToSession(medicine.getId());

                        if (listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                listener.onAddToPrescription(medicine, position);
                            }
                        }

                        Toast.makeText(itemView.getContext(),
                                medicine.getName() + " added to prescription",
                                Toast.LENGTH_SHORT).show();

                        bottomSheetDialog.dismiss();
                    } else {
                        resetButton(btnAdd);
                        Toast.makeText(itemView.getContext(),
                                "Failed to save prescription",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    resetButton(btnAdd);
                    Toast.makeText(itemView.getContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void resetButton(Button btnAdd) {
            btnAdd.setEnabled(true);
            btnAdd.setText("Add to Prescription");
        }

        private String nullSafe(String value) {
            return value != null ? value : "";
        }

        public void bindPartial(Medicine medicine, List<Object> payloads) {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    String change = (String) payload;
                    switch (change) {
                        case "name":
                            tvMedicineName.setText(nullSafe(medicine.getName()));
                            break;
                        case "category":
                            tvCategory.setText(nullSafe(medicine.getCategory_name()));
                            break;
                    }
                }
            }
        }

        public void clear() {
            btnAddToPrescription.setOnClickListener(null);
        }
    }

    private static class MedicineDiffCallback extends DiffUtil.Callback {
        private final List<Medicine> oldList;
        private final List<Medicine> newList;

        MedicineDiffCallback(List<Medicine> oldList, List<Medicine> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Medicine oldMedicine = oldList.get(oldItemPosition);
            Medicine newMedicine = newList.get(newItemPosition);
            return Objects.equals(oldMedicine.getName(), newMedicine.getName()) &&
                    Objects.equals(oldMedicine.getCategory_name(), newMedicine.getCategory_name()) &&
                    Objects.equals(oldMedicine.getDoasage_form(), newMedicine.getDoasage_form()) &&
                    Objects.equals(oldMedicine.getGeneric_name(), newMedicine.getGeneric_name()) &&
                    Objects.equals(oldMedicine.getCompany_name(), newMedicine.getCompany_name());
        }

        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Medicine oldMedicine = oldList.get(oldItemPosition);
            Medicine newMedicine = newList.get(newItemPosition);

            if (!Objects.equals(oldMedicine.getName(), newMedicine.getName())) {
                return "name";
            }
            if (!Objects.equals(oldMedicine.getCategory_name(), newMedicine.getCategory_name())) {
                return "category";
            }
            return null;
        }
    }
}
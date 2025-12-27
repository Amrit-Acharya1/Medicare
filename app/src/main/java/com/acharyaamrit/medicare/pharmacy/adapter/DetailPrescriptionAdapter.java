package com.acharyaamrit.medicare.pharmacy.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.patient.model.patientModel.Preciption;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.acharyaamrit.medicare.pharmacy.model.request.UpdatePriceRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class DetailPrescriptionAdapter extends RecyclerView.Adapter<DetailPrescriptionAdapter.ViewHolder> {

    private List<Preciption> prescriptionList;
    private Context context;
    private OnMedicineDispatchedListener listener;

    // Interface for callback
    public interface OnMedicineDispatchedListener {
        void onMedicineDispatched(int remainingCount, double priceAdded);
    }

    // Updated constructor with listener
    public DetailPrescriptionAdapter(List<Preciption> prescriptionList, Context context, OnMedicineDispatchedListener listener) {
        this.prescriptionList = prescriptionList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull DetailPrescriptionAdapter.ViewHolder holder, int position) {
        Preciption preciption = prescriptionList.get(position);

        holder.tvMedicineName.setText(preciption.getMedicine_name());
        holder.tvDosageForm.setText(preciption.getDoasage_qty() + " " + preciption.getDoasage_unit());
        holder.tvDuration.setText("Duration: " + preciption.getDuration() + " " + preciption.getDuration_type());
        holder.tvCompanyName.setText(preciption.getCompany_name());
        holder.tvCategory.setText("X " + preciption.getQty());

        holder.addBtn.setOnClickListener(v -> {
            // Inflate custom view
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_price, null);
            EditText etPrice = dialogView.findViewById(R.id.etPrice);

            // Build the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add Price")
                    .setView(dialogView)
                    .setPositiveButton("Accept", (dialog, which) -> {
                        String price = etPrice.getText().toString().trim();
                        if (!price.isEmpty()) {
                            // Validate price is a valid number
                            try {
                                double priceValue = Double.parseDouble(price);
                                if (priceValue <= 0) {
                                    Toast.makeText(context, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(context);
                                confirmBuilder.setTitle("Confirm Request")
                                        .setMessage("This Action Cannot Be Changed. Are You Sure You Want To Accept?\n\nMedicine: " + preciption.getMedicine_name() + "\nPrice: Rs. " + price);
                                confirmBuilder.setPositiveButton("Yes", (confirmDialog, confirmWhich) -> {
                                            // Get the current adapter position
                                            int adapterPosition = holder.getAdapterPosition();
                                            if (adapterPosition != RecyclerView.NO_POSITION) {
                                                updatePriceAndPharmacyId(price, preciption.getId(), confirmDialog, dialog, adapterPosition, priceValue);
                                            }
                                        })
                                        .setNegativeButton("No", (confirmDialog, confirmWhich) -> {
                                            confirmDialog.dismiss();
                                        });
                                confirmBuilder.show();

                            } catch (NumberFormatException e) {
                                Toast.makeText(context, "Please enter a valid number", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Price Empty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });

            // Create and show
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void updatePriceAndPharmacyId(String priceValue, int prescriptionId, DialogInterface confirmDialog, DialogInterface dialog, int position, double price) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        Pharmacy pharmacy = databaseHelper.getPharmacyByToken(token);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        UpdatePriceRequest request = new UpdatePriceRequest(priceValue, pharmacy.getPharmacy_id(), prescriptionId);
        Call<UserResponse> call = apiService.updatePriceAndPharmacy("Bearer " + token, request);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Medicine Dispatched Successfully", Toast.LENGTH_SHORT).show();
                    confirmDialog.dismiss();
                    dialog.dismiss();

                    // Remove the item from the list
                    if (position >= 0 && position < prescriptionList.size()) {
                        prescriptionList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, prescriptionList.size());

                        // Notify the activity about the update with price
                        if (listener != null) {
                            listener.onMedicineDispatched(prescriptionList.size(), price);
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to dispatch medicine", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(context, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public DetailPrescriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine_list_pharmacy, parent, false);
        return new DetailPrescriptionAdapter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMedicineName, tvDosageForm, tvDuration, tvCompanyName, tvCategory;
        CardView addBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosageForm = itemView.findViewById(R.id.tvDosageForm);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            addBtn = itemView.findViewById(R.id.addBtn);
        }
    }
}
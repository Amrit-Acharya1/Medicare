package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.model.patientModel.PatientDocument;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;
import java.util.Locale;

public class UserDocumentAdapter extends RecyclerView.Adapter<UserDocumentAdapter.ViewHolder>{

    private List<PatientDocument> patientDocumentList;
    private final Context context;
    private final Boolean viewAll;
    private OnDocumentClickListener clickListener;

    // Click listener interface
    public interface OnDocumentClickListener {
        void onDocumentClick(PatientDocument document, int position);
    }

    public UserDocumentAdapter(List<PatientDocument> patientDocumentList, Context context, Boolean viewAll) {
        this.patientDocumentList = patientDocumentList;
        this.context = context;
        this.viewAll = viewAll;
    }

    public void setOnDocumentClickListener(OnDocumentClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public UserDocumentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_document_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserDocumentAdapter.ViewHolder holder, int position) {
        PatientDocument patientDocument = patientDocumentList.get(position);

        // Set text data
        holder.textDocumentType.setText(patientDocument.getDocument_type());
        holder.textDoctorName.setText(patientDocument.getDoctor_name() != null ? patientDocument.getDoctor_name() : "From you");
        holder.textDocumentDate.setText(patientDocument.getCreated_at());

        // Handle document image/icon
        String url = patientDocument.getDocument_url();
        if (url != null && !url.isEmpty()) {
            if (isImageUrl(url)) {
                // Load image with Glide
                holder.imageDocument.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_broken_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.imageDocument);
            } else if (isPdfUrl(url)) {
                // Clear any previous Glide load and show PDF icon
                Glide.with(context).clear(holder.imageDocument);
                holder.imageDocument.setImageResource(R.drawable.ic_pdf);
            } else {
                // Generic document icon
                Glide.with(context).clear(holder.imageDocument);
                holder.imageDocument.setImageResource(R.drawable.ic_document_generic);
            }
        } else {
            // No URL provided
            Glide.with(context).clear(holder.imageDocument);
            holder.imageDocument.setImageResource(R.drawable.ic_document_generic);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onDocumentClick(patientDocument, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientDocumentList != null ? (!viewAll ? Math.min(patientDocumentList.size(), 5):patientDocumentList.size()) : 0;
    }

    private boolean isImageUrl(String url) {
        String lower = url.toLowerCase(Locale.getDefault());
        return lower.matches(".*\\.(jpg|jpeg|png|gif|webp|bmp)(\\?.*)?$");
    }

    private boolean isPdfUrl(String url) {
        String lower = url.toLowerCase(Locale.getDefault());
        return lower.matches(".*\\.pdf(\\?.*)?$");
    }

    // Method to update list
    public void updateList(List<PatientDocument> newList) {
        this.patientDocumentList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDocumentType;
        TextView textDoctorName;
        TextView textDocumentDate;
        ImageView imageDocument;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDocumentType = itemView.findViewById(R.id.textDocumentType);
            textDoctorName = itemView.findViewById(R.id.textDoctorName);
            textDocumentDate = itemView.findViewById(R.id.textDocumentDate);
            imageDocument = itemView.findViewById(R.id.imageDocument);
        }
    }
}
package com.acharyaamrit.medicare.common.utils;


import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.text.DecimalFormat;
public class ImagePickerBottomSheet extends BottomSheetDialogFragment {

    private ImageView previewImage;
    private ImageView editIconOverlay;
    private FrameLayout loadingOverlay;
    private LinearLayout imageInfoLayout;
    private TextView imageInfoText;
    private Button btnChooseImage;
    private Button btnUpload;
    private Button btnCancel;

    private Uri selectedImageUri = null;
    private String currentImageUrl = null;

    private OnImageSelectedListener listener;
    private ActivityResultLauncher<String> imagePickerLauncher;

    public interface OnImageSelectedListener {
        void onImageSelected(Uri imageUri);
        void onUploadClicked(Uri imageUri);
    }

    public static ImagePickerBottomSheet newInstance(String currentImageUrl) {
        ImagePickerBottomSheet fragment = new ImagePickerBottomSheet();
        Bundle args = new Bundle();
        args.putString("current_image_url", currentImageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        showSelectedImage(uri);
                        enableUploadButton(true);

                        if (listener != null) {
                            listener.onImageSelected(uri);
                        }
                    }
                }
        );
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);

        if (getArguments() != null) {
            currentImageUrl = getArguments().getString("current_image_url");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
            }
        });

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_image_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupListeners();
        loadCurrentImage();
    }

    private void initViews(View view) {
        previewImage = view.findViewById(R.id.previewImage);
        editIconOverlay = view.findViewById(R.id.editIconOverlay);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        imageInfoLayout = view.findViewById(R.id.imageInfoLayout);
        imageInfoText = view.findViewById(R.id.imageInfoText);
        btnChooseImage = view.findViewById(R.id.btnChooseImage);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnCancel = view.findViewById(R.id.btnCancel);

        enableUploadButton(false);
    }

    private void setupListeners() {
        btnChooseImage.setOnClickListener(v -> openGallery());
        editIconOverlay.setOnClickListener(v -> openGallery());
        previewImage.setOnClickListener(v -> openGallery());

        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null && listener != null) {
                showLoading(true);
                listener.onUploadClicked(selectedImageUri);
            }
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void openGallery() {
        imagePickerLauncher.launch("image/*");
    }

    private void loadCurrentImage() {
        if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(currentImageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .centerCrop()
                    .into(previewImage);

            // Show info about current image
            imageInfoText.setText("Current profile photo");
            imageInfoLayout.setVisibility(View.VISIBLE);
        } else {
            // ✅ Show placeholder if no image
            previewImage.setImageResource(R.drawable.logo);
            imageInfoText.setText("No profile photo set");
            imageInfoLayout.setVisibility(View.VISIBLE);
        }
    }

    private void showSelectedImage(Uri uri) {
        Glide.with(requireContext())
                .load(uri)
                .centerCrop()
                .into(previewImage);

        showImageInfo(uri);
    }

    private void showImageInfo(Uri uri) {
        try {
            String fileName = getFileName(uri);
            long fileSize = getFileSize(uri);
            String sizeFormatted = formatFileSize(fileSize);

            imageInfoText.setText(fileName + " • " + sizeFormatted);
            imageInfoLayout.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            imageInfoLayout.setVisibility(View.GONE);
        }
    }

    private String getFileName(Uri uri) {
        String result = "image.jpg";
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireContext().getContentResolver()
                    .query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private long getFileSize(Uri uri) {
        long size = 0;
        try (Cursor cursor = requireContext().getContentResolver()
                .query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (sizeIndex >= 0) {
                    size = cursor.getLong(sizeIndex);
                }
            }
        }
        return size;
    }

    private String formatFileSize(long size) {
        if (size <= 0) return "0 B";

        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void enableUploadButton(boolean enable) {
        btnUpload.setEnabled(enable);
        btnUpload.setAlpha(enable ? 1.0f : 0.5f);
    }

    public void showLoading(boolean show) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnUpload != null) {
            btnUpload.setEnabled(!show);
        }
        if (btnChooseImage != null) {
            btnChooseImage.setEnabled(!show);
        }
    }

    public void onUploadSuccess() {
        showLoading(false);
        Toast.makeText(requireContext(), "Profile photo updated!", Toast.LENGTH_SHORT).show();
        dismiss();
    }

    public void onUploadError(String message) {
        showLoading(false);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}
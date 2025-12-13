package com.acharyaamrit.medicare.patient;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.service.TouchImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class ImageViewerActivity extends AppCompatActivity {

    private TouchImageView photoView;
    private ProgressBar progressBar;
    private ImageView btnClose;
    private ImageView btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        // Initialize views
        photoView = findViewById(R.id.photoView);
        progressBar = findViewById(R.id.progressBar);
        btnClose = findViewById(R.id.btnClose);
        btnDownload = findViewById(R.id.btnDownload);

        // Get image URL from intent
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        if (imageUrl == null || imageUrl.isEmpty()) {
            Toast.makeText(this, "Invalid image URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load image
        loadImage(imageUrl);

        // Close button
        btnClose.setOnClickListener(v -> finish());

        // Download button
        btnDownload.setOnClickListener(v -> downloadImage(imageUrl));
    }

    private void loadImage(String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(photoView);

        progressBar.setVisibility(View.GONE);
    }

    private void downloadImage(String imageUrl) {
        // Use DownloadManager to download the image
        android.app.DownloadManager.Request request =
                new android.app.DownloadManager.Request(android.net.Uri.parse(imageUrl));
        request.setTitle("Downloading Image");
        request.setDescription("Medicare Document Image");
        request.setNotificationVisibility(
                android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                android.os.Environment.DIRECTORY_DOWNLOADS,
                "medicare_image_" + System.currentTimeMillis() + ".jpg");

        android.app.DownloadManager downloadManager =
                (android.app.DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show();
        }
    }
}
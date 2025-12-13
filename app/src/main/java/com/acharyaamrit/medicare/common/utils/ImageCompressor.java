package com.acharyaamrit.medicare.common.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {
    private static final String TAG = "ImageCompressor";
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;
    private static final int QUALITY = 80;

    /**
     * Compress image file
     */
    public static File compressImage(Context context, File originalFile, String prefix) {
        try {
            // Decode original bitmap
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originalFile.getAbsolutePath(), options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(originalFile.getAbsolutePath(), options);
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode bitmap");
                return null;
            }

            // Scale bitmap if needed
            bitmap = scaleBitmap(bitmap, MAX_WIDTH, MAX_HEIGHT);

            // Save compressed bitmap
            File compressedFile = new File(
                    context.getCacheDir(),
                    prefix + "_compressed_" + System.currentTimeMillis() + ".jpg"
            );

            FileOutputStream fos = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();

            Log.d(TAG, "Original size: " + (originalFile.length() / 1024) + " KB");
            Log.d(TAG, "Compressed size: " + (compressedFile.length() / 1024) + " KB");

            return compressedFile;
        } catch (Exception e) {
            Log.e(TAG, "Error compressing image", e);
            return null;
        }
    }

    /**
     * Calculate sample size for bitmap decoding
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Scale bitmap to max dimensions
     */
    private static Bitmap scaleBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap;
        }

        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        if (scaledBitmap != bitmap) {
            bitmap.recycle();
        }

        return scaledBitmap;
    }

    /**
     * Convert Uri to File
     */
    public static File getFileFromUri(Context context, Uri uri) {
        try {
            String fileName = "temp_" + System.currentTimeMillis() + ".jpg";
            File file = new File(context.getCacheDir(), fileName);

            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            Log.e(TAG, "Error converting Uri to File", e);
            return null;
        }
    }
}
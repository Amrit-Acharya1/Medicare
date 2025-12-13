package com.acharyaamrit.medicare.common.controller.api;

import android.content.Context;
import android.util.Log;

import com.acharyaamrit.medicare.patient.model.request.PatientDocumentRequest;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.model.response.UserResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadDocumentOfPatient {
    private static final String TAG = "UploadDocument";
    private String token;
    private PatientDocumentRequest patientDocumentRequest;

    public UploadDocumentOfPatient(String token, PatientDocumentRequest patientDocumentRequest) {
        this.token = token;
        this.patientDocumentRequest = patientDocumentRequest;
    }

    public interface DocumentCallback {
        void onSuccess();
        void onFailure(String error);
    }

    public void uploadDocument(Context context, DocumentCallback callback) {
        // Validation
        if (context == null) {
            if (callback != null) {
                callback.onFailure("Context is null");
            }
            return;
        }

        if (token == null || token.isEmpty()) {
            if (callback != null) {
                callback.onFailure("Authentication token is missing");
            }
            return;
        }

        if (patientDocumentRequest == null || patientDocumentRequest.getDocument() == null) {
            if (callback != null) {
                callback.onFailure("Document file is missing");
            }
            return;
        }

        if (!patientDocumentRequest.getDocument().exists()) {
            if (callback != null) {
                callback.onFailure("Document file does not exist");
            }
            return;
        }

        try {
            // ✅ Create RequestBody with correct MediaType
            RequestBody docTypeRB = RequestBody.create(
                    MediaType.parse("text/plain"),
                    patientDocumentRequest.getDocument_type()
            );

            RequestBody patientIdRB = RequestBody.create(
                    MediaType.parse("text/plain"),
                    patientDocumentRequest.getPatient_id()
            );

            RequestBody doctorIdRB = RequestBody.create(
                    MediaType.parse("text/plain"),
                    patientDocumentRequest.getDoctor_id() != null
                            ? patientDocumentRequest.getDoctor_id()
                            : ""
            );

            // ✅ Determine correct MIME type
            String mimeType = getMimeType(patientDocumentRequest.getDocument().getName());
            RequestBody fileReqBody = RequestBody.create(
                    MediaType.parse(mimeType),
                    patientDocumentRequest.getDocument()
            );

            // ✅ Create MultipartBody.Part with correct field name
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(
                    "document",  // This should match your backend field name
                    patientDocumentRequest.getDocument().getName(),
                    fileReqBody
            );

            Log.d(TAG, "Uploading document:");
            Log.d(TAG, "  File: " + patientDocumentRequest.getDocument().getName());
            Log.d(TAG, "  Size: " + patientDocumentRequest.getDocument().length() + " bytes");
            Log.d(TAG, "  Type: " + patientDocumentRequest.getDocument_type());
            Log.d(TAG, "  Patient ID: " + patientDocumentRequest.getPatient_id());

            // ✅ Make API call
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<UserResponse> call = apiService.uploadDocument(
                    "Bearer " + token,
                    filePart,
                    docTypeRB,
                    patientIdRB,
                    doctorIdRB
            );

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Upload successful: " + response.body().getMessage());
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        String errorMsg = "Server error: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                errorMsg += " - " + errorBody;
                                Log.e(TAG, "Error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, errorMsg);
                        if (callback != null) {
                            callback.onFailure(errorMsg);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    String errorMsg = "Network error: " + t.getMessage();
                    Log.e(TAG, errorMsg, t);
                    if (callback != null) {
                        callback.onFailure(errorMsg);
                    }
                }
            });

        } catch (Exception e) {
            String errorMsg = "Error preparing upload: " + e.getMessage();
            Log.e(TAG, errorMsg, e);
            e.printStackTrace();
            if (callback != null) {
                callback.onFailure(errorMsg);
            }
        }
    }

    /**
     * Get MIME type from file extension
     */
    private String getMimeType(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1).toLowerCase();
        }

        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "pdf":
                return "application/pdf";
            default:
                return "image/*";
        }
    }
}
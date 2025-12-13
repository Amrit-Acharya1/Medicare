package com.acharyaamrit.medicare.common.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.Notice;
import com.acharyaamrit.medicare.common.model.response.NoticeResponse;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "medicare_notifications";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        if (message.getNotification() != null) {
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            showNotification(title, body);

            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);

            fetchNotices(token);
        }


        // Check if message contains a data payload
        if (message.getData().size() > 0) {
            // Handle data payload here
        }

    }

    private void fetchNotices(String token) {
        try {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<NoticeResponse> call = apiService.getNotices("Bearer " + token);

            call.enqueue(new Callback<NoticeResponse>() {
                @Override
                public void onResponse(Call<NoticeResponse> call, Response<NoticeResponse> response) {
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Notice> notices = response.body().getNotice();


                            if (notices != null && !notices.isEmpty()) {
                                // Execute database operations on background thread
                                new Thread(() -> {
                                    DatabaseHelper dbHelper = null;
                                    try {
                                        dbHelper = new DatabaseHelper(getApplicationContext());
                                        dbHelper.deleteNotice();
                                        dbHelper.insertNoticesBatchSafe(dbHelper, notices);
                                    } finally {
                                        if (dbHelper != null) {
                                            dbHelper.close();
                                        }
                                    }
                                }).start();
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onFailure(Call<NoticeResponse> call, Throwable t) {

                }
            });

        } catch (Exception e) {
        }
    }

    private void showNotification(String title, String body) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicare Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for Medicare app");
            notificationManager.createNotificationChannel(channel);
        }



        // Build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo) // Add your icon
                        .setContentTitle(title)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                      ;

        notificationManager.notify(0, notificationBuilder.build());
    }




}

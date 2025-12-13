package com.acharyaamrit.medicare.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.adapter.notification.NotificationAdapter;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.Notice;
import com.acharyaamrit.medicare.common.model.response.NoticeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private CardView backButtonCard;
    private RecyclerView notification_recycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Notice> noticeList;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButtonCard = findViewById(R.id.backButtonCard);
        notification_recycler = findViewById(R.id.notification_recycler);

        notification_recycler.setLayoutManager(new LinearLayoutManager(this));

        backButtonCard.setOnClickListener(v -> {finish();});

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        noticeList = databaseHelper.fetchAllNotice();

        NotificationAdapter adapter = new NotificationAdapter(noticeList, this);
        notification_recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
            String token = sharedPreferences.getString("token", null);

            fetchNotices(token);
        });
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

                                        // After successful database operations, restart activity on UI thread
                                        runOnUiThread(() -> {
                                            Intent intent = getIntent();
                                            finish();
                                            overridePendingTransition(0, 0);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0);
                                        });
                                    } finally {
                                        if (dbHelper != null) {
                                            dbHelper.close();
                                        }
                                    }
                                }).start();
                            } else {
                                // No notices to update, just stop refreshing
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        } else {
                            // API response not successful, stop refreshing
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<NoticeResponse> call, Throwable t) {
                    // API call failed, stop refreshing
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

        } catch (Exception e) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
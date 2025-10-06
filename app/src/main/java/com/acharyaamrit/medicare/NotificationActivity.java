package com.acharyaamrit.medicare;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.acharyaamrit.medicare.adapter.notification.NotificationAdapter;
import com.acharyaamrit.medicare.api.ApiClient;
import com.acharyaamrit.medicare.api.ApiService;
import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.Notice;
import com.acharyaamrit.medicare.model.response.NoticeResponse;

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
//        //get passed intent
//        Intent intent = getIntent();
//        String token = intent.getStringExtra("token");

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
            // Restart the same activity
            Intent intent = getIntent();
            finish();
            overridePendingTransition(0, 0); // No animation
            startActivity(intent);
            overridePendingTransition(0, 0); // No animation
            swipeRefreshLayout.setRefreshing(false);
        });


    }
}
package com.acharyaamrit.medicare.common;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter.TimelineAdapter;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.TimelineItem;

import java.util.List;

public class UserTimelineActivity extends AppCompatActivity {

    private CardView backButtonCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_timeline);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });
        backButtonCard = findViewById(R.id.backButtonCard);

        backButtonCard.setOnClickListener(v -> {finish();});

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        List<TimelineItem> timelineItems = dbHelper.fetchTimeline();

        TimelineAdapter timelineAdapter = new TimelineAdapter(timelineItems,this);
        RecyclerView recyclerView = findViewById(R.id.timelineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(timelineAdapter);


    }
}
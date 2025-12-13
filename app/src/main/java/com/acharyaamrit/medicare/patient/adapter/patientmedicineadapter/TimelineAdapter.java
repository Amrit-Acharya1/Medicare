package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    List<TimelineItem> timelineItemsList;
    Context context;

    public TimelineAdapter(List<TimelineItem> timelineItems, Context context) {
        this.timelineItemsList = timelineItems;
        this.context = context;
    }

    @NonNull
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        return new TimelineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.ViewHolder holder, int position) {
        TimelineItem timelineItem = timelineItemsList.get(position);

        // Alternate between left and right
        boolean isLeft = position % 2 == 0;

        if (isLeft) {
            // Show left card, hide right card
            holder.eventCardLeft.setVisibility(View.VISIBLE);
            holder.eventCardRight.setVisibility(View.GONE);

            holder.eventTextLeft.setText("Checkup With " + timelineItem.getDoctor_name());
            holder.dateTimeTextLeft.setText(timelineItem.getCreated_at());

            holder.btnViewLeft.setOnClickListener(v -> showBottomSheet(v, timelineItem));
        } else {
            // Show right card, hide left card
            holder.eventCardLeft.setVisibility(View.GONE);
            holder.eventCardRight.setVisibility(View.VISIBLE);

            holder.eventTextRight.setText("Checkup With " + timelineItem.getDoctor_name());
            holder.dateTimeTextRight.setText(timelineItem.getCreated_at());

            holder.btnViewRight.setOnClickListener(v -> showBottomSheet(v, timelineItem));
        }

        // Handle timeline lines visibility
        // First item - hide top line
        if (position == 0) {
            holder.timelineLineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineLineTop.setVisibility(View.VISIBLE);
        }

        // Last item - hide bottom line
        if (position == timelineItemsList.size() - 1) {
            holder.timelineLineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.timelineLineBottom.setVisibility(View.VISIBLE);
        }
    }

    private void showBottomSheet(View v, TimelineItem timelineItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        bottomSheetDialog.setContentView(R.layout.item_bottom_sheet_detail_preciption);

        RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.detailPrescription_medicine);
//        TextView totalPriceText = bottomSheetDialog.findViewById(R.id.totalPrice);
        TextView doctorName = bottomSheetDialog.findViewById(R.id.doctor_name);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
            DetailUserTimelineAdapter detailUserTimelineAdapter =
                    new DetailUserTimelineAdapter(timelineItem.getPreciption(), v.getContext());
            recyclerView.setAdapter(detailUserTimelineAdapter);
        }

//        double total_Price = 0;
//        for (Preciption p : timelineItem.getPreciption()) {
//            total_Price = total_Price + Double.parseDouble(p.getPrice());
//        }
//
//        if (totalPriceText != null) {
//            totalPriceText.setText(String.format("Rs. %.2f", total_Price));
//        }

        if (doctorName != null) {
            doctorName.setText("Checkup With " + timelineItem.getDoctor_name());
        }

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return timelineItemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Left side views
        CardView eventCardLeft;
        TextView eventTextLeft, dateTimeTextLeft;
        LinearLayout btnViewLeft;

        // Right side views
        CardView eventCardRight;
        TextView eventTextRight, dateTimeTextRight;
        LinearLayout btnViewRight;

        // Timeline elements
        View timelineLineTop;
        View timelineLineBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Left card views
            eventCardLeft = itemView.findViewById(R.id.eventCardLeft);
            eventTextLeft = itemView.findViewById(R.id.eventTextLeft);
            dateTimeTextLeft = itemView.findViewById(R.id.dateTimeTextLeft);
            btnViewLeft = itemView.findViewById(R.id.btnViewLeft);

            // Right card views
            eventCardRight = itemView.findViewById(R.id.eventCardRight);
            eventTextRight = itemView.findViewById(R.id.eventTextRight);
            dateTimeTextRight = itemView.findViewById(R.id.dateTimeTextRight);
            btnViewRight = itemView.findViewById(R.id.btnViewRight);

            // Timeline lines
            timelineLineTop = itemView.findViewById(R.id.timelineLineTop);
            timelineLineBottom = itemView.findViewById(R.id.timelineLineBottom);
        }
    }
}
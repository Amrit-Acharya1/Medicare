package com.acharyaamrit.medicare.patient.adapter.patientmedicineadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.model.TimelineItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class UserTimelineAdapter extends RecyclerView.Adapter<UserTimelineAdapter.ViewHolder> {

    List<TimelineItem> timelineItemsList;
    Context context;
    public UserTimelineAdapter(List<TimelineItem> timelineItems, Context context) {
        this.timelineItemsList = timelineItems;
    }

    public UserTimelineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usertimeline_home, parent, false);
        return new UserTimelineAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull UserTimelineAdapter.ViewHolder holder, int position) {
        TimelineItem timelineItem = timelineItemsList.get(position);
        holder.titleView.setText("Checkup With "+timelineItem.getDoctor_name());
        holder.timeView.setText(timelineItem.getCreated_at());
        holder.viewbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
                bottomSheetDialog.setContentView(R.layout.item_bottom_sheet_detail_preciption);

                // find RecyclerView from the bottom sheet layout
                RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.detailPrescription_medicine);
//                TextView totalPriceText = bottomSheetDialog.findViewById(R.id.totalPrice);
                TextView doctorName = bottomSheetDialog.findViewById(R.id.doctor_name);
                if (recyclerView != null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                    DetailUserTimelineAdapter detailUserTimelineAdapter =
                            new DetailUserTimelineAdapter(timelineItem.getPreciption(), v.getContext());
                    recyclerView.setAdapter(detailUserTimelineAdapter);
                }


//                double total_Price = 0;
//
//                for(Preciption p : timelineItem.getPreciption()){
//                    total_Price = total_Price + Double.parseDouble(p.getPrice());
//                }
//
//                totalPriceText.setText(String.format("Rs. %.2f", total_Price));

                doctorName.setText("Checkup With "+timelineItem.getDoctor_name());
                bottomSheetDialog.show();
            }
        });


    }

    public int getItemCount() {
        return Math.min(timelineItemsList.size(), 5);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleView, timeView;
        TextView viewbtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleView = itemView.findViewById(R.id.title);
            timeView = itemView.findViewById(R.id.time);
            viewbtn = itemView.findViewById(R.id.view_btn);
        }
    }

}

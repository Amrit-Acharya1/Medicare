package com.acharyaamrit.medicare.adapter.patienthomepageadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.model.TimelineItem;

import java.util.List;

public class UserTimeLineAdapter extends RecyclerView.Adapter<UserTimeLineAdapter.ViewHolder> {

    private final List<TimelineItem> timelineItem;
    private final Context context;

    public UserTimeLineAdapter(List<TimelineItem> timelineItem, Context context) {
        this.timelineItem = timelineItem;
        this.context = context;
    }

    @NonNull
    @Override
    public UserTimeLineAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usertimeline_home, parent, false);
        return new UserTimeLineAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserTimeLineAdapter.ViewHolder holder, int position) {
        TimelineItem item = timelineItem.get(position);

        // Set data directly from object
        holder.doctor_checkup.setText(item.getDoctor_name());
        holder.time.setText(item.getCreated_at());

        // Handle timeline connector visibility
        int total = timelineItem.size();

        if (total == 1) {
            // Only one item -> hide both lines
            holder.view_up.setVisibility(View.INVISIBLE);
            holder.view_down.setVisibility(View.INVISIBLE);
        } else if (position == 0) {
            // First item -> hide top line
            holder.view_up.setVisibility(View.INVISIBLE);
            holder.view_down.setVisibility(View.VISIBLE);
        } else if (position == total - 1) {
            // Last item -> hide bottom line
            holder.view_up.setVisibility(View.VISIBLE);
            holder.view_down.setVisibility(View.INVISIBLE);
        } else {
            // Middle items -> show both lines
            holder.view_up.setVisibility(View.VISIBLE);
            holder.view_down.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        // Show maximum 3 items
        return Math.min(timelineItem.size(), 3);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctor_checkup, time, view_btn;
        View view_up, view_down;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctor_checkup = itemView.findViewById(R.id.doctor_checkup);
            time = itemView.findViewById(R.id.time);
            view_btn = itemView.findViewById(R.id.view_btn);
            view_up = itemView.findViewById(R.id.timeline_line_up);
            view_down = itemView.findViewById(R.id.timeline_line_down);
        }
    }
}

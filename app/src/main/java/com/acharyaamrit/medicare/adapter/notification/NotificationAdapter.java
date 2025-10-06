package com.acharyaamrit.medicare.adapter.notification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.model.Notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<Notice> noticeList;
    private Context context;
    private String lastDisplayedDate = "";

    public NotificationAdapter(List<Notice> noticeList, Context context) {
        this.noticeList = noticeList;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notice notice = noticeList.get(position);
        String publishAt = notice.getPublished_at();
        String expireAt = notice.getExpires_at();

        // Check if notice has expired
        if (expireAt != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date expireDate = sdf.parse(expireAt);
                Date currentDate = new Date();

                if (expireDate != null && expireDate.before(currentDate)) {
                    // Notice has expired, hide it
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Make sure item is visible (in case of view recycling)
        holder.itemView.setVisibility(View.VISIBLE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        holder.notification_title.setText(notice.getTitle());
        holder.notification_description.setText(notice.getDescription());

        // Handle date header visibility
        if (publishAt != null) {
            String currentDateCategory = getDateCategory(publishAt);

            // Check if this is the first item or if date category changed
            if (position == 0) {
                // First item, always show date
                holder.notification_date.setVisibility(View.VISIBLE);
                holder.notification_date.setText(currentDateCategory);
                lastDisplayedDate = currentDateCategory;
            } else {
                // Get previous notice's date category
                String previousDateCategory = "";

                // Find the previous visible (non-expired) notice
                for (int i = position - 1; i >= 0; i--) {
                    Notice prevNotice = noticeList.get(i);
                    String prevPublishAt = prevNotice.getPublished_at();

                    // Check if previous notice is not expired
                    if (prevPublishAt != null && !isExpired(prevNotice.getExpires_at())) {
                        previousDateCategory = getDateCategory(prevPublishAt);
                        break;
                    }
                }

                // Show date header only if category changed
                if (!currentDateCategory.equals(previousDateCategory)) {
                    holder.notification_date.setVisibility(View.VISIBLE);
                    holder.notification_date.setText(currentDateCategory);
                } else {
                    holder.notification_date.setVisibility(View.GONE);
                }
            }
        } else {
            holder.notification_date.setVisibility(View.GONE);
        }
    }

    private boolean isExpired(String expireAt) {
        if (expireAt == null) return false;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date expireDate = sdf.parse(expireAt);
            Date currentDate = new Date();
            return expireDate != null && expireDate.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getDateCategory(String publishAt) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date publishDate = sdf.parse(publishAt);
            Date currentDate = new Date();

            if (publishDate == null) return publishAt;

            long diffInMillis = currentDate.getTime() - publishDate.getTime();
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

            Calendar publishCalendar = Calendar.getInstance();
            publishCalendar.setTime(publishDate);

            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.setTime(currentDate);

            // Check if today
            if (publishCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    publishCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
                return "Today";
            }

            // Check if yesterday
            todayCalendar.add(Calendar.DAY_OF_YEAR, -1);
            if (publishCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) &&
                    publishCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)) {
                return "Yesterday";
            }

            // Between 2-7 days ago
            if (diffInDays >= 2 && diffInDays <= 7) {
                return diffInDays + " days ago";
            }

            // More than 7 days, show actual date
            SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return displayFormat.format(publishDate);

        } catch (ParseException e) {
            e.printStackTrace();
            return publishAt;
        }
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView notification_title, notification_description, notification_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notification_title = itemView.findViewById(R.id.notification_title);
            notification_description = itemView.findViewById(R.id.notification_description);
            notification_date = itemView.findViewById(R.id.notification_date);
        }
    }
}
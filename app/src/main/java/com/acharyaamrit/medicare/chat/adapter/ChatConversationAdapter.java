package com.acharyaamrit.medicare.chat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.chat.model.ChatConversation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {
    private List<ChatConversation> conversations = new ArrayList<>();
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(ChatConversation conversation);
    }

    public ChatConversationAdapter(OnConversationClickListener listener) {
        this.listener = listener;
    }

    public void setConversations(List<ChatConversation> conversations) {
        this.conversations = conversations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatConversation conversation = conversations.get(position);
        holder.bind(conversation, listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar;
        android.widget.ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvUserType;
        TextView tvLastMessage;
        TextView tvTime;
        TextView tvUnreadBadge;
        View onlineIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tv_avatar);
            ivProfileImage = itemView.findViewById(R.id.iv_profile_image);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvUserType = itemView.findViewById(R.id.tv_user_type);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvUnreadBadge = itemView.findViewById(R.id.tv_unread_badge);
            onlineIndicator = itemView.findViewById(R.id.online_indicator);
        }

        public void bind(ChatConversation conversation, OnConversationClickListener listener) {
            // Set avatar (first letter of name or profile image)
            if (conversation.getProfileImage() != null && !conversation.getProfileImage().isEmpty()) {
                // Load profile image with Glide
                ivProfileImage.setVisibility(View.VISIBLE);
                tvAvatar.setVisibility(View.GONE);
                com.bumptech.glide.Glide.with(itemView.getContext())
                        .load(conversation.getProfileImage())
                        .placeholder(R.drawable.bottom_selected_back)
                        .error(R.drawable.bottom_selected_back)
                        .circleCrop()
                        .into(ivProfileImage);
            } else {
                // Show text avatar
                ivProfileImage.setVisibility(View.GONE);
                tvAvatar.setVisibility(View.VISIBLE);
                if (conversation.getOtherUserName() != null && !conversation.getOtherUserName().isEmpty()) {
                    tvAvatar.setText(String.valueOf(conversation.getOtherUserName().charAt(0)).toUpperCase());
                }
            }

            // Set user name and type
            tvUserName.setText(conversation.getOtherUserName());
            tvUserType.setText(conversation.getOtherUserType().equals("doctor") ? "Doctor" : "Pharmacy");

            // Set last message
            tvLastMessage.setText(conversation.getLastMessage());

            // Set time
            tvTime.setText(formatTimestamp(conversation.getLastMessageTime()));

            // Set unread badge
            if (conversation.getUnreadCount() > 0) {
                tvUnreadBadge.setVisibility(View.VISIBLE);
                tvUnreadBadge.setText(String.valueOf(conversation.getUnreadCount()));
            } else {
                tvUnreadBadge.setVisibility(View.GONE);
            }

            // Set online indicator
            if (conversation.isOnline()) {
                onlineIndicator.setVisibility(View.VISIBLE);
            } else {
                onlineIndicator.setVisibility(View.GONE);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConversationClick(conversation);
                }
            });
        }

        private String formatTimestamp(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;

            if (diff < TimeUnit.MINUTES.toMillis(1)) {
                return "Just now";
            } else if (diff < TimeUnit.HOURS.toMillis(1)) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                return minutes + "m ago";
            } else if (diff < TimeUnit.DAYS.toMillis(1)) {
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                return hours + "h ago";
            } else if (diff < TimeUnit.DAYS.toMillis(2)) {
                return "Yesterday";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
                return sdf.format(new Date(timestamp));
            }
        }
    }
}

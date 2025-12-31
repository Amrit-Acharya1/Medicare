package com.acharyaamrit.medicare.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.chat.adapter.ChatMessageAdapter;
import com.acharyaamrit.medicare.chat.firebase.FirebaseChatHelper;
import com.acharyaamrit.medicare.chat.model.ChatMessage;
import com.acharyaamrit.medicare.chat.model.ChatUser;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rvMessages;
    private EditText etMessage;
    private View btnSend;
    private TextView tvUserName, tvUserStatus, tvUserAvatar;
    private View onlineIndicator;
    private ImageView btnBack;

    private ChatMessageAdapter adapter;
    private FirebaseChatHelper firebaseHelper;

    private String currentUserId;
    private String currentUserName;
    private String currentUserType;
    private String otherUserId;
    private String otherUserName;
    private String otherUserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        initializeViews();
        loadUserData();
        setupRecyclerView();
        loadMessages();
        setupListeners();
        updateOnlineStatus(true);
    }

    private void initializeViews() {
        rvMessages = findViewById(R.id.rv_messages);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserStatus = findViewById(R.id.tv_user_status);
        tvUserAvatar = findViewById(R.id.tv_user_avatar);
        onlineIndicator = findViewById(R.id.online_indicator);
        btnBack = findViewById(R.id.btn_back);

        firebaseHelper = new FirebaseChatHelper();
    }

    private void loadUserData() {
        // Get current user data
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String userType = sharedPreferences.getString("user_type", "");
        DatabaseHelper databaseHelper = new DatabaseHelper(this);

        if ("2".equals(userType)) {
            // Doctor
            String token = sharedPreferences.getString("token", "");
            Doctor doctor = databaseHelper.getDoctorByToken(token);
            if (doctor != null) {
                currentUserId = String.valueOf(doctor.getDoctor_id());
                currentUserName = doctor.getName();
                currentUserType = "doctor";

                // Store image and FCM token in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_image", doctor.getImage() != null ? doctor.getImage() : "");
                editor.putString("chat_fcm_token", doctor.getFcm_token() != null ? doctor.getFcm_token() : "");
                editor.apply();
            }
        } else if ("4".equals(userType)) {
            // Pharmacy
            String token = sharedPreferences.getString("token", "");
            Pharmacy pharmacy = databaseHelper.getPharmacyByToken(token);
            if (pharmacy != null) {
                currentUserId = String.valueOf(pharmacy.getPharmacy_id());
                currentUserName = pharmacy.getName();
                currentUserType = "pharmacy";

                // Store image and FCM token in SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_image", pharmacy.getImage() != null ? pharmacy.getImage() : "");
                editor.putString("chat_fcm_token", pharmacy.getFcm_token() != null ? pharmacy.getFcm_token() : "");
                editor.apply();
            }
        }

        // Get other user data from intent
        otherUserId = getIntent().getStringExtra("otherUserId");
        otherUserName = getIntent().getStringExtra("otherUserName");
        otherUserType = getIntent().getStringExtra("otherUserType");

        // Set header info
        if (otherUserName != null) {
            tvUserName.setText(otherUserName);
            tvUserAvatar.setText(String.valueOf(otherUserName.charAt(0)).toUpperCase());
        }

        // Load other user's online status
        loadUserOnlineStatus();
    }

    private void loadUserOnlineStatus() {
        firebaseHelper.fetchUserInfo(otherUserId, user -> {
            runOnUiThread(() -> {
                // Load profile image
                ImageView ivUserProfile = findViewById(R.id.iv_user_profile);
                if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                    ivUserProfile.setVisibility(View.VISIBLE);
                    tvUserAvatar.setVisibility(View.GONE);
                    com.bumptech.glide.Glide.with(this)
                            .load(user.getProfileImage())
                            .placeholder(R.drawable.bottom_selected_back)
                            .error(R.drawable.bottom_selected_back)
                            .circleCrop()
                            .into(ivUserProfile);
                } else {
                    ivUserProfile.setVisibility(View.GONE);
                    tvUserAvatar.setVisibility(View.VISIBLE);
                }

                // Update online status
                if (user.isOnline()) {
                    tvUserStatus.setText("Online");
                    tvUserStatus.setTextColor(0xFF4CAF50);
                    onlineIndicator.setVisibility(View.VISIBLE);
                } else {
                    tvUserStatus.setText(formatLastSeen(user.getLastSeen()));
                    tvUserStatus.setTextColor(0xFF888888);
                    onlineIndicator.setVisibility(View.GONE);
                }
            });
        });
    }

    private String formatLastSeen(long lastSeen) {
        long diff = System.currentTimeMillis() - lastSeen;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1)
            return "Just now";
        if (minutes < 60)
            return "Last seen " + minutes + "m ago";
        if (hours < 24)
            return "Last seen " + hours + "h ago";
        if (days == 1)
            return "Last seen yesterday";
        return "Last seen " + days + " days ago";
    }

    private void setupRecyclerView() {
        adapter = new ChatMessageAdapter(currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(adapter);
    }

    private void loadMessages() {
        firebaseHelper.getMessages(currentUserId, otherUserId, new FirebaseChatHelper.OnMessagesListener() {
            @Override
            public void onMessages(List<ChatMessage> messages) {
                runOnUiThread(() -> {
                    adapter.setMessages(messages);
                    if (messages.size() > 0) {
                        rvMessages.scrollToPosition(messages.size() - 1);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(
                        () -> Toast.makeText(ChatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show());
            }
        });

        // Mark messages as read
        firebaseHelper.markMessagesAsRead(currentUserId, otherUserId);
    }

    private void setupListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        ChatMessage message = new ChatMessage();
        message.setSenderId(currentUserId);
        message.setReceiverId(otherUserId);
        message.setMessage(messageText);
        message.setTimestamp(System.currentTimeMillis());
        message.setRead(false);
        message.setSenderName(currentUserName);

        firebaseHelper.sendMessage(message, new FirebaseChatHelper.OnCompleteListener() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    etMessage.setText("");
                    // Message will be added automatically through the listener
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(
                        () -> Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateOnlineStatus(boolean isOnline) {
        if (currentUserId != null) {
            firebaseHelper.updateOnlineStatus(currentUserId, isOnline);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateOnlineStatus(true);
        if (otherUserId != null) {
            firebaseHelper.markMessagesAsRead(currentUserId, otherUserId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateOnlineStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateOnlineStatus(false);
    }
}

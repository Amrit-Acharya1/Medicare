package com.acharyaamrit.medicare.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.chat.adapter.ChatConversationAdapter;
import com.acharyaamrit.medicare.chat.firebase.FirebaseChatHelper;
import com.acharyaamrit.medicare.chat.model.ChatConversation;
import com.acharyaamrit.medicare.chat.model.ChatUser;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.pharmacy.model.Pharmacy;
import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView rvConversations;
    private EditText etSearch;
    private View emptyState;
    private LottieAnimationView loadingLottie;
    private ImageView btnBack;

    private ChatConversationAdapter adapter;
    private FirebaseChatHelper firebaseHelper;

    private String currentUserId;
    private String currentUserName;
    private String currentUserType;
    private String clinicId;

    private List<ChatConversation> allConversations = new ArrayList<>();
    private List<ChatUser> searchResults = new ArrayList<>();

    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });

        initializeViews();
        loadUserData();
        setupRecyclerView();
        loadConversations();
        setupListeners();
        createOrUpdateCurrentUser();
    }

    private void initializeViews() {
        rvConversations = findViewById(R.id.rv_conversations);
        etSearch = findViewById(R.id.et_search);
        emptyState = findViewById(R.id.empty_state);
        loadingLottie = findViewById(R.id.loading_lottie);
        btnBack = findViewById(R.id.btn_back);

        firebaseHelper = new FirebaseChatHelper();
    }

    private void loadUserData() {
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
                clinicId = doctor.getClicnic();

                // Store image and FCM token in SharedPreferences for later use
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
                clinicId = pharmacy.getClicnic();

                // Store image and FCM token in SharedPreferences for later use
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_image", pharmacy.getImage() != null ? pharmacy.getImage() : "");
                editor.putString("chat_fcm_token", pharmacy.getFcm_token() != null ? pharmacy.getFcm_token() : "");
                editor.apply();
            }
        }
    }

    private void createOrUpdateCurrentUser() {
        // Create or update current user in Firebase
        SharedPreferences sharedPreferences = getSharedPreferences("user_preference", MODE_PRIVATE);
        String fcmToken = sharedPreferences.getString("chat_fcm_token", "");
        String profileImage = sharedPreferences.getString("user_image", "");

        android.util.Log.d("ChatListActivity", "FCM Token from Database: " + fcmToken);
        android.util.Log.d("ChatListActivity", "Profile Image: " + profileImage);
        android.util.Log.d("ChatListActivity", "Current User ID: " + currentUserId);
        android.util.Log.d("ChatListActivity", "Current User Name: " + currentUserName);

        ChatUser currentUser = new ChatUser();
        currentUser.setUserId(currentUserId);
        currentUser.setName(currentUserName);
        currentUser.setUserType(currentUserType);
        currentUser.setClinicId(clinicId);
        currentUser.setProfileImage(profileImage);
        currentUser.setOnline(true);
        currentUser.setLastSeen(System.currentTimeMillis());
        currentUser.setFcmToken(fcmToken);

        android.util.Log.d("ChatListActivity", "Uploading to Firebase - FCM Token: " + currentUser.getFcmToken());

        firebaseHelper.createOrUpdateUser(currentUser, new FirebaseChatHelper.OnCompleteListener() {
            @Override
            public void onSuccess() {
                // User created/updated successfully
                Toast.makeText(ChatListActivity.this, "Chat initialized", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(ChatListActivity.this, "Error initializing chat: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ChatConversationAdapter(conversation -> {
            // Open chat with this user
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("otherUserId", conversation.getOtherUserId());
            intent.putExtra("otherUserName", conversation.getOtherUserName());
            intent.putExtra("otherUserType", conversation.getOtherUserType());
            startActivity(intent);
        });

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        rvConversations.setAdapter(adapter);
    }

    private void loadConversations() {
        loadConversations(true);
    }

    private void loadConversations(boolean showLoading) {
        if (showLoading) {
            loadingLottie.setVisibility(View.VISIBLE);
        }
        emptyState.setVisibility(View.GONE);

        firebaseHelper.getConversations(currentUserId, new FirebaseChatHelper.OnConversationsListener() {
            @Override
            public void onConversations(List<ChatConversation> conversations) {
                runOnUiThread(() -> {
                    loadingLottie.setVisibility(View.GONE);
                    allConversations = conversations;

                    if (conversations.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        rvConversations.setVisibility(View.GONE);
                    } else {
                        emptyState.setVisibility(View.GONE);
                        rvConversations.setVisibility(View.VISIBLE);
                        adapter.setConversations(conversations);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    loadingLottie.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                    rvConversations.setVisibility(View.GONE);
                    Toast.makeText(ChatListActivity.this, "Error loading conversations", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Debounce handler for search
        final android.os.Handler searchHandler = new android.os.Handler();
        final Runnable[] searchRunnable = new Runnable[1];

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search if user is still typing
                if (searchRunnable[0] != null) {
                    searchHandler.removeCallbacks(searchRunnable[0]);
                }

                String query = s.toString().trim();

                if (query.isEmpty()) {
                    // Show conversations immediately when search is cleared
                    loadingLottie.setVisibility(View.GONE);
                    emptyState.setVisibility(View.GONE);
                    rvConversations.setVisibility(View.VISIBLE);
                    adapter.setConversations(allConversations);

                    // Show empty state if no conversations
                    if (allConversations.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        rvConversations.setVisibility(View.GONE);
                    }
                } else {
                    // Debounce search - wait 500ms after user stops typing
                    searchRunnable[0] = () -> searchUsers(query);
                    searchHandler.postDelayed(searchRunnable[0], 500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchUsers(String query) {
        // First, search in same clinic if user belongs to a clinic
        if (clinicId != null && !clinicId.isEmpty() && !clinicId.equals("null")) {
            firebaseHelper.getSameClinicUsers(clinicId, currentUserId, currentUserType,
                    new FirebaseChatHelper.OnUsersListener() {
                        @Override
                        public void onUsers(List<ChatUser> users) {
                            List<ChatUser> filteredUsers = new ArrayList<>();
                            for (ChatUser user : users) {
                                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                                    filteredUsers.add(user);
                                }
                            }

                            // If no results in same clinic, search all users
                            if (filteredUsers.isEmpty()) {
                                searchAllUsers(query);
                            } else {
                                displaySearchResults(filteredUsers);
                            }
                        }

                        @Override
                        public void onError(String error) {
                            searchAllUsers(query);
                        }
                    });
        } else {
            // No clinic, search all users
            searchAllUsers(query);
        }
    }

    private void searchAllUsers(String query) {
        firebaseHelper.searchUsers(query, currentUserId, new FirebaseChatHelper.OnUsersListener() {
            @Override
            public void onUsers(List<ChatUser> users) {
                runOnUiThread(() -> {
                    if (users.isEmpty()) {
                        Toast.makeText(ChatListActivity.this, "No users found matching '" + query + "'",
                                Toast.LENGTH_SHORT).show();
                    }
                    displaySearchResults(users);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatListActivity.this, "Search error: " + error, Toast.LENGTH_LONG).show();
                    emptyState.setVisibility(View.VISIBLE);
                    rvConversations.setVisibility(View.GONE);
                });
            }
        });
    }

    private void displaySearchResults(List<ChatUser> users) {
        runOnUiThread(() -> {
            // Hide loading animation
            loadingLottie.setVisibility(View.GONE);

            // Convert users to conversations for display
            List<ChatConversation> searchConversations = new ArrayList<>();
            for (ChatUser user : users) {
                ChatConversation conversation = new ChatConversation();
                conversation.setOtherUserId(user.getUserId());
                conversation.setOtherUserName(user.getName());
                conversation.setOtherUserType(user.getUserType());
                conversation.setLastMessage("Start a conversation");
                conversation.setLastMessageTime(System.currentTimeMillis());
                conversation.setUnreadCount(0);
                conversation.setOnline(user.isOnline());
                conversation.setProfileImage(user.getProfileImage());
                searchConversations.add(conversation);
            }

            if (searchConversations.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);
                rvConversations.setVisibility(View.GONE);
            } else {
                emptyState.setVisibility(View.GONE);
                rvConversations.setVisibility(View.VISIBLE);
                adapter.setConversations(searchConversations);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update online status
        if (currentUserId != null) {
            firebaseHelper.updateOnlineStatus(currentUserId, true);
        }
        // Reload conversations - only show loading on first load
        loadConversations(isFirstLoad);
        isFirstLoad = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentUserId != null) {
            firebaseHelper.updateOnlineStatus(currentUserId, false);
        }
    }
}

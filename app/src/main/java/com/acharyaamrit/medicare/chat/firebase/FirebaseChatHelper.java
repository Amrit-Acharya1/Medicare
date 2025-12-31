package com.acharyaamrit.medicare.chat.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.acharyaamrit.medicare.chat.model.ChatConversation;
import com.acharyaamrit.medicare.chat.model.ChatMessage;
import com.acharyaamrit.medicare.chat.model.ChatUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseChatHelper {
    private static final String TAG = "FirebaseChatHelper";
    private static final String ROOT_PATH = "medicare-chat";
    private static final String USERS_PATH = "users";
    private static final String CONVERSATIONS_PATH = "conversations";
    private static final String MESSAGES_PATH = "messages";

    private final DatabaseReference databaseReference;

    public FirebaseChatHelper() {
        FirebaseDatabase database = FirebaseDatabase
                .getInstance("https://medicare-500bb-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.databaseReference = database.getReference(ROOT_PATH);
    }

    // Create or update user in Firebase
    public void createOrUpdateUser(ChatUser user, OnCompleteListener listener) {
        Log.d(TAG, "Creating/updating user: " + user.getName() + " (ID: " + user.getUserId() + ", Type: "
                + user.getUserType() + ")");
        databaseReference.child(USERS_PATH)
                .child(user.getUserId())
                .setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User created/updated successfully: " + user.getUserId());
                    if (listener != null)
                        listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating/updating user: " + user.getUserId(), e);
                    Log.e(TAG, "Error message: " + e.getMessage());
                    if (listener != null)
                        listener.onFailure(e.getMessage());
                });
    }

    // Send a message
    public void sendMessage(ChatMessage message, OnCompleteListener listener) {
        String conversationId = getConversationId(message.getSenderId(), message.getReceiverId());
        String messageId = databaseReference.child(MESSAGES_PATH).child(conversationId).push().getKey();

        if (messageId == null) {
            if (listener != null)
                listener.onFailure("Failed to generate message ID");
            return;
        }

        message.setMessageId(messageId);

        // Save message
        databaseReference.child(MESSAGES_PATH)
                .child(conversationId)
                .child(messageId)
                .setValue(message)
                .addOnSuccessListener(aVoid -> {
                    // Update conversation for both users
                    updateConversation(message);
                    if (listener != null)
                        listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending message", e);
                    if (listener != null)
                        listener.onFailure(e.getMessage());
                });
    }

    // Update conversation metadata
    private void updateConversation(ChatMessage message) {
        String conversationId = getConversationId(message.getSenderId(), message.getReceiverId());

        // Update sender's conversation
        Map<String, Object> senderConversation = new HashMap<>();
        senderConversation.put("lastMessage", message.getMessage());
        senderConversation.put("lastMessageTime", message.getTimestamp());
        senderConversation.put("otherUserId", message.getReceiverId());

        databaseReference.child(CONVERSATIONS_PATH)
                .child(message.getSenderId())
                .child(message.getReceiverId())
                .updateChildren(senderConversation);

        // Update receiver's conversation with unread count
        databaseReference.child(CONVERSATIONS_PATH)
                .child(message.getReceiverId())
                .child(message.getSenderId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int unreadCount = 0;
                        if (snapshot.child("unreadCount").exists()) {
                            unreadCount = snapshot.child("unreadCount").getValue(Integer.class);
                        }

                        Map<String, Object> receiverConversation = new HashMap<>();
                        receiverConversation.put("lastMessage", message.getMessage());
                        receiverConversation.put("lastMessageTime", message.getTimestamp());
                        receiverConversation.put("otherUserId", message.getSenderId());
                        receiverConversation.put("unreadCount", unreadCount + 1);

                        databaseReference.child(CONVERSATIONS_PATH)
                                .child(message.getReceiverId())
                                .child(message.getSenderId())
                                .updateChildren(receiverConversation);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error updating receiver conversation", error.toException());
                    }
                });
    }

    // Get messages for a conversation
    public void getMessages(String currentUserId, String otherUserId, OnMessagesListener listener) {
        String conversationId = getConversationId(currentUserId, otherUserId);

        databaseReference.child(MESSAGES_PATH)
                .child(conversationId)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ChatMessage> messages = new ArrayList<>();
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                        if (listener != null)
                            listener.onMessages(messages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting messages", error.toException());
                        if (listener != null)
                            listener.onError(error.getMessage());
                    }
                });
    }

    // Get conversations for a user
    public void getConversations(String userId, OnConversationsListener listener) {
        Log.d(TAG, "Getting conversations for user: " + userId);
        databaseReference.child(CONVERSATIONS_PATH)
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Conversations snapshot received. Count: " + snapshot.getChildrenCount());
                        List<ChatConversation> conversations = new ArrayList<>();

                        for (DataSnapshot convSnapshot : snapshot.getChildren()) {
                            String otherUserId = convSnapshot.getKey();
                            String lastMessage = convSnapshot.child("lastMessage").getValue(String.class);
                            Long lastMessageTime = convSnapshot.child("lastMessageTime").getValue(Long.class);
                            Integer unreadCount = convSnapshot.child("unreadCount").getValue(Integer.class);

                            Log.d(TAG, "Processing conversation with user: " + otherUserId);

                            if (lastMessage != null && lastMessageTime != null) {
                                // Fetch other user's info
                                fetchUserInfo(otherUserId, otherUser -> {
                                    if (otherUser != null) {
                                        ChatConversation conversation = new ChatConversation();
                                        conversation.setConversationId(getConversationId(userId, otherUserId));
                                        conversation.setOtherUserId(otherUserId);
                                        conversation.setOtherUserName(otherUser.getName());
                                        conversation.setOtherUserType(otherUser.getUserType());
                                        conversation.setLastMessage(lastMessage);
                                        conversation.setLastMessageTime(lastMessageTime);
                                        conversation.setUnreadCount(unreadCount != null ? unreadCount : 0);
                                        conversation.setProfileImage(otherUser.getProfileImage());
                                        conversation.setOnline(otherUser.isOnline());

                                        conversations.add(conversation);
                                        Log.d(TAG, "Added conversation. Total: " + conversations.size());

                                        // Notify listener after processing all conversations
                                        if (conversations.size() == (int) snapshot.getChildrenCount()) {
                                            Log.d(TAG, "All conversations processed. Notifying listener.");
                                            if (listener != null)
                                                listener.onConversations(conversations);
                                        }
                                    } else {
                                        Log.w(TAG, "User info is null for userId: " + otherUserId);
                                    }
                                });
                            }
                        }

                        // Handle empty conversations
                        if (snapshot.getChildrenCount() == 0) {
                            Log.d(TAG, "No conversations found. Returning empty list.");
                            if (listener != null)
                                listener.onConversations(conversations);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting conversations: " + error.getMessage(), error.toException());
                        Log.e(TAG, "Error code: " + error.getCode());
                        Log.e(TAG, "Error details: " + error.getDetails());
                        if (listener != null)
                            listener.onError(error.getMessage() + " (Code: " + error.getCode() + ")");
                    }
                });
    }

    // Fetch user info
    public void fetchUserInfo(String userId, OnUserListener listener) {
        databaseReference.child(USERS_PATH)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ChatUser user = snapshot.getValue(ChatUser.class);
                        if (user != null && listener != null) {
                            listener.onUser(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error fetching user info", error.toException());
                    }
                });
    }

    // Search users by name
    public void searchUsers(String query, String currentUserId, OnUsersListener listener) {
        Log.d(TAG, "Searching users with query: " + query + ", currentUserId: " + currentUserId);
        databaseReference.child(USERS_PATH)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "Search snapshot received. Total users in DB: " + snapshot.getChildrenCount());
                        List<ChatUser> users = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            ChatUser user = userSnapshot.getValue(ChatUser.class);
                            if (user != null && !user.getUserId().equals(currentUserId)) {
                                Log.d(TAG, "Checking user: " + user.getName() + " (ID: " + user.getUserId() + ")");
                                if (user.getName().toLowerCase().contains(query.toLowerCase())) {
                                    users.add(user);
                                    Log.d(TAG, "User matched query: " + user.getName());
                                }
                            }
                        }
                        Log.d(TAG, "Search complete. Found " + users.size() + " matching users");
                        if (listener != null)
                            listener.onUsers(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error searching users: " + error.getMessage(), error.toException());
                        Log.e(TAG, "Error code: " + error.getCode());
                        Log.e(TAG, "Error details: " + error.getDetails());
                        if (listener != null)
                            listener.onError(error.getMessage() + " (Code: " + error.getCode() + ")");
                    }
                });
    }

    // Get users from same clinic
    public void getSameClinicUsers(String clinicId, String currentUserId, String currentUserType,
            OnUsersListener listener) {
        databaseReference.child(USERS_PATH)
                .orderByChild("clinicId")
                .equalTo(clinicId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ChatUser> users = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            ChatUser user = userSnapshot.getValue(ChatUser.class);
                            if (user != null && !user.getUserId().equals(currentUserId)) {
                                // Only show opposite user types (doctors see pharmacies, pharmacies see
                                // doctors)
                                if (!user.getUserType().equals(currentUserType)) {
                                    users.add(user);
                                }
                            }
                        }
                        if (listener != null)
                            listener.onUsers(users);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error getting same clinic users", error.toException());
                        if (listener != null)
                            listener.onError(error.getMessage());
                    }
                });
    }

    // Update online status
    public void updateOnlineStatus(String userId, boolean isOnline) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("isOnline", isOnline);
        updates.put("lastSeen", System.currentTimeMillis());

        databaseReference.child(USERS_PATH)
                .child(userId)
                .updateChildren(updates);
    }

    // Mark messages as read
    public void markMessagesAsRead(String currentUserId, String otherUserId) {
        // Reset unread count in conversation
        databaseReference.child(CONVERSATIONS_PATH)
                .child(currentUserId)
                .child(otherUserId)
                .child("unreadCount")
                .setValue(0);

        // Mark all messages as read
        String conversationId = getConversationId(currentUserId, otherUserId);
        databaseReference.child(MESSAGES_PATH)
                .child(conversationId)
                .orderByChild("receiverId")
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                            messageSnapshot.getRef().child("isRead").setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error marking messages as read", error.toException());
                    }
                });
    }

    // Generate conversation ID (consistent for both users)
    private String getConversationId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0
                ? userId1 + "_" + userId2
                : userId2 + "_" + userId1;
    }

    // Listener interfaces
    public interface OnCompleteListener {
        void onSuccess();

        void onFailure(String error);
    }

    public interface OnMessagesListener {
        void onMessages(List<ChatMessage> messages);

        void onError(String error);
    }

    public interface OnConversationsListener {
        void onConversations(List<ChatConversation> conversations);

        void onError(String error);
    }

    public interface OnUserListener {
        void onUser(ChatUser user);
    }

    public interface OnUsersListener {
        void onUsers(List<ChatUser> users);

        void onError(String error);
    }
}

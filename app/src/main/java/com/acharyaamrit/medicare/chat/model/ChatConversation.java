package com.acharyaamrit.medicare.chat.model;

public class ChatConversation {
    private String conversationId;
    private String otherUserId;
    private String otherUserName;
    private String otherUserType;
    private String lastMessage;
    private long lastMessageTime;
    private int unreadCount;
    private String profileImage;
    private boolean isOnline;

    public ChatConversation() {
        // Required empty constructor for Firebase
    }

    public ChatConversation(String conversationId, String otherUserId, String otherUserName, String otherUserType,
            String lastMessage, long lastMessageTime, int unreadCount, String profileImage, boolean isOnline) {
        this.conversationId = conversationId;
        this.otherUserId = otherUserId;
        this.otherUserName = otherUserName;
        this.otherUserType = otherUserType;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.unreadCount = unreadCount;
        this.profileImage = profileImage;
        this.isOnline = isOnline;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUserName) {
        this.otherUserName = otherUserName;
    }

    public String getOtherUserType() {
        return otherUserType;
    }

    public void setOtherUserType(String otherUserType) {
        this.otherUserType = otherUserType;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}

package com.acharyaamrit.medicare.chat.model;

public class ChatUser {
    private String userId;
    private String name;
    private String userType; // "doctor" or "pharmacy"
    private String clinicId;
    private String profileImage;
    private boolean isOnline;
    private long lastSeen;
    private String fcmToken;

    public ChatUser() {
        // Required empty constructor for Firebase
    }

    public ChatUser(String userId, String name, String userType, String clinicId, String profileImage, boolean isOnline,
            long lastSeen, String fcmToken) {
        this.userId = userId;
        this.name = name;
        this.userType = userType;
        this.clinicId = clinicId;
        this.profileImage = profileImage;
        this.isOnline = isOnline;
        this.lastSeen = lastSeen;
        this.fcmToken = fcmToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getClinicId() {
        return clinicId;
    }

    public void setClinicId(String clinicId) {
        this.clinicId = clinicId;
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

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}

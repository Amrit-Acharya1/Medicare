package com.acharyaamrit.medicare.common.model.request;

public class UserRequest {
    private String email;
    private String password;
    private String fcm_token;
    private String device_token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFcm_token() {
        return fcm_token;
    }

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public UserRequest(String email, String password, String fcm_token, String device_token) {
        this.email = email;
        this.password = password;
        this.fcm_token = fcm_token;
        this.device_token = device_token;
    }
}

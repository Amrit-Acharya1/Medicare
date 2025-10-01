package com.acharyaamrit.medicare.model;

public class UserResponse {
    private String title;
    private String message;
    private String token;
    private Patient user;

    public UserResponse(String title, String message, String token, Patient user) {
        this.title = title;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Patient getUser() {
        return user;
    }

    public void setUser(Patient user) {
        this.user = user;
    }
}
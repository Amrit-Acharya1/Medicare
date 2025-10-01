package com.acharyaamrit.medicare.model;

public class UserResponse {
    private String token;
    private UserRequest userRequest;

    // Getters
    public String getToken() {
        return token;
    }

    public UserRequest getUser() {
        return userRequest;
    }
}
package com.acharyaamrit.medicare.common.model.request;

public class OtpRequest {
    private final String email;

    public OtpRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
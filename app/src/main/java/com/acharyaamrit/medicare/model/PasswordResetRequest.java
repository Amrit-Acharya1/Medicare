package com.acharyaamrit.medicare.model;

public class PasswordResetRequest {
    private String otp;
    private String password;

    public PasswordResetRequest(String otp, String password) {
        this.otp = otp;
        this.password = password;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

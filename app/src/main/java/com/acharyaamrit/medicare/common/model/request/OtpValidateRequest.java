package com.acharyaamrit.medicare.common.model.request;

public class OtpValidateRequest {
    private String email;
    private String otp;

    public OtpValidateRequest(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

}

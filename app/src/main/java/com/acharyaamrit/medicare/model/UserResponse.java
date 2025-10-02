package com.acharyaamrit.medicare.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    private String title;
    private String message;
    private String token;

    @SerializedName("user_type")
    private String user_type;

    @SerializedName("user")
    private JsonObject user;

    public UserResponse() {
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
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

    public Patient getPatient() {
        if (user != null) {
            return new com.google.gson.Gson().fromJson(user, Patient.class);
        }
        return null;
    }

    public Doctor getDoctor() {
        if (user != null) {
            return new com.google.gson.Gson().fromJson(user, Doctor.class);
        }
        return null;
    }
    public Pharmacy getPharmacy() {
        if (user != null) {
            return new com.google.gson.Gson().fromJson(user, Pharmacy.class);
        }
        return null;
    }
    public Clicnic getClicnic() {
        if (user != null) {
            return new com.google.gson.Gson().fromJson(user, Clicnic.class);
        }
        return null;
    }
}
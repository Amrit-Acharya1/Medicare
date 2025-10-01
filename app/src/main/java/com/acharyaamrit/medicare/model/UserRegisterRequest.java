package com.acharyaamrit.medicare.model;

public class UserRegisterRequest {
    private int user_type;
    private String name;
    private String email;
    private String password;

    public UserRegisterRequest(int user_type, String name, String email, String password) {
        this.user_type = user_type;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
    // Constructors, getters, and setters

}

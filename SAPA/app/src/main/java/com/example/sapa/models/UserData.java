package com.example.sapa.models;

public class UserData {
    private String user_id;
    private String full_name;
    private String email;
    private String mobile;
    private String username;
    private String role;
    private String status;
    private String profile_image;


    public String getUserId() {
        return user_id;
    }

    public String getFullName() {
        return full_name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public String getProfileImage() {
        return profile_image;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
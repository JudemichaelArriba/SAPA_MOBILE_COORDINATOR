package com.example.sapa.models;

public class UserProfileResponse {
    private String status;
    private String message;
    private UserData data;


    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }


    public void setStatus(String status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(UserData data) { this.data = data; }
}
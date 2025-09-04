package com.example.sapa.models;

import java.util.List;

public class AppointmentRequest {
    private int slot_id;
    private String user_id;
    private List<String> student_ids;

    public AppointmentRequest(int slot_id, String user_id, List<String> student_ids) {
        this.slot_id = slot_id;
        this.user_id = user_id;
        this.student_ids = student_ids;


    }





    public int getSlot_id() { return slot_id; }
    public String getUser_id() { return user_id; }
    public List<String> getStudent_ids() { return student_ids; }








}

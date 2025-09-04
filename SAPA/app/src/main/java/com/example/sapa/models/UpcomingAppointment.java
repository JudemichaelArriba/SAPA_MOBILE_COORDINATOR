package com.example.sapa.models;

import com.google.gson.annotations.SerializedName;

public class UpcomingAppointment {

    @SerializedName("appointment_id")
    private int appointmentId;

    @SerializedName("slot_id")
    private int slotId;

    @SerializedName("appointment_status")
    private String appointmentStatus;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("slot_name")
    private String slotName;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("hospital_id")
    private String hospitalId;

    @SerializedName("hospital_name")
    private String hospitalName;

    @SerializedName("section_id")
    private int sectionId;

    @SerializedName("section_name")
    private String sectionName;

    @SerializedName("student_ids")
    private String studentIds;

    @SerializedName("student_count")
    private int studentCount;



    public int getAppointmentId() {
        return appointmentId;
    }

    public int getSlotId() {
        return slotId;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getSlotName() {
        return slotName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getStudentIds() {
        return studentIds;
    }

    public int getStudentCount() {
        return studentCount;
    }
}

package com.example.sapa.models;

import com.google.gson.annotations.SerializedName;

public class School {

    @SerializedName("school_id")
    private String schoolId;

    @SerializedName("school_name")
    private String schoolName;

    @SerializedName("school_address")
    private String schoolAddress;

    @SerializedName("school_email")
    private String schoolEmail;

    @SerializedName("contact_no")
    private String contactNo;

    @SerializedName("request_status")
    private String schoolStatus;

    @SerializedName("coordinator_id")
    private String coordinatorId;

    @SerializedName("approved_by")
    private String approvedBy;

    @SerializedName("requested_at")
    private String requestedAt;

    @SerializedName("approved_at")
    private String approvedAt;

    @SerializedName("profile_image")
    private String imageBase64;

    @SerializedName("student_count")
    private int studentCount;

    public School(String schoolId, String schoolName, String schoolAddress,
                  String schoolEmail, String contactNo, String schoolStatus,
                  String coordinatorId, String approvedBy, String requestedAt,
                  String approvedAt, String imageBase64, int studentCount) {
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
        this.schoolEmail = schoolEmail;
        this.contactNo = contactNo;
        this.schoolStatus = schoolStatus;
        this.coordinatorId = coordinatorId;
        this.approvedBy = approvedBy;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.imageBase64 = imageBase64;
        this.studentCount = studentCount;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public String getSchoolEmail() {
        return schoolEmail;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getSchoolStatus() {
        return schoolStatus;
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public int getStudentCount() {
        return studentCount;
    }
}

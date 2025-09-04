package com.example.sapa.models;

import com.google.gson.annotations.SerializedName;

public class Students {

    @SerializedName("student_id")
    private String id;

    @SerializedName("student_fullname")
    private String studentFullname;

    @SerializedName("email")
    private String email;

    @SerializedName("contact_no")
    private String contactNo;

    @SerializedName("birthdate")
    private String birthdate;

    @SerializedName("gender")
    private String gender;

    @SerializedName("coordinator_id")
    private String coordinatorId;

    @SerializedName("school_id")
    private String schoolId;

    @SerializedName("school_name")
    private String schoolName;

    public String getId() {
        return id;
    }

    public void student_id(String id) {
        this.id = id;
    }

    public String getStudentFullname() {
        return studentFullname;
    }

    public void setStudentFullname(String studentFullname) {
        this.studentFullname = studentFullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCoordinatorId() {
        return coordinatorId;
    }

    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}

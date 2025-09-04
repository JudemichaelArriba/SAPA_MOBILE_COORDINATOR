package com.example.sapa.models;

import com.google.gson.annotations.SerializedName;

public class Bill {

    @SerializedName("bill_code")
    private String billCode;

    @SerializedName("amount")
    private double amount;

    @SerializedName("issued_at")
    private String issuedAt;

    @SerializedName("status")
    private String status;

    @SerializedName("slot_id")
    private int slotId;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("section_name")
    private String sectionName;

    @SerializedName("hospital_name")
    private String hospitalName;


    public Bill() {}


    public Bill(String billCode, double amount, String issuedAt, String status,
                int slotId, String startTime, String endTime,
                String sectionName, String hospitalName) {
        this.billCode = billCode;
        this.amount = amount;
        this.issuedAt = issuedAt;
        this.status = status;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sectionName = sectionName;
        this.hospitalName = hospitalName;
    }


    public String getBillCode() { return billCode; }
    public double getAmount() { return amount; }
    public String getIssuedAt() { return issuedAt; }
    public String getStatus() { return status; }
    public int getSlotId() { return slotId; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getSectionName() { return sectionName; }
    public String getHospitalName() { return hospitalName; }
}

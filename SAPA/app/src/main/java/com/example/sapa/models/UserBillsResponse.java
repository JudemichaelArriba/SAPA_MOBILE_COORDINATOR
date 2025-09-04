package com.example.sapa.models;

import java.util.List;

public class UserBillsResponse {
    private double total_bills;
    private List<Bill> bills;

    public double getTotal_bills() {
        return total_bills;
    }

    public void setTotal_bills(double total_bills) {
        this.total_bills = total_bills;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }
}

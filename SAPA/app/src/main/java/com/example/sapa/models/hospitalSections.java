package com.example.sapa.models;

public class hospitalSections {
    private int section_id;
    private String section_name;
    private String section_description;
    private String hospital_id;
    private String hospital_name;
    private double billing;

    public int getSection_id() {
        return section_id;
    }

    public String getSection_name() {
        return section_name;
    }

    public String getSection_description() {
        return section_description;
    }

    public String getHospital_id() {
        return hospital_id;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public double getBilling() {
        return billing;
    }
}

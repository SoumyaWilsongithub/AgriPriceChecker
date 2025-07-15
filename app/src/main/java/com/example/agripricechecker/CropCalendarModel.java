package com.example.agripricechecker.models;

public class CropCalendarModel {
    private String sowing;
    private String harvesting;
    private String advice;

    public CropCalendarModel() {} // Required

    public String getSowing() {
        return sowing;
    }

    public String getHarvesting() {
        return harvesting;
    }

    public String getAdvice() {
        return advice;
    }
}

package com.example.agripricechecker;
public class Forecast {
    private String day;
    private String temp;
    private String iconUrl;

    public Forecast(String day, String temp, String iconUrl) {
        this.day = day;
        this.temp = temp;
        this.iconUrl = iconUrl;
    }

    public String getDay() { return day; }
    public String getTemp() { return temp; }
    public String getIconUrl() { return iconUrl; }
}

package com.example.agripricechecker;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("location")
    public Location location;

    @SerializedName("current")
    public Current current;

    public class Location {
        @SerializedName("name")
        public String name;
        public String region;
        public String country;
    }

    public class Current {
        @SerializedName("temp_c")
        public float tempC;

        @SerializedName("humidity")
        public int humidity;

        @SerializedName("wind_kph")
        public float windKph;

        @SerializedName("condition")
        public Condition condition;
    }

    public class Condition {
        @SerializedName("text")
        public String text;

        @SerializedName("icon")
        public String icon;
    }
}
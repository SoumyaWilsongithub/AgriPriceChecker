package com.example.agripricechecker;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This model handles the WeatherAPI Forecast response.
 * We include all necessary sub-classes here to prevent "Symbol not found" errors.
 */
public class ForecastResponse {

    @SerializedName("location")
    public Location location;

    @SerializedName("forecast")
    public Forecast forecast;

    public static class Location {
        @SerializedName("name")
        public String name;
        @SerializedName("region")
        public String region;
    }

    public static class Forecast {
        @SerializedName("forecastday")
        public List<ForecastDay> forecastday;
    }

    public static class ForecastDay {
        @SerializedName("date")
        public String date;
        @SerializedName("day")
        public Day day;
    }

    public static class Day {
        @SerializedName("avgtemp_c")
        public double avgtemp_c;

        @SerializedName("condition")
        public Condition condition;

        @SerializedName("avghumidity")
        public double avghumidity;

        @SerializedName("maxwind_kph")
        public double maxwind_kph;
    }

    public static class Condition {
        @SerializedName("text")
        public String text;
        @SerializedName("icon")
        public String icon;
    }
}
package com.example.agripricechecker;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ForecastResponse {
    @SerializedName("forecast")
    public Forecast forecast;

    public class Forecast {
        @SerializedName("forecastday")
        public List<ForecastDay> forecastday;
    }

    public class ForecastDay {
        @SerializedName("date")
        public String date;

        @SerializedName("day")
        public Day day;
    }

    public class Day {
        @SerializedName("avgtemp_c")
        public float avgTempC;

        @SerializedName("condition")
        public WeatherResponse.Condition condition; // Reusing Condition from WeatherResponse
    }
}
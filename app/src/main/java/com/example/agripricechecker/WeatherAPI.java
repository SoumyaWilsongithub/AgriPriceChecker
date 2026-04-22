package com.example.agripricechecker;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    @GET("v1/forecast.json")
    Call<WeatherResponse> getForecast(
            @Query("key") String apiKey,
            @Query("q") String query,
            @Query("days") int days,
            @Query("aqi") String aqi,
            @Query("alerts") String alerts,
            @Query("lang") String lang
    );

    class WeatherResponse {
        public Location location;
        public Current current;
        public Forecast forecast;

        public static class Location {
            public String name;
        }

        public static class Current {
            public double temp_c;
            public int humidity;
            public double wind_kph;
            public Condition condition;
        }

        public static class Condition {
            public String text; // This returns translated text from API
            public String icon; // This provides the weather image URL
        }

        public static class Forecast {
            public List<ForecastDay> forecastday;
        }

        public static class ForecastDay {
            public String date;
            public Day day;
        }

        public static class Day {
            public double avgtemp_c;
            public Condition condition; // Needed for forecast icons/text
        }
    }
}
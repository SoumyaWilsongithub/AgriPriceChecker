package com.example.agripricechecker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherAPI {

    // WeatherAPI.com uses "key" instead of "appid"
    @GET("v1/current.json")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("key") String apiKey
    );

    // WeatherAPI.com forecast endpoint
    @GET("v1/forecast.json")
    Call<ForecastResponse> getForecast(
            @Query("q") String city,
            @Query("key") String apiKey,
            @Query("days") int days
    );
}
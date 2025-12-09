package com.example.agripricechecker; // Make sure this matches your package name

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    TextView tvCity, tvTemp, tvHumidity, tvWind, tvDescription, tvSuggestion;
    LinearLayout forecastLayout;

    // API Details
    private final String BASE_URL = "https://api.weatherapi.com/";
    private final String API_KEY = "34d6bbcc55414b30a8f134632250407";

    // Location Variables
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final String DEFAULT_CITY_QUERY = "New Delhi, India";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // UI Initialization
        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvDescription = findViewById(R.id.tvDescription);
        tvSuggestion = findViewById(R.id.tvSuggestion);
        forecastLayout = findViewById(R.id.forecastLayout);

        // Location Setup
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkLocationPermission();
    }

    // --- LOCATION METHODS ---

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied. Showing weather for " + DEFAULT_CITY_QUERY, Toast.LENGTH_LONG).show();
                fetchWeather(DEFAULT_CITY_QUERY);
            }
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // Should not happen if permission flow is followed
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String coordinates = location.getLatitude() + "," + location.getLongitude();
                    Log.d("WeatherApp", "Fetching weather for coords: " + coordinates);
                    fetchWeather(coordinates);
                } else {
                    Toast.makeText(WeatherActivity.this, "Could not get current location. Showing weather for " + DEFAULT_CITY_QUERY, Toast.LENGTH_LONG).show();
                    fetchWeather(DEFAULT_CITY_QUERY);
                }
            }
        });
    }

    // --- API FETCH METHODS ---

    private void fetchWeather(String locationQuery) {
        fetchCurrentWeather(locationQuery);
        fetchForecast(locationQuery);
    }

    private void fetchCurrentWeather(String locationQuery) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPI api = retrofit.create(WeatherAPI.class);
        Call<WeatherResponse> call = api.getCurrentWeather(locationQuery, API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse data = response.body();

                    // 🛑 INDIA FILTER AND NULL CHECK 🛑
                    boolean isIndia = data.location != null && "India".equalsIgnoreCase(data.location.country);

                    if (isIndia) {
                        // Location is in India, proceed to display data
                        if (data.location != null) {
                            tvCity.setText(data.location.name + ", " + data.location.region);
                        }

                        if (data.current != null) {
                            tvTemp.setText(data.current.tempC + "°C");
                            tvHumidity.setText("Humidity: " + data.current.humidity + "%");
                            tvWind.setText("Wind: " + data.current.windKph + " km/h");

                            if (data.current.condition != null) {
                                String conditionText = data.current.condition.text;
                                tvDescription.setText(conditionText);
                                tvSuggestion.setText(getSuggestion(conditionText));

                            }
                        }
                    } else {
                        // Location is NOT in India
                        tvCity.setText("Location not in India. Using fallback.");
                        Toast.makeText(WeatherActivity.this, "Location detected outside India. Using fallback: " + DEFAULT_CITY_QUERY, Toast.LENGTH_LONG).show();
                        fetchWeather(DEFAULT_CITY_QUERY);
                    }
                } else {
                    Toast.makeText(WeatherActivity.this, "Weather data failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                if (!isFinishing() && !isDestroyed()) {
                    Toast.makeText(WeatherActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchForecast(String locationQuery) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPI api = retrofit.create(WeatherAPI.class);
        Call<ForecastResponse> call = api.getForecast(locationQuery, API_KEY, 5); // Requesting 5 days

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForecastResponse> call, @NonNull Response<ForecastResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse body = response.body();
                    if (body.forecast != null && body.forecast.forecastday != null) {
                        displayForecast(body.forecast.forecastday);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForecastResponse> call, @NonNull Throwable t) {
                Log.e("WeatherApp", "Forecast network error: " + t.getMessage());
            }
        });
    }

    // --- UTILITY METHODS ---

    private void displayForecast(List<ForecastResponse.ForecastDay> list) {
        forecastLayout.removeAllViews();

        if (list == null || list.size() < 2) return; // Need at least today and tomorrow

        // 🛑 FIX: Start the loop from the second element (index 1) to skip today's forecast.
        for (int i = 1; i < list.size(); i++) {
            ForecastResponse.ForecastDay item = list.get(i);

            if (item == null || item.day == null || item.day.condition == null) continue;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(8, 16, 8, 16);
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            row.setLayoutParams(params);

            // Date TextView
            TextView tvDate = new TextView(this);
            tvDate.setText(item.date);
            tvDate.setTextSize(14);
            tvDate.setPadding(0,0,20,0);
            LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f);
            tvDate.setLayoutParams(dateParams);

            // Icon ImageView
            ImageView ivIcon = new ImageView(this);
            String iconUrl = "https:" + item.day.condition.icon;
            try {
                Glide.with(this).load(iconUrl).into(ivIcon);
            } catch (Exception e) {
                // Image loading failed gracefully
            }
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                    0, 100, 1f);
            ivIcon.setLayoutParams(iconParams);


            // Temp TextView
            TextView tvTemp = new TextView(this);
            tvTemp.setText(item.day.avgTempC + "°C");
            tvTemp.setTextSize(16);
            tvTemp.setGravity(Gravity.END);
            LinearLayout.LayoutParams tempParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f);
            tvTemp.setLayoutParams(tempParams);

            // Description TextView
            TextView tvDesc = new TextView(this);
            tvDesc.setText(item.day.condition.text);
            tvDesc.setTextSize(12);
            tvDesc.setPadding(20, 0, 0, 0);
            LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2.5f);
            tvDesc.setLayoutParams(descParams);


            row.addView(tvDate);
            row.addView(ivIcon);
            row.addView(tvTemp);
            row.addView(tvDesc);

            forecastLayout.addView(row);
        }
    }

    private String getSuggestion(String desc) {
        if (desc == null) return "Suggestion: Check local news for agricultural advisories.";
        desc = desc.toLowerCase();

        if (desc.contains("rain") || desc.contains("drizzle")) return "Suggestion: Avoid field work and protect crops from water damage.";
        if (desc.contains("storm") || desc.contains("thunder")) return "Suggestion: Secure outdoor materials immediately.";
        if (desc.contains("clear") || desc.contains("sunny")) return "Suggestion: Excellent weather for irrigation or harvesting.";
        if (desc.contains("cloudy") || desc.contains("overcast")) return "Suggestion: Suitable for light fieldwork.";
        if (desc.contains("mist") || desc.contains("fog")) return "Suggestion: Visibility is low, postpone road transport.";

        return "Suggestion: Check local news for agricultural advisories.";
    }
}
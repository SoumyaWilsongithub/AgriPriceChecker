package com.example.agripricechecker;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private TextView tvCity, tvTemp, tvHumidity, tvWind, tvDescription, tvSuggestion;
    private ImageView ivWeatherIcon;
    private LinearLayout forecastLayout;
    private FusedLocationProviderClient fusedLocationClient;

    private final String BASE_URL = "https://api.weatherapi.com/";
    private final String API_KEY = "34d6bbcc55414b30a8f134632250407";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        // ✅ Toolbar setup (Title + Back Arrow)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_weather));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvWind = findViewById(R.id.tvWind);
        tvDescription = findViewById(R.id.tvDescription);
        tvSuggestion = findViewById(R.id.tvSuggestion);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
        forecastLayout = findViewById(R.id.forecastLayout);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        checkPermissionsAndFetch();
    }

    private void checkPermissionsAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            String query = (location != null) ? (location.getLatitude() + "," + location.getLongitude()) : "New Delhi";
            callWeatherApi(query);
        });
    }

    private void callWeatherApi(String query) {
        SharedPreferences prefs = getSharedPreferences("AgriPrice_Prefs", MODE_PRIVATE);
        String savedLang = prefs.getString("Locale.Helper.Selected.Language", "en").toLowerCase();
        String currentResLang = getResources().getConfiguration().locale.getLanguage();

        final boolean isHindi = savedLang.contains("hi") || currentResLang.contains("hi");
        final String apiLang = isHindi ? "hi" : "en";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherAPI api = retrofit.create(WeatherAPI.class);

        api.getForecast(API_KEY, query, 3, "no", "no", apiLang).enqueue(new Callback<WeatherAPI.WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherAPI.WeatherResponse> call, @NonNull Response<WeatherAPI.WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body(), isHindi);
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherAPI.WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(WeatherActivity.this, isHindi ? "नेटवर्क त्रुटि" : "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper method to convert common city names to Hindi
    private String getHindiCityName(String englishName) {
        if (englishName == null) return "";
        switch (englishName.toLowerCase().trim()) {
            case "bareilly": return "बरेली";
            case "new delhi": return "नई दिल्ली";
            case "lucknow": return "लखनऊ";
            case "mumbai": return "मुंबई";
            case "moradabad": return "मुरादाबाद";
            case "agra": return "आगरा";
            case "kanpur": return "कानपुर";
            default: return englishName; // Fallback to original if not in list
        }
    }

    private void updateUI(WeatherAPI.WeatherResponse data, boolean isHindi) {
        // Apply city translation if in Hindi mode
        String cityName = isHindi ? getHindiCityName(data.location.name) : data.location.name;
        tvCity.setText(cityName);

        tvTemp.setText((int) data.current.temp_c + "°C");

        if (isHindi) {
            tvHumidity.setText("नमी: " + data.current.humidity + "%");
            tvWind.setText("हवा: " + data.current.wind_kph + " किमी/घंटा");
            tvDescription.setText(data.current.condition.text);
        } else {
            tvHumidity.setText("Humidity: " + data.current.humidity + "%");
            tvWind.setText("Wind: " + data.current.wind_kph + " km/h");
            tvDescription.setText(data.current.condition.text);
        }

        tvSuggestion.setText(generateSuggestion(data.current.condition.text, isHindi));
        Glide.with(this).load("https:" + data.current.condition.icon).into(ivWeatherIcon);

        forecastLayout.removeAllViews();
        List<WeatherAPI.WeatherResponse.ForecastDay> days = data.forecast.forecastday;

        TextView header = new TextView(this);
        header.setText(isHindi ? "2-दिन का पूर्वानुमान" : "2-Day Forecast");
        header.setTextSize(18);
        header.setTypeface(null, Typeface.BOLD);
        header.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        header.setPadding(0, 30, 0, 15);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        forecastLayout.addView(header);

        for (int i = 1; i < days.size(); i++) {
            TextView tv = new TextView(this);
            tv.setPadding(0, 15, 0, 15);
            tv.setTextSize(16);
            tv.setTextColor(ContextCompat.getColor(this, android.R.color.black));

            String dateLabel = isHindi ? "तारीख: " : "Date: ";
            tv.setText(dateLabel + days.get(i).date + " | " + (int) days.get(i).day.avgtemp_c + "°C | " + days.get(i).day.condition.text);
            forecastLayout.addView(tv);
        }
    }

    private String generateSuggestion(String desc, boolean isHindi) {
        if (desc == null) return "";
        String low = desc.toLowerCase();

        if (isHindi) {
            if (low.contains("rain") || low.contains("mist") || low.contains("बारिश") || low.contains("धुंध"))
                return "सुझाव: नमी या बारिश की संभावना। सिंचाई रोक दें।";
            if (low.contains("sun") || low.contains("clear") || low.contains("साफ") || low.contains("धूप"))
                return "सुझाव: मौसम साफ है। खाद डालने के लिए अच्छा समय।";
            return "सुझाव: कृषि कार्य जारी रखें।";
        } else {
            if (low.contains("rain") || low.contains("mist"))
                return "Suggestion: Possible rain/mist. Stop irrigation.";
            return "Suggestion: Clear sky. Good for farm activities.";
        }
    }
}
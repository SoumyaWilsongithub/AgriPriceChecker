package com.example.agripricechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {
    // SYNCED CONSTANTS
    private static final String PREF_NAME = "AgriPrice_Prefs";
    private static final String KEY_LANG = "Locale.Helper.Selected.Language";

    private LineChart lineChart;
    private TextView chartHeading;
    private static final String API_KEY = "579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b";

    private boolean isHindi = false;
    private String langCode = "en";
    private final Map<String, String> cropTranslationMap = new HashMap<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        // Ensures the activity starts with the correct localized context
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        // 1. CRITICAL: Check correct SharedPreferences
        checkLanguage();

        // 2. Initialize translations
        initCropTranslations();

        lineChart = findViewById(R.id.lineChart);
        chartHeading = findViewById(R.id.chartHeading);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isHindi ? "कीमत का रुझान" : "Price Trend");
        }

        String crop = getIntent().getStringExtra("crop");
        if (crop != null && !crop.isEmpty()) {
            String displayCrop = isHindi && cropTranslationMap.containsKey(crop)
                    ? cropTranslationMap.get(crop) : crop;

            String headingTemplate = isHindi ? "%s मूल्य चार्ट" : "%s Price Chart";
            chartHeading.setText(String.format(headingTemplate, displayCrop));

            fetchChartData(crop);
        }
    }

    private void checkLanguage() {
        // UPDATED: Now matches MainActivity and ChartCropSelectionActivity
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        langCode = prefs.getString(KEY_LANG, "en");
        isHindi = "hi".equals(langCode);
    }

    private void initCropTranslations() {
        cropTranslationMap.put("Wheat", "गेहूँ");
        cropTranslationMap.put("Rice", "धान");
        cropTranslationMap.put("Maize", "मक्का");
        cropTranslationMap.put("Barley", "जौ");
        cropTranslationMap.put("Potato", "आलू");
        cropTranslationMap.put("Onion", "प्याज");
        cropTranslationMap.put("Tomato", "टमाटर");
        cropTranslationMap.put("Moong", "मूंग");
        cropTranslationMap.put("Chana", "चना");
        cropTranslationMap.put("Masoor", "मसूर");
        cropTranslationMap.put("Arhar", "अरहर");
        cropTranslationMap.put("Urad", "उड़द");
        cropTranslationMap.put("Apple", "सेब");
        cropTranslationMap.put("Banana", "केला");
        cropTranslationMap.put("Grapes", "अंगूर");
        cropTranslationMap.put("Mango", "आम");
        cropTranslationMap.put("Orange", "संतरा");
        cropTranslationMap.put("Cabbage", "पत्ता गोभी");
        cropTranslationMap.put("Brinjal", "बैंगन");
        cropTranslationMap.put("Jowar", "ज्वार");
        cropTranslationMap.put("Bajra", "बाजरा");
    }

    private void fetchChartData(String crop) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<MandiResponse> call = apiService.getMandiPrices(
                API_KEY,
                "json",
                "Uttar Pradesh",
                crop,
                10
        );

        call.enqueue(new Callback<MandiResponse>() {
            @Override
            public void onResponse(Call<MandiResponse> call, Response<MandiResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                    try {
                        float price = Float.parseFloat(response.body().records.get(0).modal_price);
                        generateSmoothChart(crop, price);
                    } catch (Exception e) {
                        generateSmoothChart(crop, 2400f);
                    }
                } else {
                    generateSmoothChart(crop, 2350f);
                }
            }

            @Override
            public void onFailure(Call<MandiResponse> call, Throwable t) {
                generateSmoothChart(crop, 2200f);
            }
        });
    }

    private void generateSmoothChart(String crop, float startPrice) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        float currentPrice = startPrice;
        Random random = new Random();
        Calendar calendar = Calendar.getInstance();

        // FIX: Use langCode variable derived from the sync'd SharedPreferences
        Locale currentLocale = new Locale(langCode);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", currentLocale);

        for (int i = 0; i < 7; i++) {
            labels.add(sdf.format(calendar.getTime()));
            entries.add(new Entry(i, currentPrice));

            float changePercent = (random.nextFloat() * 0.04f) - 0.02f;
            currentPrice += (currentPrice * changePercent);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        String translatedCrop = isHindi && cropTranslationMap.containsKey(crop)
                ? cropTranslationMap.get(crop) : crop;

        String legendLabel = isHindi ? translatedCrop + " भाव (अनुमानित)" : crop + " Price (Simulated)";

        LineDataSet dataSet = new LineDataSet(entries, legendLabel);

        int themeGreen = Color.parseColor("#008444");
        dataSet.setColor(themeGreen);
        dataSet.setLineWidth(3f);
        dataSet.setCircleColor(themeGreen);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(themeGreen);
        dataSet.setFillAlpha(50);

        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.DKGRAY);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(11f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setTextSize(11f);

        lineChart.getAxisRight().setEnabled(false);

        Description description = new Description();
        description.setText(isHindi ? "अगले 7 दिनों का मूल्य विश्लेषण" : "7-Day Price Forecast");
        description.setTextColor(Color.GRAY);
        description.setTextSize(10f);
        lineChart.setDescription(description);

        lineChart.setExtraOffsets(10, 10, 10, 10);
        lineChart.animateX(1200);
        lineChart.getLegend().setTextSize(12f);
        lineChart.invalidate();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
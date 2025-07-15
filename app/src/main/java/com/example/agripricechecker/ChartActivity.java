package com.example.agripricechecker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private static final String API_KEY = "579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        lineChart = findViewById(R.id.lineChart);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Price Trend");
        }

        String crop = getIntent().getStringExtra("crop");
        if (crop != null && !crop.isEmpty()) {
            fetchChartData(crop);
        } else {
            Toast.makeText(this, "No crop name provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchChartData(String crop) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMandiPrices(API_KEY, "json", 20, 0, crop)
                .enqueue(new Callback<MandiResponse>() {
                    @Override
                    public void onResponse(Call<MandiResponse> call, Response<MandiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                            simulateUpcomingPrices(crop, response.body().records);
                        } else {
                            Toast.makeText(ChartActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<MandiResponse> call, Throwable t) {
                        Toast.makeText(ChartActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                        Log.e("CHART", "Error: ", t);
                    }
                });
    }

    private void simulateUpcomingPrices(String crop, List<MandiRecord> records) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        float lastPrice = -1f;

        // Get last known price
        for (int i = records.size() - 1; i >= 0; i--) {
            try {
                lastPrice = Float.parseFloat(records.get(i).modal_price.replace(",", ""));
                break;
            } catch (Exception ignored) {}
        }

        if (lastPrice == -1f) {
            Toast.makeText(this, "No valid price data", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(calendar.getTime());
            labels.add(date);

            // Simulate by ±5% random variation
            float fluctuation = (float) ((Math.random() * 0.1 - 0.05) * lastPrice);
            float simulatedPrice = lastPrice + fluctuation;
            entries.add(new Entry(i, simulatedPrice));

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        LineDataSet dataSet = new LineDataSet(entries, crop + " Price (Simulated)");
        dataSet.setColor(ContextCompat.getColor(this, R.color.purple_700));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(true);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-35);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setYOffset(10f);

        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f);

        Description description = new Description();
        description.setText("Next 7 Days Simulated Price Trend");
        description.setTextSize(12f);
        lineChart.setDescription(description);

        lineChart.setExtraBottomOffset(15f);
        lineChart.animateX(1000);
        lineChart.invalidate();
    }
}

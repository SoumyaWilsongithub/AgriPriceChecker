package com.example.agripricechecker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random; // Import Random

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView chartHeading;

    private static final String API_KEY = "579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b"; // Replace with your actual API key

    // --- Simulation Parameters (Tune these values) ---
    // General Volatility (base percentage for fluctuation)
    private static final float BASE_VOLATILITY_PERCENTAGE = 0.03f; // 3%
    // Maximum additional random volatility (adds unpredictability)
    private static final float MAX_RANDOM_VOLATILITY_ADDITION = 0.07f; // up to an additional 7%
    // Chance of a significant price event (e.g., larger jump or drop)
    private static final float SIGNIFICANT_EVENT_CHANCE = 0.15f; // 15% chance per day
    // Multiplier for significant events (how much larger the change is)
    private static final float SIGNIFICANT_EVENT_MULTIPLIER = 2.0f; // 2 times the normal fluctuation
    // Trend persistence (how likely the trend is to continue in the same direction)
    private static final float TREND_PERSISTENCE_FACTOR = 0.3f; // 30% chance to follow previous trend
    // --- End of Simulation Parameters ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart); // Make sure this matches your XML file name

        lineChart = findViewById(R.id.lineChart);
        chartHeading = findViewById(R.id.chartHeading);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Price Trend");
        }

        lineChart.setNoDataText("Loading price data...");
        lineChart.invalidate();

        String crop = getIntent().getStringExtra("crop");

        if (crop != null && !crop.isEmpty()) {
            chartHeading.setText(crop + " Price Chart");
            fetchChartData(crop);
        } else {
            Toast.makeText(this, "No crop name provided", Toast.LENGTH_SHORT).show();
            chartHeading.setText("Crop Price Chart");
            lineChart.setNoDataText("No crop name provided.");
            lineChart.invalidate();
            // finish(); // Consider if you want to close if no crop
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchChartData(String crop) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getMandiPrices(API_KEY, "json", 20, 0, crop) // Using your parameters
                .enqueue(new Callback<MandiResponse>() {
                    @Override
                    public void onResponse(Call<MandiResponse> call, Response<MandiResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().records != null && !response.body().records.isEmpty()) {
                            simulateUpcomingPrices(crop, response.body().records);
                        } else {
                            String message = "Failed to load data";
                            if (response.body() != null && (response.body().records == null || response.body().records.isEmpty())) {
                                message = "No records found for " + crop;
                            } else if (!response.isSuccessful()) {
                                message = "Error: " + response.code() + " " + response.message();
                            }
                            Toast.makeText(ChartActivity.this, message, Toast.LENGTH_LONG).show();
                            lineChart.setNoDataText(message);
                            lineChart.invalidate();
                        }
                    }

                    @Override
                    public void onFailure(Call<MandiResponse> call, Throwable t) {
                        Toast.makeText(ChartActivity.this, "Network error. Please check your connection.", Toast.LENGTH_LONG).show();
                        Log.e("CHART_ACTIVITY", "API Call Failure: ", t);
                        lineChart.setNoDataText("Network error. Please try again.");
                        lineChart.invalidate();
                    }
                });
    }

    private void simulateUpcomingPrices(String crop, List<MandiRecord> records) {
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        float actualLastPrice = -1f;

        // Get last known actual price from records
        for (int i = records.size() - 1; i >= 0; i--) {
            MandiRecord record = records.get(i);
            if (record != null && record.modal_price != null && !record.modal_price.trim().isEmpty()) {
                try {
                    String priceString = record.modal_price.replace(",", "");
                    actualLastPrice = Float.parseFloat(priceString);
                    if (actualLastPrice > 0) {
                        break;
                    } else {
                        actualLastPrice = -1f; // Reset if parsed price is not positive
                    }
                } catch (NumberFormatException e) {
                    Log.w("CHART_ACTIVITY", "Could not parse price: " + record.modal_price);
                    actualLastPrice = -1f;
                }
            }
        }

        if (actualLastPrice <= 0) { // Check if a valid last price was found
            Toast.makeText(this, "No valid historical price data available for " + crop + " to simulate.", Toast.LENGTH_LONG).show();
            lineChart.setNoDataText("No valid historical price data for " + crop + ".");
            lineChart.invalidate();
            return;
        }

        float currentSimulatedPrice = actualLastPrice; // Start simulation from the actual last price

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();
        float previousChangeDirectionSign = 0; // -1 for down, 1 for up, 0 for initial

        // If you wanted crop-specific params, you'd retrieve them here:
        // CropSimulationParams currentCropParams = getCropParams(crop); // Example

        for (int i = 0; i < 7; i++) { // Simulate for next 7 days
            String date = sdf.format(calendar.getTime());
            labels.add(date);

            // 1. Determine base fluctuation amount for the day
            //    (Using global params here. If crop-specific, use currentCropParams.baseVolatility etc.)
            float dailyRandomAddition = random.nextFloat() * MAX_RANDOM_VOLATILITY_ADDITION;
            float dailyVolatility = BASE_VOLATILITY_PERCENTAGE + dailyRandomAddition;
            float baseFluctuation = currentSimulatedPrice * dailyVolatility;

            // 2. Determine direction of change
            float changeDirection; // Will be -1 or 1
            if (random.nextFloat() < TREND_PERSISTENCE_FACTOR && previousChangeDirectionSign != 0) {
                changeDirection = previousChangeDirectionSign; // Continue trend
            } else {
                changeDirection = (random.nextBoolean()) ? 1.0f : -1.0f; // Randomly up or down
            }

            // 3. Check for a significant event
            float eventMultiplier = 1.0f;
            if (random.nextFloat() < SIGNIFICANT_EVENT_CHANCE) {
                eventMultiplier = SIGNIFICANT_EVENT_MULTIPLIER;
            }

            // 4. Calculate the price change
            float priceChange = baseFluctuation * changeDirection * eventMultiplier;

            // 5. Apply the change to get the new simulated price
            currentSimulatedPrice += priceChange;
            currentSimulatedPrice = Math.max(1, currentSimulatedPrice); // Ensure price doesn't go below a minimum (e.g., 1 or 0.01)

            entries.add(new Entry(i, currentSimulatedPrice));
            previousChangeDirectionSign = Math.signum(priceChange); // Store direction sign for next iteration

            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        LineDataSet dataSet = new LineDataSet(entries, crop + " Price (Simulated)");
        // --- DataSet Styling (Use your app's theme colors) ---
        if (ContextCompat.getColor(this, R.color.iffco_green) != 0) { // Check if color exists
            dataSet.setColor(ContextCompat.getColor(this, R.color.iffco_green));
            dataSet.setCircleColor(ContextCompat.getColor(this, R.color.iffco_green));
        } else {
            dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_green_dark)); // Fallback
            dataSet.setCircleColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
        }
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false); // Hide values on points
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); // Smoother line

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // --- XAxis Setup ---
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setTextSize(10f);
        xAxis.setLabelRotationAngle(-45);
        xAxis.setAvoidFirstLastClipping(true);

        // --- YAxis Setup ---
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f); // Prices shouldn't be negative
        // lineChart.getAxisLeft().setGranularity(10f); // Optional: Set Y-axis step based on price range

        // --- Chart Description ---
        Description description = new Description();
        description.setText("Next 7 Days Simulated Price Trend");
        description.setTextSize(12f);
        lineChart.setDescription(description);

        // --- Other Chart Settings ---
        lineChart.setExtraBottomOffset(20f); // Add padding for rotated X-axis labels
        lineChart.animateX(1000);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);

        lineChart.invalidate(); // Refresh the chart
    }

    // Example placeholder for getting crop-specific parameters (if you implement it later)
    /*
    private CropSimulationParams getCropParams(String cropName) {
        // In a real scenario, you'd have a Map or similar structure
        if ("Wheat".equalsIgnoreCase(cropName)) {
            return new CropSimulationParams(0.02f, 0.05f, 0.10f, 1.5f, 0.2f);
        } else if ("Tomato".equalsIgnoreCase(cropName)) {
            return new CropSimulationParams(0.05f, 0.10f, 0.25f, 2.5f, 0.4f);
        }
        // Default parameters if crop not found
        return new CropSimulationParams(
            BASE_VOLATILITY_PERCENTAGE,
            MAX_RANDOM_VOLATILITY_ADDITION,
            SIGNIFICANT_EVENT_CHANCE,
            SIGNIFICANT_EVENT_MULTIPLIER,
            TREND_PERSISTENCE_FACTOR
        );
    }

    // Helper class for crop-specific params (if you implement it later)
    private static class CropSimulationParams {
        float baseVolatility;
        float maxRandomVolatility;
        float eventChance;
        float eventMultiplier;
        float trendPersistence;

        public CropSimulationParams(float baseVolatility, float maxRandomVolatility, float eventChance, float eventMultiplier, float trendPersistence) {
            this.baseVolatility = baseVolatility;
            this.maxRandomVolatility = maxRandomVolatility;
            this.eventChance = eventChance;
            this.eventMultiplier = eventMultiplier;
            this.trendPersistence = trendPersistence;
        }
    }
    */
}

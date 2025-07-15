package com.example.agripricechecker;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchPriceActivity extends AppCompatActivity {

    Spinner typeSpinner;
    AutoCompleteTextView cropName;
    ImageButton clearCropBtn;
    Button fetchBtn;
    RecyclerView recyclerView;

    List<PriceModel> cropList = new ArrayList<>();
    PriceAdapter adapter;
    Map<String, List<String>> cropMapByType = new HashMap<>();

    private static final String API_KEY = "579b464db66ec23bdd000001cdd3946e44ce4aad7209ff7b23ac571b";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_price);

        // Show back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fetch Price");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Bind views
        typeSpinner = findViewById(R.id.typeSpinner);
        cropName = findViewById(R.id.cropName);
        clearCropBtn = findViewById(R.id.clearCropBtn);
        fetchBtn = findViewById(R.id.fetchBtn);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PriceAdapter(cropList);
        recyclerView.setAdapter(adapter);

        setupCropTypeSpinner();

        // Fetch price button
        fetchBtn.setOnClickListener(v -> {
            String crop = cropName.getText().toString().trim();
            if (!crop.isEmpty()) {
                fetchPrices(crop);
            } else {
                Toast.makeText(this, "Please enter a crop name", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear/cut icon button
        clearCropBtn.setOnClickListener(v -> {
            cropName.setText("");
            cropList.clear();
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupCropTypeSpinner() {
        String[] types = {"All", "Grain", "Pulse", "Vegetable" , "Fruit"};

        // Define crops by type
        cropMapByType.put("Grain", Arrays.asList("Wheat", "Rice", "Maize", "Barley", "Jowar", "Bajra"));
        cropMapByType.put("Pulse", Arrays.asList("Moong", "Chana", "Masoor", "Arhar", "Urad"));
        cropMapByType.put("Vegetable", Arrays.asList("Potato", "Onion", "Tomato", "Cabbage", "Brinjal"));
        cropMapByType.put("Fruit", Arrays.asList("Apple", "Banana", "Grapes", "Mango", "Orange"));

        List<String> all = new ArrayList<>();
        for (List<String> list : cropMapByType.values()) {
            all.addAll(list);
        }
        cropMapByType.put("All", all);

        // Custom spinner style
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                types
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(spinnerAdapter);

        // Spinner selection listener
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = types[position];
                List<String> filtered = cropMapByType.get(selectedType);
                if (filtered != null) {
                    ArrayAdapter<String> cropAdapter = new ArrayAdapter<>(
                            FetchPriceActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            filtered
                    );
                    cropName.setAdapter(cropAdapter);
                    cropName.setText(""); // Clear input
                    cropList.clear();     // Clear list
                    adapter.notifyDataSetChanged();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void fetchPrices(String crop) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getMandiPrices(API_KEY, "json", 20, 0, crop).enqueue(new Callback<MandiResponse>() {
            public void onResponse(Call<MandiResponse> call, Response<MandiResponse> res) {
                if (res.isSuccessful() && res.body() != null) {
                    cropList.clear();
                    for (MandiRecord r : res.body().records) {
                        cropList.add(new PriceModel(r.commodity, r.market, r.modal_price, r.arrival_date));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FetchPriceActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            public void onFailure(Call<MandiResponse> call, Throwable t) {
                Toast.makeText(FetchPriceActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", t.getMessage(), t);
            }
        });
    }
}

package com.example.agripricechecker;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchPriceActivity extends AppCompatActivity {

    private Spinner typeSpinner;
    private AutoCompleteTextView cropNameInput;
    private ImageButton clearCropBtn;
    private Button fetchBtn;
    private RecyclerView recyclerView;

    private TextView fetchTitle, typeLabel, cropLabel;

    private Map<String, String> localGlossary = new HashMap<>();

    private final String API_KEY = "579b464db66ec23bdd000001fb75576d11034566571c2b1f40bf316d";

    private List<MandiRecord> mandiList = new ArrayList<>();
    private MandiAdapter mandiAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_price);

        // ✅ Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_fetch));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initGlossary();

        typeSpinner = findViewById(R.id.typeSpinner);
        cropNameInput = findViewById(R.id.cropName);
        clearCropBtn = findViewById(R.id.clearCropBtn);
        fetchBtn = findViewById(R.id.fetchBtn);
        recyclerView = findViewById(R.id.recyclerView);

        fetchTitle = findViewById(R.id.fetchTitle);
        typeLabel = findViewById(R.id.typeLabel);
        cropLabel = findViewById(R.id.cropLabel);

        setupCropTypeSpinner();
        setupAutoComplete();
        refreshUI();

        // ✅ RecyclerView Setup
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mandiAdapter = new MandiAdapter(mandiList);
        recyclerView.setAdapter(mandiAdapter);

        // 🔥 Clear list when clicking input
        cropNameInput.setOnClickListener(v -> {
            mandiList.clear();
            mandiAdapter.notifyDataSetChanged();
            cropNameInput.showDropDown();
        });

        // 🔥 Clear list when focus comes
        cropNameInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mandiList.clear();
                mandiAdapter.notifyDataSetChanged();
            }
        });

        // ✅ Clear button FIXED
        clearCropBtn.setOnClickListener(v -> {

            // Clear text
            cropNameInput.setText("");

            // Clear list
            mandiList.clear();
            mandiAdapter.notifyDataSetChanged();

            // Remove focus
            cropNameInput.clearFocus();

            // Hide dropdown
            cropNameInput.dismissDropDown();
        });

        // ✅ Show/hide clear button dynamically
        cropNameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearCropBtn.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // ✅ Fetch button
        fetchBtn.setOnClickListener(v -> {

            String rawInput = cropNameInput.getText().toString().trim();

            if (rawInput.isEmpty()) {
                Toast.makeText(this,
                        getString(R.string.enter_crop_error),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String processedCrop = translateInput(rawInput);

            // Clear before fetching
            mandiList.clear();
            mandiAdapter.notifyDataSetChanged();

            performPriceFetch(processedCrop);
        });
    }

    // ✅ Back Arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void performPriceFetch(String crop) {

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

                if (response.isSuccessful() && response.body() != null) {

                    List<MandiRecord> records = response.body().records;

                    if (records != null && !records.isEmpty()) {

                        mandiList.clear();

                        for (MandiRecord record : records) {
                            if (record.state != null &&
                                    record.state.equalsIgnoreCase("Uttar Pradesh")) {
                                mandiList.add(record);
                            }
                        }

                        if (!mandiList.isEmpty()) {
                            mandiAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(FetchPriceActivity.this,
                                    getString(R.string.no_up_mandi),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(FetchPriceActivity.this,
                                getString(R.string.no_price_data),
                                Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(FetchPriceActivity.this,
                            "API Error: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<MandiResponse> call, Throwable t) {
                Log.e("NETWORK_ERROR", t.getMessage());
                Toast.makeText(FetchPriceActivity.this,
                        getString(R.string.network_error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUI() {
        cropNameInput.setHint(getString(R.string.enter_crop_name));
        fetchBtn.setText(getString(R.string.fetch_price));
        fetchTitle.setText(getString(R.string.check_crop_price));
        typeLabel.setText(getString(R.string.select_crop_type));
        cropLabel.setText(getString(R.string.select_crop));
    }

    private void initGlossary() {
        localGlossary.put("धान", "Paddy");
        localGlossary.put("गेहूँ", "Wheat");
        localGlossary.put("मक्का", "Maize");
        localGlossary.put("प्याज", "Onion");
        localGlossary.put("आलू", "Potato");
        localGlossary.put("टमाटर", "Tomato");
    }

    private String translateInput(String input) {
        if (localGlossary.containsKey(input)) {
            return localGlossary.get(input);
        }
        return input;
    }

    private void setupCropTypeSpinner() {

        String[] categories = {
                getString(R.string.all),
                getString(R.string.grain),
                getString(R.string.pulse),
                getString(R.string.vegetable),
                getString(R.string.fruit)
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        categories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);
    }

    private void setupAutoComplete() {

        String[] crops = {
                "Rice","Wheat","Maize","Onion","Potato","Tomato",
                "धान","गेहूँ","मक्का","प्याज","आलू","टमाटर"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        crops);

        cropNameInput.setAdapter(adapter);
        cropNameInput.setThreshold(1);
    }
}
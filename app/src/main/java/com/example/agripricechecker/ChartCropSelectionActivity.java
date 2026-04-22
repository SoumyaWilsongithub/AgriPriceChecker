package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class ChartCropSelectionActivity extends AppCompatActivity {

    private Spinner cropSpinner;
    private Button viewChartBtn;
    private TextView tvSelectCrop;
    private Map<String, String> cropTranslationMap = new HashMap<>();
    private boolean isHindi = false;
    private String currentLang = "en";

    // Constants to match your MainActivity's SharedPreferences
    private static final String PREF_NAME = "AgriPrice_Prefs";
    private static final String KEY_LANG = "Locale.Helper.Selected.Language";

    @Override
    protected void attachBaseContext(Context newBase) {
        // Use LocaleHelper to wrap the context with the saved language
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_crop_selection);

        // 1. Initialize Views
        tvSelectCrop = findViewById(R.id.tvSelectCrop);
        cropSpinner = findViewById(R.id.spinnerCropNames);
        viewChartBtn = findViewById(R.id.btnShowChart);

        // 2. Setup Action Bar back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Initialize the translation map
        initCropTranslations();

        // 4. Detect and Set Language
        detectLanguage();

        // 5. Force UI refresh
        refreshUI();

        // 6. Handle Button Click
        viewChartBtn.setOnClickListener(v -> {
            int selectedPosition = cropSpinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                String errorMsg = isHindi ? "कृपया फसल चुनें" : "Please Select Crop";
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            } else {
                String selectedDisplay = cropSpinner.getSelectedItem().toString();
                String selectedCrop = getEnglishName(selectedDisplay);

                // Start ChartActivity and pass the language explicitly
                // to fix the "Date appearing in Hindi" issue
                Intent intent = new Intent(this, ChartActivity.class);
                intent.putExtra("crop", selectedCrop);
                intent.putExtra("lang", currentLang);
                startActivity(intent);
            }
        });
    }

    private void detectLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        currentLang = prefs.getString(KEY_LANG, "en");
        isHindi = "hi".equals(currentLang);
    }

    @Override
    protected void onResume() {
        super.onResume();
        detectLanguage();
        refreshUI();
    }

    private void refreshUI() {
        // Update Action Bar and Static Text
        if (isHindi) {
            if (tvSelectCrop != null) tvSelectCrop.setText("फसल चुनें:");
            if (viewChartBtn != null) viewChartBtn.setText("भाव का रुझान देखें");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("फसल का चयन");
        } else {
            if (tvSelectCrop != null) tvSelectCrop.setText("Select Crop:");
            if (viewChartBtn != null) viewChartBtn.setText("VIEW PRICE TREND");
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Select Crop");
        }

        // Re-setup the spinner with the correct language list
        setupCropSpinner();
    }

    private void setupCropSpinner() {
        List<String> cropList = new ArrayList<>();

        // Add Hint
        cropList.add(isHindi ? "फसल चुनें" : "Select Crop");

        String[] englishCrops = {
                "Wheat", "Rice", "Maize", "Barley", "Potato", "Onion", "Tomato",
                "Moong", "Chana", "Masoor", "Arhar", "Urad", "Apple", "Banana",
                "Grapes", "Mango", "Orange", "Cabbage", "Brinjal", "Jowar", "Bajra"
        };

        for (String crop : englishCrops) {
            if (isHindi && cropTranslationMap.containsKey(crop)) {
                cropList.add(cropTranslationMap.get(crop));
            } else {
                cropList.add(crop);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cropList
        ) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable first item (Hint)
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                applyColor(tv, position);
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                applyColor(tv, position);
                return view;
            }

            private void applyColor(TextView tv, int position) {
                int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                        tv.setTextColor(getResources().getColor(android.R.color.white));
                    } else {
                        tv.setTextColor(getResources().getColor(android.R.color.black));
                    }
                }
            }
        };

        cropSpinner.setAdapter(adapter);
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

    private String getEnglishName(String currentName) {
        if (!isHindi) return currentName;
        for (Map.Entry<String, String> entry : cropTranslationMap.entrySet()) {
            if (entry.getValue().equals(currentName)) {
                return entry.getKey();
            }
        }
        return currentName;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
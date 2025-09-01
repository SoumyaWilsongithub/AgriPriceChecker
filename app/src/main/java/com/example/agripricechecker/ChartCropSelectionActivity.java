package com.example.agripricechecker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class ChartCropSelectionActivity extends AppCompatActivity {

    private Spinner cropSpinner;
    private Button viewChartBtn;
    private List<String> cropList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart_crop_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Select Crop");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cropSpinner = findViewById(R.id.spinnerCropNames);
        viewChartBtn = findViewById(R.id.btnShowChart);

        // Add hint item at top (non-selectable)
        cropList = new ArrayList<>();
        cropList.add("Select Crop"); // hint
        cropList.addAll(Arrays.asList(
                "Wheat", "Rice", "Maize", "Barley", "Potato", "Onion", "Tomato",
                "Moong", "Chana", "Masoor", "Arhar", "Urad", "Apple", "Banana",
                "Grapes", "Mango", "Orange", "Cabbage", "Brinjal", "Jowar", "Bajra"
        ));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                cropList
        ) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the hint item ("Select Crop")
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;

                // Detect dark mode
                int nightModeFlags = getResources().getConfiguration().uiMode &
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK;

                if (position == 0) {
                    tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                        tv.setTextColor(getResources().getColor(android.R.color.white)); // dark mode
                    } else {
                        tv.setTextColor(getResources().getColor(android.R.color.black)); // light mode
                    }
                }
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;

                // Detect dark mode
                int nightModeFlags = getResources().getConfiguration().uiMode &
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK;

                if (position == 0) {
                    tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
                } else {
                    if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                        tv.setTextColor(getResources().getColor(android.R.color.white)); // dark mode
                    } else {
                        tv.setTextColor(getResources().getColor(android.R.color.black)); // light mode
                    }
                }
                return view;
            }
        };

        cropSpinner.setAdapter(adapter);

        viewChartBtn.setOnClickListener(v -> {
            int selectedPosition = cropSpinner.getSelectedItemPosition();
            if (selectedPosition == 0) {
                Toast.makeText(this, "Please select a crop", Toast.LENGTH_SHORT).show();
            } else {
                String selectedCrop = cropSpinner.getSelectedItem().toString();
                Intent intent = new Intent(this, ChartActivity.class);
                intent.putExtra("crop", selectedCrop);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

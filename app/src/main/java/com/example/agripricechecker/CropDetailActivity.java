package com.example.agripricechecker;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agripricechecker.models.CropCalendarModel;
import com.google.firebase.database.*;

public class CropDetailActivity extends AppCompatActivity {

    private TextView cropNameText, sowingText, harvestingText, adviceText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crop Details");
        }

        cropNameText = findViewById(R.id.cropNameDetail);
        sowingText = findViewById(R.id.sowingText);
        harvestingText = findViewById(R.id.harvestText);
        adviceText = findViewById(R.id.adviceText);

        String cropName = getIntent().getStringExtra("cropName");

        if (cropName == null || cropName.isEmpty()) {
            Toast.makeText(this, "No crop selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cropNameText.setText(cropName);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("cropCalendar/cropCalendar/" + cropName);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CropCalendarModel model = snapshot.getValue(CropCalendarModel.class);
                if (model != null) {
                    sowingText.setText("Sowing: " + model.getSowing());
                    harvestingText.setText("Harvesting: " + model.getHarvesting());
                    adviceText.setText("Advice: " + model.getAdvice());
                } else {
                    Toast.makeText(CropDetailActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CropDetailActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Handle back arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

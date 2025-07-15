package com.example.agripricechecker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agripricechecker.models.CropCalendarModel;
import com.google.firebase.database.*;

import java.util.*;

public class CropCalendarActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CropCalendarAdapter adapter;
    private List<String> cropList = new ArrayList<>();
    private List<String> fullCropList = new ArrayList<>();
    private Map<String, CropCalendarModel> cropData = new HashMap<>();
    private DatabaseReference dbRef;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_calendar);

        // Enable back arrow
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Crop Calendar");
        }

        recyclerView = findViewById(R.id.cropCalendarRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);

        adapter = new CropCalendarAdapter(cropList, cropData, cropName -> {
            Intent intent = new Intent(CropCalendarActivity.this, CropDetailActivity.class);
            intent.putExtra("cropName", cropName);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("cropCalendar/cropCalendar");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cropList.clear();
                fullCropList.clear();
                cropData.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String crop = child.getKey();
                    CropCalendarModel model = child.getValue(CropCalendarModel.class);
                    if (crop != null && model != null) {
                        cropList.add(crop);
                        fullCropList.add(crop);
                        cropData.put(crop, model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CropCalendarActivity.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCrop(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCrop(newText);
                return true;
            }
        });
    }

    private void filterCrop(String query) {
        cropList.clear();
        for (String crop : fullCropList) {
            if (crop.toLowerCase().contains(query.toLowerCase())) {
                cropList.add(crop);
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Handle back arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

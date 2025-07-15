package com.example.agripricechecker;

import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agripricechecker.models.FertilizerModel;
import com.google.firebase.database.*;

import java.util.*;

public class FertilizerActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private FertilizerAdapter adapter;
    private List<String> cropList = new ArrayList<>();
    private List<String> fullCropList = new ArrayList<>();
    private Map<String, FertilizerModel> dataMap = new HashMap<>();
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fertilizer);

        recyclerView = findViewById(R.id.fertilizerRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);

        adapter = new FertilizerAdapter(cropList, dataMap, cropName -> {
            FertilizerModel model = dataMap.get(cropName);
            if (model != null) {
                FertilizerDetailActivity.open(this, cropName, model);
            }
        });

        recyclerView.setAdapter(adapter);

        dbRef = FirebaseDatabase.getInstance().getReference("fertilizerRecommendation/fertilizerRecommendations");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cropList.clear();
                fullCropList.clear();
                dataMap.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String crop = child.getKey();
                    FertilizerModel model = child.getValue(FertilizerModel.class);
                    if (crop != null && model != null) {
                        cropList.add(crop);
                        fullCropList.add(crop);
                        dataMap.put(crop, model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FertilizerActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

        // Back arrow in toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Fertilizer Recommendations");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

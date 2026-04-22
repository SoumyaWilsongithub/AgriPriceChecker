package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private boolean isHindi = false;
    private static final String PREF_NAME = "AgriPrice_Prefs";
    private static final String KEY_LANG = "Locale.Helper.Selected.Language";

    private Map<String, String> hindiToEnglish = new HashMap<>();
    private Map<String, String> englishToHindi = new HashMap<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_calendar);

        checkLanguagePreference();
        initTranslations();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isHindi ? "फसल कैलेंडर" : "Crop Calendar");
        }

        recyclerView = findViewById(R.id.cropCalendarRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint(isHindi ? "फसल खोजें" : "Search crop");

        adapter = new CropCalendarAdapter(cropList, cropData, isHindi, displayName -> {
            // FIX: Convert the Display Name (Hindi) back to English Key for Firebase
            String keyToPass = displayName;
            if (isHindi && hindiToEnglish.containsKey(displayName)) {
                keyToPass = hindiToEnglish.get(displayName);
            }

            Intent intent = new Intent(CropCalendarActivity.this, CropDetailActivity.class);
            intent.putExtra("cropName", keyToPass);
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
                    String rawKey = child.getKey();
                    CropCalendarModel model = child.getValue(CropCalendarModel.class);

                    if (rawKey != null && model != null) {
                        String displayName = translateName(rawKey);
                        cropList.add(displayName);
                        fullCropList.add(displayName);

                        // Map the model to the display name so adapter can show it
                        cropData.put(displayName, model);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CropCalendarActivity.this, isHindi ? "डेटा लोड करने में विफल।" : "Failed to load data.", Toast.LENGTH_SHORT).show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { filterCrop(query); return true; }
            @Override
            public boolean onQueryTextChange(String newText) { filterCrop(newText); return true; }
        });
    }

    private void checkLanguagePreference() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANG, "en");
        isHindi = "hi".equals(lang);
    }

    private void initTranslations() {
        addMap("Wheat", "गेहूं");
        addMap("Rice", "धान");
        addMap("Maize", "मक्का");
        addMap("Bajra", "बाजरा");
        addMap("Barley", "जौ");
        addMap("Jowar", "ज्वार");
        addMap("Chili", "मिर्च");
        addMap("Chilli", "मिर्च");
        addMap("Mango", "आम");
        addMap("Masoor", "मसूर");
        addMap("Moong", "मूंग");
        addMap("Papaya", "पपीता");
        addMap("Peas", "मटर");
        addMap("Arhar", "अरहर");
        addMap("Pomegranate", "अनार");
        addMap("Potato", "आलू");
        addMap("Urad", "उड़द");
        addMap("Apple", "सेब");
        addMap("Banana", "केला");
        addMap("Brinjal", "बैंगन");
        addMap("Cabbage", "पत्ता गोभी");
        addMap("Carrot", "गाजर");
        addMap("Cauliflower", "फूलगोभी");
        addMap("Chana", "चना");
        addMap("Citrus", "नींबू वर्गीय");
        addMap("Guava", "अमरूद");
        addMap("Onion", "प्याज");
        addMap("Tomato", "टमाटर");
    }

    private void addMap(String en, String hi) {
        englishToHindi.put(en, hi);
        hindiToEnglish.put(hi, en);
    }

    private String translateName(String name) {
        if (isHindi) {
            if (englishToHindi.containsKey(name)) return englishToHindi.get(name);
            for (String key : englishToHindi.keySet()) {
                if (key.equalsIgnoreCase(name)) return englishToHindi.get(key);
            }
            return name;
        }
        return name;
    }

    private void filterCrop(String query) {
        cropList.clear();
        String lowerQuery = query.toLowerCase();
        for (String crop : fullCropList) {
            if (crop.toLowerCase().contains(lowerQuery)) {
                cropList.add(crop);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

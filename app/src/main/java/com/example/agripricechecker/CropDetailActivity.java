package com.example.agripricechecker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.agripricechecker.models.CropCalendarModel;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class CropDetailActivity extends AppCompatActivity {

    private TextView cropNameText, sowingText, harvestingText, adviceText;
    private Map<String, String> translationMap = new HashMap<>();
    private Map<String, String> hindiToEnglish = new HashMap<>();

    private boolean isHindi = false;
    private static final String PREF_NAME = "AgriPrice_Prefs";
    private static final String KEY_LANG = "Locale.Helper.Selected.Language";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        // 1. Get correct Language
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String lang = prefs.getString(KEY_LANG, "en");
        isHindi = "hi".equals(lang);

        // 2. Initialize Maps
        initComprehensiveTranslations();

        cropNameText = findViewById(R.id.cropNameDetail);
        sowingText = findViewById(R.id.sowingText);
        harvestingText = findViewById(R.id.harvestText);
        adviceText = findViewById(R.id.adviceText);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isHindi ? "फसल का विवरण" : "Crop Detail");
        }

        String receivedName = getIntent().getStringExtra("cropName");

        if (receivedName != null) {
            // CRITICAL FIX: If user is in Hindi, convert "बाजरा" back to "Bajra" for Firebase
            String firebaseKey = receivedName;
            if (isHindi && hindiToEnglish.containsKey(receivedName)) {
                firebaseKey = hindiToEnglish.get(receivedName);
            }

            // Show the translated name in the Title (e.g., "बाजरा")
            cropNameText.setText(getTranslatedValue(firebaseKey));

            // FETCH using the English Key (e.g., "Bajra")
            fetchData(firebaseKey);
        }
    }

    private void fetchData(String cropKey) {
        // ALWAYS query Firebase using the English key
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("cropCalendar/cropCalendar/" + cropKey);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CropCalendarModel model = snapshot.getValue(CropCalendarModel.class);
                if (model != null) {
                    if (isHindi) {
                        sowingText.setText("बुवाई का समय: " + getTranslatedValue(model.getSowing()));
                        harvestingText.setText("कटाई का समय: " + getTranslatedValue(model.getHarvesting()));
                        adviceText.setText("कृषि सलाह: " + getTranslatedValue(model.getAdvice()));
                    } else {
                        sowingText.setText("Sowing: " + (model.getSowing() != null ? model.getSowing() : "N/A"));
                        harvestingText.setText("Harvesting: " + (model.getHarvesting() != null ? model.getHarvesting() : "N/A"));
                        adviceText.setText("Advice: " + (model.getAdvice() != null ? model.getAdvice() : "N/A"));
                    }
                } else {
                    Toast.makeText(CropDetailActivity.this, isHindi ? "डेटा नहीं मिला" : "No data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CropDetailActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTranslatedValue(String rawInput) {
        if (rawInput == null || rawInput.isEmpty() || !isHindi) return rawInput;
        String processed = rawInput;
        for (Map.Entry<String, String> entry : translationMap.entrySet()) {
            processed = processed.replaceAll("(?i)\\b" + java.util.regex.Pattern.quote(entry.getKey()) + "\\b", entry.getValue());
        }
        return processed;
    }

    private void initComprehensiveTranslations() {
        // MAPPING: These must match EXACTLY what is in your RecyclerView and Firebase
        hindiToEnglish.put("सेब", "Apple");
        hindiToEnglish.put("अरहर", "Arhar");
        hindiToEnglish.put("बाजरा", "Bajra");
        hindiToEnglish.put("केला", "Banana");
        hindiToEnglish.put("जौ", "Barley");
        hindiToEnglish.put("बैंगन", "Brinjal");
        hindiToEnglish.put("पत्ता गोभी", "Cabbage");
        hindiToEnglish.put("गाजर", "Carrot");
        hindiToEnglish.put("फूलगोभी", "Cauliflower");
        hindiToEnglish.put("चना", "Chana");
        hindiToEnglish.put("मिर्च", "Chili");
        hindiToEnglish.put("नींबू वर्गीय", "Citrus");
        hindiToEnglish.put("अमरूद", "Guava");
        hindiToEnglish.put("गेहूँ", "Wheat");
        hindiToEnglish.put("धान", "Rice");
        hindiToEnglish.put("टमाटर", "Tomato");
        hindiToEnglish.put("प्याज", "Onion");
        hindiToEnglish.put("मक्का", "Maize");
        hindiToEnglish.put("मूंग", "Moong");
        hindiToEnglish.put("उड़द", "Urad");
        hindiToEnglish.put("मसूर", "Masoor");
        hindiToEnglish.put("मटर", "Peas");
        hindiToEnglish.put("आलू", "Potato");
        hindiToEnglish.put("ज्वार","Sorghum");


        // --- MONTHS ---
        translationMap.put("January", "जनवरी");
        translationMap.put("Jan", "जनवरी");
        translationMap.put("February", "फरवरी");
        translationMap.put("Feb", "फरवरी");
        translationMap.put("March", "मार्च");
        translationMap.put("Mar", "मार्च");
        translationMap.put("April", "अप्रैल");
        translationMap.put("Apr", "अप्रैल");
        translationMap.put("May", "मई");
        translationMap.put("June", "जून");
        translationMap.put("July", "जुलाई");
        translationMap.put("August", "अगस्त");
        translationMap.put("Aug", "अगस्त");
        translationMap.put("September", "सितंबर");
        translationMap.put("Sep", "सितंबर");
        translationMap.put("October", "अक्टूबर");
        translationMap.put("Oct", "अक्टूबर");
        translationMap.put("November", "नवंबर");
        translationMap.put("Nov", "नवंबर");
        translationMap.put("December", "दिसंबर");
        translationMap.put("Dec", "दिसंबर");
        translationMap.put("Year-round", "पूरे साल");

        // --- CEREALS & GRAINS ---
        translationMap.put("Wheat", "गेहूं");
        translationMap.put("Rice", "धान (चावल)");
        translationMap.put("Paddy", "धान");
        translationMap.put("Maize", "मक्का");
        translationMap.put("Barley", "जौ");
        translationMap.put("Millet", "बाजरा");
        translationMap.put("Bajra", "बाजरा");
        translationMap.put("Jowar", "ज्वार");
        translationMap.put("Sorghum", "ज्वार");

        // --- PULSES ---
        translationMap.put("Gram", "चना");
        translationMap.put("Chana", "चना");
        translationMap.put("Moong", "मूंग");
        translationMap.put("Urad", "उड़द");
        translationMap.put("Arhar", "अरहर");
        translationMap.put("Tur", "अरहर");
        translationMap.put("Masoor", "मसूर");
        translationMap.put("Lentil", "मसूर");
        translationMap.put("Peas", "मटर");

        // --- VEGETABLES ---
        translationMap.put("Potato", "आलू");
        translationMap.put("Tomato", "टमाटर");
        translationMap.put("Onion", "प्याज");
        translationMap.put("Garlic", "लहसुन");
        translationMap.put("Ginger", "अदरक");
        translationMap.put("Chili", "मिर्च");
        translationMap.put("Chilli", "मिर्च");
        translationMap.put("Brinjal", "बैंगन");
        translationMap.put("Cabbage", "पत्ता गोभी");
        translationMap.put("Cauliflower", "फूलगोभी");
        translationMap.put("Ladyfinger", "भिंडी");
        translationMap.put("Okra", "भिंडी");
        hindiToEnglish.put("Carrot", "गाजर");
        translationMap.put("Radish", "मूली");
        translationMap.put("Spinach", "पालक");

        // --- FRUITS ---
        translationMap.put("saplings", "रोपण");
        translationMap.put("Mango", "आम");
        translationMap.put("Banana", "केला");
        translationMap.put("Apple", "सेब");
        translationMap.put("Guava", "अमरूद");
        translationMap.put("Pomegranate", "अनार");
        translationMap.put("Grapes", "अंगूर");
        translationMap.put("Papaya", "पपीता");
        translationMap.put("Citrus", "नींबू/संतरा");

        // --- SEASONS & CONNECTORS ---
        translationMap.put("Kharif", "खरीफ");
        translationMap.put("Rabi", "रबी");
        translationMap.put("Zaid", "जायद");
        translationMap.put("Season", "सीजन");
        translationMap.put("Summer", "गर्मी");
        translationMap.put("Winter", "सर्दी");
        translationMap.put("Monsoon", "मानसून");
        translationMap.put("Early", "शुरुआत");
        translationMap.put("Late", "अंत");
        translationMap.put("Mid", "मध्य");
        translationMap.put("To", "से");
        translationMap.put("to", "से");
        translationMap.put("And", "और");
        translationMap.put("Or", "या");
        translationMap.put("Weeks", "हफ्ते");
        translationMap.put("Days", "दिन");

        // --- NEW WORDS ADDED ---
        translationMap.put("With", "के साथ");
        translationMap.put("Also", "भी");
        translationMap.put("Every", "हर");
        translationMap.put("From", "से");
        translationMap.put("Need", "जरूरत");
        translationMap.put("Needs", "जरूरत है");
        translationMap.put("Required", "आवश्यकता");
        translationMap.put("Plant", "पौधा");
        translationMap.put("Water", "पानी");
        translationMap.put("Soil", "मिट्टी");
        translationMap.put("Cold", "ठंडा");
        translationMap.put("Cool", "ठंडा");
        translationMap.put("Warm", "गर्म");
        translationMap.put("Hot", "गर्म");
        translationMap.put("Temperature", "तापमान");
        translationMap.put("Temperatures", "तापमान");
        translationMap.put("Climate", "जलवायु");
        translationMap.put("Weather", "मौसम");
        translationMap.put("Temperate", "समशीतोष्ण");
        translationMap.put("Full sunlight", "पूरी धूप");
        translationMap.put("Partial shade", "आंशिक छाया");
        translationMap.put("High humidity", "अधिक आर्द्रता");
        translationMap.put("Low humidity", "कम आर्द्रता");
        translationMap.put("Cold climate", "ठंडी जलवायु");
        translationMap.put("Warm climate", "गर्म जलवायु");
        translationMap.put("Frost", "पाला");
        translationMap.put("Thrives in dry zones", "शुष्क क्षेत्रों में अच्छी तरह बढ़ता है");
        translationMap.put("Arid region", "शुष्क क्षेत्र");
        translationMap.put("Drought resistant", "सूखा प्रतिरोधी");
        translationMap.put("Requires", "आवश्यकता");
        translationMap.put("proper drainage", "उचित जल निकासी");
        translationMap.put("loamy soil", "दोमट मिट्टी");
        translationMap.put("saline soil", "खारी मिट्टी");
        translationMap.put("saline soils", "खारी मिट्टी");
        translationMap.put("Sandy soil", "रेतीली मिट्टी");
        translationMap.put("Clay soil", "चिकनी मिट्टी");
        translationMap.put("Sandy loam", "रेतीली दोमट");
        translationMap.put("Black soil", "काली मिट्टी");
        translationMap.put("Alluvial soil", "जलोढ़ मिट्टी");
        translationMap.put("Fertile", "उपजाऊ");
        translationMap.put("Minimal", "न्यूनतम");
        translationMap.put("Suited for", "के लिए उपयुक्त");
        translationMap.put("irrigation", "सिंचाई");
        translationMap.put("rich", "उपजाऊ");
        translationMap.put("Spring", "वसंत");
        translationMap.put("Fall", "पतझड़");
        translationMap.put("Autumn", "पतझड़");
        translationMap.put("rich loamy", "उपजाऊ दोमट");
        translationMap.put("Spring/fall", "वसंत या पतझड़");
        translationMap.put("Spring or fall", "वसंत या पतझड़");
        translationMap.put("Spring या fall", "वसंत या पतझड़");
        translationMap.put("spring or fall", "वसंत या पतझड़");
        translationMap.put("8-12 months after flowering", "फूल आने के 8-12 महीने बाद");
        translationMap.put("months after flowering", "फूल आने के बाद के महीने");
        translationMap.put("Grows in all soil types", "सभी प्रकार की मिट्टी में उगता है");
        translationMap.put("good drainage.", "अच्छी जल निकासी।");
        translationMap.put("Drought से tolerant,", "सूखे के प्रति सहनशील,");
        translationMap.put("thrives in semi से arid zones.", "अर्ध-शुष्क क्षेत्रों में अच्छी तरह पनपता है।");
        translationMap.put("well-drained", "अच्छी जल निकासी वाली ");
        translationMap.put("firm well-drained soil", "कठोर अच्छी जल निकासी वाली मिट्टी");
        translationMap.put("firm soil", "कठोर मिट्टी");
        translationMap.put("firm", "कठोर (मजबूत)");
        translationMap.put("Light sandy loam is ideal.", "हल्की रेतीली दोमट मिट्टी आदर्श है।");
        translationMap.put("frequent irrigation", "बार-बार सिंचाई");
        translationMap.put("Frequent irrigation", "बार-बार सिंचाई");
        translationMap.put("Light", "हल्की");
        translationMap.put("is ideal", "आदर्श है");
        translationMap.put("frequent irrigation.", "बार-बार सिंचाई।");
        translationMap.put("Frequent irrigation.", "बार-बार सिंचाई।");
        translationMap.put("regular watering.", "नियमित पानी देना।");
        translationMap.put("regular watering", "नियमित पानी देना");

        // --- PHRASES ---
        translationMap.put("Plant saplings in", "पौधे लगाने का समय");
        translationMap.put("months after planting", "पौधे लगाने के महीने बाद");
        translationMap.put("Requires humid tropical climate", "आर्द्र उष्णकटिबंधीय जलवायु की आवश्यकता है");
        translationMap.put("Needs warm climate", "गर्म जलवायु और पूरी धूप की आवश्यकता है");
        translationMap.put("Needs cold temperate climate", "ठंडी समशीतोष्ण जलवायु की आवश्यकता है");
        translationMap.put("needs minimal water, thrives in dry zones","कम पानी की ज़रूरत होती है, सूखे इलाकों में पनपता है");
        translationMap.put("needs warm climate full sunlight","गर्म जलवायु और पूर्ण सूर्यप्रकाश की आवश्यकता होती है");
        translationMap.put("suited for saline soils with minimal irrigation","कम सिंचाई वाली खारी मिट्टी के लिए उपयुक्त");
        translationMap.put("needs warm temperature ","गर्म तापमान की आवश्यकता है");
        translationMap.put("cold temperature", "ठंडा तापमान");
        translationMap.put("cold climate", "ठंडी जलवायु");
        translationMap.put("Grows", "बढ़ता");
        translationMap.put("in", "में");
        translationMap.put("all", "सभी");
        translationMap.put("types", "प्रकार");
        translationMap.put("good drainage", "निकासी अच्छी");
        translationMap.put("Drought", "सूखा");
        translationMap.put("Tolerant", "सहनशील");
        translationMap.put("Thrives", "पनपता है");
        translationMap.put("Semi-arid", "अर्ध-शुष्क");
        translationMap.put("Zones", "क्षेत्रों");
        translationMap.put("well", "अच्छी तरह से");
        translationMap.put("avoid", "बचें");
        translationMap.put("waterlogging", "जलभराव");
        translationMap.put("dry", "सूखा");
        translationMap.put("Rhizobium seed treatment", "राइजोबियम बीज उपचार");
        translationMap.put("Full sun", "पूर्ण सूर्य");
        translationMap.put("full sun", "पूर्ण सूर्य");
        translationMap.put("free areas", "रिक्त क्षेत्र");
        translationMap.put("crop", "फसल");
        translationMap.put("prefers", "पसंद करता है");
        translationMap.put("loam", "दोमट");
        translationMap.put("Loose", "भुरभुरी / ढीली");
        translationMap.put("standing","ठहरा हुआ");
        translationMap.put("transplanting", "रोपाई");
        translationMap.put("after", "के बाद");
        translationMap.put("months", "महीने");
        translationMap.put("loamy", "दोमट");
        translationMap.put("clayey", "चिकनी");
        translationMap.put("irrigate at CRI stage", "शीर्ष जड़ निकलने की अवस्था (CRI) पर सिंचाई करें");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
package com.example.agripricechecker;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.*;

public class SoilTestActivity extends AppCompatActivity {

    Spinner cropSpinner, soilSpinner;
    Button checkSoilBtn;
    TextView resultText;

    boolean isHindi;

    String[] cropEnglish = {
            "Apple","Arhar","Bajra","Banana","Barley","Brinjal","Cabbage",
            "Carrot","Cauliflower","Chana","Chili","Citrus","Guava",
            "Jowar","Maize","Mango","Masoor","Moong","Onion",
            "Papaya","Peas","Pigeon Pea","Pomegranate","Potato",
            "Rice","Tomato","Urad","Wheat"
    };

    String[] cropHindi = {
            "सेब","अरहर","बाजरा","केला","जौ","बैंगन","पत्ता गोभी",
            "गाजर","फूलगोभी","चना","मिर्च","संतरा","अमरूद",
            "ज्वार","मक्का","आम","मसूर","मूंग","प्याज",
            "पपीता","मटर","अरहर","अनार","आलू",
            "धान","टमाटर","उड़द","गेहूं"
    };

    String[] soilEnglish = {
            "Sandy Soil","Loamy Soil","Clay Soil"
    };

    String[] soilHindi = {
            "बलुई मिट्टी","दोमट मिट्टी","चिकनी मिट्टी"
    };

    HashMap<String,int[]> cropNPK = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_test);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            boolean isHindiTitle = getResources().getConfiguration().locale.getLanguage().equals("hi");
            getSupportActionBar().setTitle(isHindiTitle ? "मिट्टी परीक्षण" : "Soil Test");
        }

        cropSpinner = findViewById(R.id.cropSpinner);
        soilSpinner = findViewById(R.id.soilSpinner);
        checkSoilBtn = findViewById(R.id.checkSoilBtn);
        resultText = findViewById(R.id.resultText);

        isHindi = getResources().getConfiguration().locale.getLanguage().equals("hi");

        setupCropSpinner();
        setupSoilSpinner();

        loadCropData();

        checkSoilBtn.setOnClickListener(v -> {

            int cropPos = cropSpinner.getSelectedItemPosition();
            int soilPos = soilSpinner.getSelectedItemPosition();

            // ✅ VALIDATION (skip first hint item)
            if (cropPos == 0) {
                Toast.makeText(this,
                        isHindi ? "कृपया फसल चुनें" : "Please select crop",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (soilPos == 0) {
                Toast.makeText(this,
                        isHindi ? "कृपया मिट्टी चुनें" : "Please select soil",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            simulateSoilTest(cropPos - 1); // 🔥 FIXED INDEX
        });
    }

    // 🔥 Crop Spinner with Hint
    private void setupCropSpinner() {

        List<String> cropList = new ArrayList<>();

        cropList.add(isHindi ? "फसल चुनें" : "Select Crop");

        if (isHindi) {
            cropList.addAll(Arrays.asList(cropHindi));
        } else {
            cropList.addAll(Arrays.asList(cropEnglish));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cropList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cropSpinner.setAdapter(adapter);
        cropSpinner.setSelection(0);
    }

    // 🔥 Soil Spinner with Hint
    private void setupSoilSpinner() {

        List<String> soilList = new ArrayList<>();

        soilList.add(isHindi ? "मिट्टी चुनें" : "Select Soil");

        if (isHindi) {
            soilList.addAll(Arrays.asList(soilHindi));
        } else {
            soilList.addAll(Arrays.asList(soilEnglish));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                soilList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        soilSpinner.setAdapter(adapter);
        soilSpinner.setSelection(0);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCropData(){

        cropNPK.put("Apple", new int[]{500,250,300});
        cropNPK.put("Arhar", new int[]{20,50,20});
        cropNPK.put("Bajra", new int[]{80,40,40});
        cropNPK.put("Banana", new int[]{200,60,200});
        cropNPK.put("Barley", new int[]{60,30,20});
        cropNPK.put("Brinjal", new int[]{120,60,60});
        cropNPK.put("Cabbage", new int[]{150,80,80});
        cropNPK.put("Carrot", new int[]{60,40,60});
        cropNPK.put("Cauliflower", new int[]{150,100,100});
        cropNPK.put("Chana", new int[]{20,60,40});
        cropNPK.put("Chili", new int[]{100,50,50});
        cropNPK.put("Citrus", new int[]{400,200,200});
        cropNPK.put("Guava", new int[]{300,150,200});
        cropNPK.put("Jowar", new int[]{80,40,40});
        cropNPK.put("Maize", new int[]{150,60,40});
        cropNPK.put("Mango", new int[]{400,200,300});
        cropNPK.put("Masoor", new int[]{20,40,40});
        cropNPK.put("Moong", new int[]{20,40,20});
        cropNPK.put("Onion", new int[]{120,60,60});
        cropNPK.put("Papaya", new int[]{200,100,200});
        cropNPK.put("Peas", new int[]{25,50,40});
        cropNPK.put("Pigeon Pea", new int[]{20,50,20});
        cropNPK.put("Pomegranate", new int[]{250,125,125});
        cropNPK.put("Potato", new int[]{180,80,100});
        cropNPK.put("Rice", new int[]{100,50,40});
        cropNPK.put("Tomato", new int[]{120,80,60});
        cropNPK.put("Urad", new int[]{20,40,20});
        cropNPK.put("Wheat", new int[]{120,60,40});
    }

    private void simulateSoilTest(int cropIndex){

        Random random = new Random();

        int soilN = random.nextInt(100);
        int soilP = random.nextInt(80);
        int soilK = random.nextInt(80);

        String cropKey = cropEnglish[cropIndex];
        int[] req = cropNPK.get(cropKey);

        int needN = Math.max(req[0] - soilN,0);
        int needP = Math.max(req[1] - soilP,0);
        int needK = Math.max(req[2] - soilK,0);

        // ✅ Chemical fertilizers
        double urea = needN / 0.46;
        double dap = needP / 0.46;
        double mop = needK / 0.60;

        // ✅ Organic fertilizers
        int vermicompost = 2 + random.nextInt(3);  // 2-4 tons/ha
        int compost = 3 + random.nextInt(3);       // 3-5 tons/ha

        // ✅ Micronutrients
        String micronutrients = "";
        if(soilN < 30){
            micronutrients += isHindi ? "जिंक की कमी\n" : "Zinc deficiency\n";
        }
        if(soilP < 20){
            micronutrients += isHindi ? "फॉस्फोरस की कमी\n" : "Phosphorus deficiency\n";
        }
        if(soilK < 20){
            micronutrients += isHindi ? "पोटाश की कमी\n" : "Potassium deficiency\n";
        }

        // ✅ Soil health advice
        String soilAdvice;
        if(soilN < 40){
            soilAdvice = isHindi ?
                    "हरी खाद और गोबर खाद का उपयोग करें" :
                    "Use green manure & farmyard manure";
        } else {
            soilAdvice = isHindi ?
                    "मिट्टी अच्छी स्थिति में है" :
                    "Soil condition is good";
        }

        // ✅ Final Result
        String result;

        if(isHindi){
            result = "🌱 मिट्टी परीक्षण रिपोर्ट\n\n"
                    +"फसल : "+cropHindi[cropIndex]+"\n\n"

                    +"🧪 NPK स्तर:\n"
                    +"नाइट्रोजन: "+soilN+"\n"
                    +"फॉस्फोरस: "+soilP+"\n"
                    +"पोटाश: "+soilK+"\n\n"

                    +"💊 रासायनिक उर्वरक:\n"
                    +"यूरिया: "+Math.round(urea)+" kg/ha\n"
                    +"DAP: "+Math.round(dap)+" kg/ha\n"
                    +"MOP: "+Math.round(mop)+" kg/ha\n\n"

                    +"🌿 जैविक उर्वरक:\n"
                    +"वर्मी कम्पोस्ट: "+vermicompost+" ton/ha\n"
                    +"कम्पोस्ट: "+compost+" ton/ha\n\n"

                    +"⚠ सूक्ष्म पोषक तत्व:\n"
                    +(micronutrients.isEmpty() ? "कोई कमी नहीं\n" : micronutrients+"\n")

                    +"📌 सलाह:\n"+soilAdvice;

        } else {
            result = "🌱 Soil Test Report\n\n"
                    +"Crop : "+cropEnglish[cropIndex]+"\n\n"

                    +"🧪 NPK Levels:\n"
                    +"Nitrogen: "+soilN+"\n"
                    +"Phosphorus: "+soilP+"\n"
                    +"Potassium: "+soilK+"\n\n"

                    +"💊 Chemical Fertilizers:\n"
                    +"Urea: "+Math.round(urea)+" kg/ha\n"
                    +"DAP: "+Math.round(dap)+" kg/ha\n"
                    +"MOP: "+Math.round(mop)+" kg/ha\n\n"

                    +"🌿 Organic Fertilizers:\n"
                    +"Vermicompost: "+vermicompost+" ton/ha\n"
                    +"Compost: "+compost+" ton/ha\n\n"

                    +"⚠ Micronutrients:\n"
                    +(micronutrients.isEmpty() ? "No deficiency\n" : micronutrients+"\n")

                    +"📌 Recommendation:\n"+soilAdvice;
        }

        resultText.setText(result);
    }
}
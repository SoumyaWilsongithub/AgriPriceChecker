package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CropDiseaseActivity extends AppCompatActivity {

    private DiseaseClassifier classifier;
    private ImageView imageView;
    private TextView resultTextView, titleTextView, resultLabelTextView;
    private Button btnSelectImage, btnClassify, btnTakePhoto;
    private Bitmap currentBitmap = null;

    private boolean isHindi = false;
    private final Map<String, String> translationMap = new HashMap<>();

    private static final String SELECTED_LANGUAGE = "Locale.Helper.Selected.Language";
    private static final String PREF_NAME = "AgriPrice_Prefs";

    // 📸 Gallery Launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    processSelectedImage(result.getData().getData());
                }
            }
    );

    // 📷 Camera Launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap photo = (Bitmap) extras.get("data");
                        processCapturedImage(photo);
                    }
                }
            }
    );

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_disease);

        // Views
        titleTextView = findViewById(R.id.titleTextView);
        resultTextView = findViewById(R.id.resultTextView);
        resultLabelTextView = findViewById(R.id.resultLabel);
        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnClassify = findViewById(R.id.btnClassify);

        detectLanguagePreference();
        setupTranslationMap();

        // ✅ Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            if (isHindi) {
                actionBar.setTitle("फसल रोग पहचान");
            } else {
                actionBar.setTitle("Disease Detection");
            }
        }

        updateUI();

        try {
            classifier = new DiseaseClassifier(this, "crop_disease.tflite", "labels.txt");
        } catch (IOException e) {
            Toast.makeText(this,
                    isHindi ? "मॉडल लोड करने में त्रुटि!" : "Error loading AI model!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

        btnSelectImage.setOnClickListener(v -> openGallery());
        btnTakePhoto.setOnClickListener(v -> checkPermissionAndLaunchCamera());

        btnClassify.setOnClickListener(v -> {
            if (currentBitmap != null && classifier != null) {
                resultTextView.setText(isHindi ? "जांच हो रही है..." : "Analyzing...");
                String rawResult = classifier.classify(currentBitmap);
                handleOutput(rawResult);
            } else {
                Toast.makeText(this,
                        isHindi ? "कृपया पहले फोटो चुनें" : "Please select a photo first",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔙 Back Arrow
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 🌐 Language
    private void detectLanguagePreference() {
        SharedPreferences preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String lang = preferences.getString(SELECTED_LANGUAGE, "en");
        isHindi = (lang != null && lang.equalsIgnoreCase("hi"));
    }

    private void updateUI() {
        if (isHindi) {
            titleTextView.setText("फसल रोग पहचान");
            resultLabelTextView.setText("जांच का परिणाम:");
            btnTakePhoto.setText("फोटो खींचें");
            btnSelectImage.setText("गैलरी");
            btnClassify.setText("बीमारी पहचानें");
            if (currentBitmap == null) resultTextView.setText("फोटो चुनें।");
        } else {
            titleTextView.setText("Crop Disease Detection");
            resultLabelTextView.setText("Analysis Result:");
            btnTakePhoto.setText("Take Photo");
            btnSelectImage.setText("Gallery");
            btnClassify.setText("Identify Disease");
            if (currentBitmap == null) resultTextView.setText("Select a photo.");
        }
    }

    // 🧠 Translation Map
    private void setupTranslationMap() {

        // 🍎 Apple
        translationMap.put("Apple - Apple Scab", "सेब - एप्पल स्कैब");
        translationMap.put("Apple - Black Rot", "सेब - ब्लैक रॉट");
        translationMap.put("Apple - Cedar Apple Rust", "सेब - सीडर एप्पल रस्ट");
        translationMap.put("Apple - Healthy", "सेब - स्वस्थ");

        // 🫐 Blueberry
        translationMap.put("Blueberry - Healthy", "ब्लूबेरी - स्वस्थ");

        // 🍒 Cherry
        translationMap.put("Cherry - Powdery Mildew", "चेरी - पाउडरी मिल्ड्यू");
        translationMap.put("Cherry - Healthy", "चेरी - स्वस्थ");

        // 🌽 Corn (Maize)
        translationMap.put("Corn (Maize) - Cercospora Leaf Spot & Gray Leaf Spot", "मक्का - सर्कोस्पोरा लीफ स्पॉट");
        translationMap.put("Corn (Maize) - Common Rust", "मक्का - कॉमन रस्ट");
        translationMap.put("Corn (Maize) - Northern Leaf Blight", "मक्का - नॉर्दर्न लीफ ब्लाइट");
        translationMap.put("Corn (Maize) - Healthy", "मक्का - स्वस्थ");

        // 🍇 Grape
        translationMap.put("Grape - Black Rot", "अंगूर - ब्लैक रॉट");
        translationMap.put("Grape - Esca (Black Measles)", "अंगूर - एस्का रोग");
        translationMap.put("Grape - Leaf Blight (Isariopsis Leaf Spot)", "अंगूर - लीफ ब्लाइट");
        translationMap.put("Grape - Healthy", "अंगूर - स्वस्थ");

        // 🍊 Orange
        translationMap.put("Orange - Huanglongbing (Citrus Greening)", "संतरा - सिट्रस ग्रीनिंग रोग");

        // 🍑 Peach
        translationMap.put("Peach - Bacterial Spot", "आड़ू - बैक्टीरियल स्पॉट");
        translationMap.put("Peach - Healthy", "आड़ू - स्वस्थ");

        // 🌶️ Pepper
        translationMap.put("Pepper (Bell) - Bacterial Spot", "शिमला मिर्च - बैक्टीरियल स्पॉट");
        translationMap.put("Pepper (Bell) - Healthy", "शिमला मिर्च - स्वस्थ");

        // 🥔 Potato
        translationMap.put("Potato - Early Blight", "आलू - अगेती झुलसा");
        translationMap.put("Potato - Late Blight", "आलू - पछेती झुलसा");
        translationMap.put("Potato - Healthy", "आलू - स्वस्थ");

        // 🍓 Raspberry
        translationMap.put("Raspberry - Healthy", "रास्पबेरी - स्वस्थ");

        // 🌱 Soybean
        translationMap.put("Soybean - Healthy", "सोयाबीन - स्वस्थ");

        // 🎃 Squash
        translationMap.put("Squash - Powdery Mildew", "स्क्वैश - पाउडरी मिल्ड्यू");

        // 🍓 Strawberry
        translationMap.put("Strawberry - Leaf Scorch", "स्ट्रॉबेरी - लीफ स्कॉर्च");
        translationMap.put("Strawberry - Healthy", "स्ट्रॉबेरी - स्वस्थ");

        // 🍅 Tomato
        translationMap.put("Tomato - Bacterial Spot", "टमाटर - बैक्टीरियल स्पॉट");
        translationMap.put("Tomato - Early Blight", "टमाटर - अगेती झुलसा");
        translationMap.put("Tomato - Late Blight", "टमाटर - पछेती झुलसा");
        translationMap.put("Tomato - Leaf Mold", "टमाटर - लीफ मोल्ड");
        translationMap.put("Tomato - Septoria Leaf Spot", "टमाटर - सेप्टोरिया लीफ स्पॉट");
        translationMap.put("Tomato - Spider Mites (Two-spotted Spider Mite)", "टमाटर - स्पाइडर माइट");
        translationMap.put("Tomato - Target Spot", "टमाटर - टारगेट स्पॉट");
        translationMap.put("Tomato - Tomato Yellow Leaf Curl Virus", "टमाटर - येलो लीफ कर्ल वायरस");
        translationMap.put("Tomato - Tomato Mosaic Virus", "टमाटर - मोज़ेक वायरस");
        translationMap.put("Tomato - Healthy", "टमाटर - स्वस्थ");
    }

    private void handleOutput(String rawResult) {
        if (!isHindi) {
            resultTextView.setText(rawResult);
            applyColor(rawResult);
            return;
        }

        String label = rawResult;
        String confidence = "";

        if (rawResult.contains("(")) {
            int idx = rawResult.indexOf("(");
            label = rawResult.substring(0, idx).trim();
            confidence = " " + rawResult.substring(idx);
        }

        String hindi = translationMap.get(label);
        resultTextView.setText(hindi != null ? hindi + confidence : rawResult);

        applyColor(rawResult);
    }

    private void applyColor(String result) {
        if (result.contains("%")) {
            try {
                float score = Float.parseFloat(result.substring(result.indexOf("(") + 1, result.indexOf("%")));
                if (score > 75) resultTextView.setTextColor(Color.GREEN);
                else if (score < 40) resultTextView.setTextColor(Color.RED);
                else resultTextView.setTextColor(Color.parseColor("#FFA500"));
            } catch (Exception e) {
                resultTextView.setTextColor(Color.BLACK);
            }
        }
    }

    // 🆕 RESET UI (IMPORTANT)
    private void resetUIForNewImage() {
        resultTextView.setText(isHindi ? "नई फोटो लोड हो रही है..." : "Loading new image...");
        resultTextView.setTextColor(Color.BLACK);
        btnClassify.setEnabled(false);
    }

    // 📷 Camera Permission
    private void checkPermissionAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        } else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 100);
        }
    }

    // 🖼️ Open Gallery
    private void openGallery() {
        galleryLauncher.launch(new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    // 📸 Camera Image
    private void processCapturedImage(Bitmap photo) {
        resetUIForNewImage();   // ✅ CLEAR OLD DATA

        currentBitmap = photo;
        imageView.setImageBitmap(photo);

        btnClassify.setEnabled(true);
        resultTextView.setText(isHindi ? "अब बीमारी पहचानें" : "Now click Identify Disease");
    }

    // 🖼️ Gallery Image
    private void processSelectedImage(Uri uri) {
        try {
            resetUIForNewImage();   // ✅ CLEAR OLD DATA

            InputStream stream = getContentResolver().openInputStream(uri);
            currentBitmap = BitmapFactory.decodeStream(stream);

            imageView.setImageBitmap(currentBitmap);

            btnClassify.setEnabled(true);
            resultTextView.setText(isHindi ? "अब बीमारी पहचानें" : "Now click Identify Disease");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) classifier.close();
    }
}
package com.example.agripricechecker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CropDiseaseActivity extends AppCompatActivity {

    private static final String TAG = "CropDiseaseActivity";

    private DiseaseClassifier classifier;
    private ImageView imageView;
    private TextView resultTextView;
    private Button btnSelectImage, btnClassify, btnTakePhoto;

    private Bitmap currentBitmap = null;

    // 1. Gallery Launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    processSelectedImage(imageUri);
                } else {
                    Toast.makeText(this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // 2. Camera Launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Camera Intent usually returns the Bitmap thumbnail in the 'data' extra
                    Bundle extras = result.getData().getExtras();
                    if (extras != null && extras.get("data") instanceof Bitmap) {
                        Bitmap photo = (Bitmap) extras.get("data");
                        processCapturedImage(photo);
                    } else {
                        Toast.makeText(this, "Failed to capture image data. Try gallery.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    // 3. Permission Launcher
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    launchCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_LONG).show();
                }
            }
    );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_disease);

        // Initialize the TFLite Classifier - This is the FIRST potential crash point
        try {
            classifier = new DiseaseClassifier(this);
        } catch (IOException e) {
            Toast.makeText(this, "FATAL: Model loading failed! See Logcat (Tag: DiseaseClassifier).", Toast.LENGTH_LONG).show();
            Log.e(TAG, "App cannot run without model. Finishing activity.", e);
            // Don't finish(), but disable the classify button instead
            // if we can't classify, this activity is useless, so finishing is acceptable.
            finish();
            return;
        }

        imageView = findViewById(R.id.imageView);
        resultTextView = findViewById(R.id.resultTextView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnClassify = findViewById(R.id.btnClassify);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);

        // --- Click Listeners ---

        // Gallery Button
        btnSelectImage.setOnClickListener(v -> openGallery());

        // Camera Button
        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndLaunch());

        // Classify Button - This is the SECOND potential crash point
        btnClassify.setOnClickListener(v -> {
            if (currentBitmap != null && classifier != null) {
                resultTextView.setText("Classifying...");

                // Perform classification on a background thread in a real app,
                // but for a simple example, we run it directly.
                String result = classifier.classify(currentBitmap);

                resultTextView.setText(result);
            } else {
                Toast.makeText(this, "Please select or take a photo first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Camera/Permission Methods ---
    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No camera app found on device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processCapturedImage(Bitmap photo) {
        if (photo != null) {
            currentBitmap = photo;
            imageView.setImageBitmap(currentBitmap);
            btnClassify.setEnabled(true);
            resultTextView.setText("Photo captured. Ready to classify.");
        }
    }

    // --- Gallery/Image Selection Methods ---
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void processSelectedImage(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(imageStream);

            if (selectedBitmap != null) {
                // Resize the image to fit the ImageView better and save memory
                // Note: The ImageProcessor handles the model's required resize
                // This is only for the ImageView preview
                currentBitmap = selectedBitmap;
                imageView.setImageBitmap(currentBitmap);
                btnClassify.setEnabled(true);
                resultTextView.setText("Image selected. Ready to classify.");
            } else {
                Toast.makeText(this, "Could not decode image.", Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error processing image URI", e);
            Toast.makeText(this, "File not found.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Clean up ---
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (classifier != null) {
            classifier.close();
        }
        if (currentBitmap != null) {
            currentBitmap.recycle();
            currentBitmap = null;
        }
    }
}
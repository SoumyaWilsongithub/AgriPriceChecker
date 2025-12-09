package com.example.agripricechecker;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ImageButton btnFetchPrice, btnChart, btnTip, btnNews, btnWeather, btnDisease;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Initialize buttons
        btnFetchPrice = findViewById(R.id.btnFetchPrice);
        btnChart = findViewById(R.id.btnChart);
        btnTip = findViewById(R.id.btnTip);
        btnNews = findViewById(R.id.btnNews);
        btnWeather = findViewById(R.id.btnWeather);
        btnDisease = findViewById(R.id.btnDisease);

        // Button click listeners
        btnFetchPrice.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FetchPriceActivity.class)));

        btnChart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChartCropSelectionActivity.class)));

        btnTip.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CropCalendarActivity.class)));

        btnNews.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FertilizerActivity.class)));

        btnWeather.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WeatherActivity.class)));

       // btnDisease.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CropDiseaseActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    mAuth.signOut();
                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}

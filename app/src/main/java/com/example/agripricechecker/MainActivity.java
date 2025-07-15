package com.example.agripricechecker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ImageButton btnFetchPrice, btnChart, btnTip, btnNews;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your existing layout

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize buttons
        btnFetchPrice = findViewById(R.id.btnFetchPrice);
        btnChart = findViewById(R.id.btnChart);
        btnTip = findViewById(R.id.btnTip);
        btnNews = findViewById(R.id.btnNews);


        // Set up button click actions
        btnFetchPrice.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FetchPriceActivity.class));
        });

        btnChart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChartCropSelectionActivity.class));
        });

        btnTip.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CropCalendarActivity.class));
        });

        btnNews.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, FertilizerActivity.class));
        });
    }

    // Inflate the logout icon in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu); // menu with logout icon
        return true;
    }

    // Handle menu item click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Logout confirmation dialog
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

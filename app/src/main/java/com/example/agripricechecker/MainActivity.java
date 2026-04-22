package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    ImageButton btnFetchPrice, btnChart, btnTip, btnNews, btnWeather, btnDisease;
    TextView tvWelcome;
    FirebaseAuth mAuth;

    // These MUST match the constants used in CropDiseaseActivity and LocaleHelper
    private static final String PREF_NAME = "AgriPrice_Prefs";
    private static final String KEY_LANG = "Locale.Helper.Selected.Language";

    @Override
    protected void attachBaseContext(Context newBase) {
        // This is critical: it applies the saved language to the activity context
        // before the UI is even drawn.
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.title_main));
        }

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI Elements
        tvWelcome = findViewById(R.id.tvWelcome);
        btnFetchPrice = findViewById(R.id.btnFetchPrice);
        btnChart = findViewById(R.id.btnChart);
        btnTip = findViewById(R.id.btnTip);
        btnNews = findViewById(R.id.btnNews);
        btnWeather = findViewById(R.id.btnWeather);
        btnDisease = findViewById(R.id.btnDisease);

        // Click listeners to launch your other activities
        btnFetchPrice.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FetchPriceActivity.class)));
        btnChart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChartCropSelectionActivity.class)));
        btnTip.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CropCalendarActivity.class)));
        btnNews.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SoilTestActivity.class)));
        btnWeather.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WeatherActivity.class)));
        btnDisease.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CropDiseaseActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            showLogoutConfirmationDialog();
            return true;
        } else if (id == R.id.action_language) {
            showLanguageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        String[] languages = {"English", "हिन्दी (Hindi)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Setting a bilingual title for the dialog
        builder.setTitle("Choose Language / भाषा चुनें");

        builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
            String selectedLang = (which == 0) ? "en" : "hi";

            // 1. Save the choice and update configuration via LocaleHelper
            LocaleHelper.setLocale(MainActivity.this, selectedLang);

            // 2. Restart MainActivity to apply language changes to all UI strings immediately
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            dialog.dismiss();
        });
        builder.create().show();
    }

    private void showLogoutConfirmationDialog() {

        String title = getString(R.string.logout_title);
        String message = getString(R.string.logout_message);

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)

                // ✅ Language-based buttons
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })

                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss())

                .show();
    }
}
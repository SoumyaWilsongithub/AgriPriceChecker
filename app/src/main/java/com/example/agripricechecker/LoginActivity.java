package com.example.agripricechecker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button loginBtn, btnSwitchLang;
    TextView gotoSignup, tvTitle;
    FirebaseAuth mAuth;
    boolean isHindi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Detect current preference
        detectLanguage();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        boolean fromSignup = getIntent().getBooleanExtra("FROM_SIGNUP", false);
        if (currentUser != null && !fromSignup) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.login);

        // Initialize Views
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoSignup = findViewById(R.id.gotoSignup);
        tvTitle = findViewById(R.id.loginTitle); // Make sure you have an ID for "Login to Kisan Sathi"

        // Use an existing button or add one to your XML with ID 'btnSwitchLang'
        btnSwitchLang = findViewById(R.id.btnSwitchLang);

        // 2. Initial UI translation
        applyTranslations();

        if (fromSignup) {
            Toast.makeText(this, isHindi ? "साइनअप सफल! लॉगिन करें" : "Signup successful! Please log in.", Toast.LENGTH_LONG).show();
        }

        // Language Switcher Logic
        btnSwitchLang.setOnClickListener(v -> {
            isHindi = !isHindi;
            saveLanguagePreference(isHindi ? "hi" : "en");
            applyTranslations(); // Refresh UI text immediately
        });

        loginBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (emailText.isEmpty()) {
                email.setError(isHindi ? "ईमेल आवश्यक है" : "Email is required");
                return;
            }

            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String errorMsg = isHindi ? "प्रमाणीकरण विफल" : "Authentication Failed";
                            Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        gotoSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void detectLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "en");
        isHindi = lang.equals("hi");
    }

    private void saveLanguagePreference(String langCode) {
        SharedPreferences prefs = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("My_Lang", langCode);
        editor.apply();
    }

    private void applyTranslations() {
        if (isHindi) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("लॉगिन");
            if (tvTitle != null) tvTitle.setText("किसान साथी में लॉगिन करें");
            email.setHint("ईमेल");
            password.setHint("पासवर्ड");
            loginBtn.setText("लॉगिन");
            gotoSignup.setText("खाता नहीं है? साइन अप करें");
            btnSwitchLang.setText("English"); // Button shows option to switch back
        } else {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("Login");
            if (tvTitle != null) tvTitle.setText("Login to Kisan Sathi");
            email.setHint("Email");
            password.setHint("Password");
            loginBtn.setText("LOGIN");
            gotoSignup.setText("Don't have an account? Sign Up");
            btnSwitchLang.setText("हिन्दी"); // Button shows option to switch back
        }
    }
}
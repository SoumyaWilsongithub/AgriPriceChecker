package com.example.agripricechecker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    // Using exact variables from your XML
    private EditText signupName, signupEmail, signupPassword;
    private Button signupBtn;
    private TextView gotoLogin;

    // Additional UI for Header and Language Switch
    private TextView signupHeaderTitle;
    private Button btnSwitchLang;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private boolean isHindi = false;

    // Model for Firebase
    public static class User {
        public String fullName, email;
        public User() {}
        public User(String fullName, String email) {
            this.fullName = fullName;
            this.email = email;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize variables using your exact XML IDs
        signupName = findViewById(R.id.signupName);
        signupEmail = findViewById(R.id.signupEmail);
        signupPassword = findViewById(R.id.signupPassword);
        signupBtn = findViewById(R.id.signupBtn);
        gotoLogin = findViewById(R.id.gotoLogin);

        // These need to be added to your XML if not already present for the toggle to work
        signupHeaderTitle = findViewById(R.id.signupHeaderTitle);
        btnSwitchLang = findViewById(R.id.btnSwitchLang);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        // Language Switch Logic
        if (btnSwitchLang != null) {
            btnSwitchLang.setOnClickListener(v -> {
                isHindi = !isHindi;
                updateLanguage();
            });
        }

        signupBtn.setOnClickListener(v -> {
            performSignup();
        });

        gotoLogin.setOnClickListener(v -> {
            // Navigate back to login
            finish();
        });
    }

    private void updateLanguage() {
        if (isHindi) {
            if (signupHeaderTitle != null) signupHeaderTitle.setText("खाता बनाएं");
            signupName.setHint("पूरा नाम");
            signupEmail.setHint("ईमेल");
            signupPassword.setHint("पासवर्ड");
            signupBtn.setText("साइन अप करें");
            gotoLogin.setText("क्या आपके पास पहले से खाता है? लॉगिन करें");
            if (btnSwitchLang != null) btnSwitchLang.setText("English");
        } else {
            if (signupHeaderTitle != null) signupHeaderTitle.setText("Create an Account");
            signupName.setHint("Full Name");
            signupEmail.setHint("Email");
            signupPassword.setHint("Password");
            signupBtn.setText("Sign Up");
            gotoLogin.setText("Already have an account? Login");
            if (btnSwitchLang != null) btnSwitchLang.setText("हिन्दी");
        }
    }

    private void performSignup() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String pass = signupPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            String errorMsg = isHindi ? "कृपया सभी विवरण भरें" : "Please fill all fields";
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if (fUser != null) {
                            userRef.child(fUser.getUid()).setValue(new User(name, email));

                            String successMsg = isHindi ? "पंजीकरण सफल!" : "Registration Successful!";
                            Toast.makeText(SignupActivity.this, successMsg, Toast.LENGTH_SHORT).show();

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                finish();
                            }, 1500);
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
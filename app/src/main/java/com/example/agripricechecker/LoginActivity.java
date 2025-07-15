package com.example.agripricechecker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView gotoSignup;
    FirebaseAuth mAuth;
    // ProgressBar progressBar; // Optional: for visual feedback

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Get the intent extra
        boolean fromSignup = getIntent().getBooleanExtra("FROM_SIGNUP", false);

        // If fromSignup is true, we want to remove the extra so that if the user
        // presses back and returns to LoginActivity, it doesn't think it's still "fromSignup".
        if (fromSignup) {
            getIntent().removeExtra("FROM_SIGNUP");
        }

        // ✅ Check if user is already logged in AND not coming directly from signup
        if (currentUser != null && !fromSignup) {
            Log.d("LoginActivity", "User already logged in. Redirecting to MainActivity.");
            startActivity(new Intent(this, MainActivity.class));
            finish(); // Finish LoginActivity so user can't go back to it
            return;   // Stop further execution of onCreate
        }

        // If we are coming from signup OR if no user is logged in (or currentUser is null), show the login screen
        setContentView(R.layout.login); // Assuming your layout file is login.xml or similar

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoSignup = findViewById(R.id.gotoSignup);
        // progressBar = findViewById(R.id.loginProgressBar); // Optional

        if (fromSignup) {
            Toast.makeText(this, "Signup successful! Please log in.", Toast.LENGTH_LONG).show();
        }


        loginBtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            if (emailText.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.setError("Please enter a valid email");
                email.requestFocus();
                return;
            }
            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            // Show progress bar / disable button (optional)
            // loginBtn.setEnabled(false);
            // progressBar.setVisibility(View.VISIBLE);

            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Hide progress bar / re-enable button (optional)
                            // loginBtn.setEnabled(true);
                            // progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                Log.d("LoginActivity", "Login Successful");
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                                // No need for a Handler here unless you want a deliberate delay
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                                startActivity(intent);
                                finish(); // Finish LoginActivity
                            } else {
                                Log.w("LoginActivity", "Login Failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication Failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        gotoSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));

        });
    }


}

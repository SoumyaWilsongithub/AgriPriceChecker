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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText name, email, password;
    Button signupBtn;
    TextView gotoLogin;
    FirebaseAuth mAuth;
    DatabaseReference userRef;

    // A simple UserModel class (you might have this in a separate file)
    // If you have a more complex UserModel, ensure it has a no-argument constructor
    // for Firebase, or adjust how you create/save it.
    public static class UserModel {
        public String name;
        public String email;

        public UserModel() {
            // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
        }

        public UserModel(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup); // Assuming your layout file is signup.xml or similar

        // Initialize UI components
        name = findViewById(R.id.signupName);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        signupBtn = findViewById(R.id.signupBtn);
        gotoLogin = findViewById(R.id.gotoLogin);

        // Firebase instances
        mAuth = FirebaseAuth.getInstance();
        // Make sure your Firebase database rules allow writes to the "users" path
        userRef = FirebaseDatabase.getInstance().getReference("users");

        signupBtn.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            // Input validation
            if (nameText.isEmpty()) {
                name.setError("Name is required");
                name.requestFocus();
                return;
            }
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
            if (passwordText.length() < 6) {
                password.setError("Password must be at least 6 characters");
                password.requestFocus();
                return;
            }

            // Show a progress bar or disable button during signup (optional but good UX)
            // signupBtn.setEnabled(false);
            // progressBar.setVisibility(View.VISIBLE);

            // Firebase Signup
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Re-enable button or hide progress bar
                            // signupBtn.setEnabled(true);
                            // progressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    String uid = firebaseUser.getUid();
                                    UserModel user = new UserModel(nameText, emailText);

                                    userRef.child(uid).setValue(user)
                                            .addOnCompleteListener(dbTask -> {
                                                if (dbTask.isSuccessful()) {
                                                    Log.d("SignupActivity", "User data saved to database.");
                                                } else {
                                                    Log.e("SignupActivity", "Failed to save user data.", dbTask.getException());
                                                    // You might want to inform the user or handle this error
                                                }
                                            });

                                    Toast.makeText(SignupActivity.this, "Signup Successful. Please login.", Toast.LENGTH_LONG).show();

                                    // Navigate to LoginActivity after signup with an extra
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        intent.putExtra("FROM_SIGNUP", true); // Add this extra
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                                        startActivity(intent);
                                        finish(); // Finish SignupActivity
                                    }, 1500); // 1.5-second delay
                                } else {
                                    Log.e("SignupError", "User is null after successful creation.");
                                    Toast.makeText(SignupActivity.this, "Signup failed: User data error.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("SignupError", "Signup failed", task.getException());
                                Toast.makeText(SignupActivity.this,
                                        "Signup Failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        // Switch to Login
        gotoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clears activities on top of LoginActivity
            startActivity(intent);
            // finish(); // Optional: finish SignupActivity if you don't want it in back stack
        });
    }
}

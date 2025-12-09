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
    public static class UserModel {
        public String name;
        public String email;
        public UserModel() {
        }
        public UserModel(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        name = findViewById(R.id.signupName);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signupPassword);
        signupBtn = findViewById(R.id.signupBtn);
        gotoLogin = findViewById(R.id.gotoLogin);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");
        signupBtn.setOnClickListener(v -> {
            String nameText = name.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
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
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
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
                                                    Log.e("SignupActivity", "Failed to save user data."
                                                            , dbTask.getException());
                                                }
                                            });
                                    Toast.makeText(SignupActivity.this, "Signup Successful. Please login."
                                            , Toast.LENGTH_LONG).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        intent.putExtra("FROM_SIGNUP", true); // Add this extra
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }, 1500);
                                } else {
                                    Log.e("SignupError", "User is null after successful creation.");
                                    Toast.makeText(SignupActivity.this, "Signup failed: User data error."
                                            , Toast.LENGTH_LONG).show();
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
        gotoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean fromSignup = getIntent().
                getBooleanExtra("FROM_SIGNUP", false);
        if (fromSignup) {
            getIntent().removeExtra("FROM_SIGNUP");
        }
        if (currentUser != null && !fromSignup) {
            Log.d("LoginActivity", "User already logged in. Redirecting to MainActivity.");
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.login);
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);
        gotoSignup = findViewById(R.id.gotoSignup);
        if (fromSignup) {
            Toast.makeText(this, "Signup successful! Please log in.",
                    Toast.LENGTH_LONG).show();
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
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("LoginActivity", "Login Successful");
                                Toast.makeText(LoginActivity.this, "Login Successful",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.w("LoginActivity", "Login Failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication Failed: "
                                                + task.getException().getMessage(),
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

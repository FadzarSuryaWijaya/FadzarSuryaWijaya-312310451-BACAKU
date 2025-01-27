package com.example.testfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private EditText signupUsername, signupPassword, signupPasswordConfirmation;
    private Button signupButton;
    private TextView backToLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupUsername = findViewById(R.id.signupUsername);
        signupPassword = findViewById(R.id.signupPassword);
        signupPasswordConfirmation = findViewById(R.id.supasswordConfirmation);
        signupButton = findViewById(R.id.signupButton);
        backToLogin = findViewById(R.id.backtoLogin);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(v -> {
            String email = signupUsername.getText().toString().trim() + "@example.com";
            String password = signupPassword.getText().toString().trim();
            String confirmPassword = signupPasswordConfirmation.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                signupUsername.setError("Username required");
                return;
            }
            if (TextUtils.isEmpty(password) || password.length() < 6) {
                signupPassword.setError("Password must be at least 6 characters");
                return;
            }
            if (!password.equals(confirmPassword)) {
                signupPasswordConfirmation.setError("Passwords do not match");
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("email", email);
                            userMap.put("isAdmin", false);  // Register everyone as regular user initially

                            // Store user info in Firestore
                            db.collection("users").document(userId).set(userMap)
                                    .addOnCompleteListener(docTask -> {
                                        if (docTask.isSuccessful()) {
                                            Toast.makeText(SignUp.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(SignUp.this, login.class));
                                            finish();
                                        } else {
                                            Toast.makeText(SignUp.this, "Failed to store user info", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignUp.this, "Username already registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignUp.this, login.class));
            finish();
        });
    }
}

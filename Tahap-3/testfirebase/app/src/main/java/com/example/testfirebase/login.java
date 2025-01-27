package com.example.testfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.style.ForegroundColorSpan;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.testfirebase.admin.AdminBookActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class login extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private TextView signupText;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        // Change color of "SignUp" to blue
        String text = "Not yet registered? SignUp Now";
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("SignUp");
        int end = start + "SignUp".length();
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        signupText.setText(spannableString);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim() + "@example.com";
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                checkIfAdmin(user);  // Call the checkIfAdmin method here
                            }
                        } else {
                            Toast.makeText(login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, SignUp.class);
            startActivity(intent);
        });
    }

    // Method to check if the user is an admin
    private void checkIfAdmin(FirebaseUser user) {
        user.getIdToken(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> claims = task.getResult().getClaims();
                Boolean isAdmin = claims != null ? (Boolean) claims.get("admin") : null;
                if (isAdmin != null && isAdmin) {
                    saveUserRole("admin"); // Save role as admin
                    startActivity(new Intent(login.this, AdminBookActivity.class));
                } else {
                    saveUserRole("user"); // Save role as user
                    startActivity(new Intent(login.this, MainActivity.class));
                }
                finish();
            } else {
                Toast.makeText(login.this, "Failed to fetch user claims: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save user role in SharedPreferences
    private void saveUserRole(String role) {
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_role", role);
        editor.apply();
    }
}

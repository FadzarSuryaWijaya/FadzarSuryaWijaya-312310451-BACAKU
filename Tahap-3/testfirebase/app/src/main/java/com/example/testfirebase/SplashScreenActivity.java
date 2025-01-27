package com.example.testfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 3 seconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Cek status login setelah beberapa detik
        new android.os.Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // Jika sudah login, arahkan ke MainActivity
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
            } else {
                // Jika belum login, arahkan ke LoginActivity
                startActivity(new Intent(SplashScreenActivity.this, login.class));
            }
            finish();
        }, SPLASH_TIME_OUT);
    }
}

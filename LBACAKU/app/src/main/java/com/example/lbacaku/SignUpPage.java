package com.example.lbacaku;

import android.view.inputmethod.EditorInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpPage extends AppCompatActivity {

    EditText signupUsername, signupPassword, supasswordConfirmation;
    Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signupUsername = findViewById(R.id.signupUsername);
        signupPassword = findViewById(R.id.signupPassword);
        supasswordConfirmation = findViewById(R.id.supasswordConfirmation);
        signupButton = findViewById(R.id.signupButton);

        // Set imeOptions untuk mencegah input baris baru
        signupUsername.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        signupPassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        supasswordConfirmation.setImeOptions(EditorInfo.IME_ACTION_DONE);

        signupButton.setOnClickListener(view -> {
            String username = signupUsername.getText().toString();
            String password = signupPassword.getText().toString();
            String passwordConf = supasswordConfirmation.getText().toString();

            if (username.isEmpty() || password.isEmpty() || passwordConf.isEmpty()) {
                Toast.makeText(SignUpPage.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(passwordConf)) {
                Toast.makeText(SignUpPage.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpPage.this, "Password must be at least 6 characters!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUpPage.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

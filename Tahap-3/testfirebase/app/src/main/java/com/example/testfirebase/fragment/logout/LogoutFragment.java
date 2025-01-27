package com.example.testfirebase.fragment.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.testfirebase.R;
import com.example.testfirebase.login;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        mAuth = FirebaseAuth.getInstance();

        logout();
        return view;
    }

    private void logout() {
        // Log out from Firebase
        mAuth.signOut();

        // Show a toast message
        Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();

        // Navigate back to the login activity
        Intent loginIntent = new Intent(getActivity(), login.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}

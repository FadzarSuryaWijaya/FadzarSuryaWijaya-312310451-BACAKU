package com.example.testfirebase.admin;

import com.example.testfirebase.Book;
import com.example.testfirebase.BookApi;
import com.example.testfirebase.MainActivity;
import com.example.testfirebase.ProfileActivity;
import com.example.testfirebase.R;
import com.example.testfirebase.RetrofitClient;
import com.example.testfirebase.fragment.profile.ProfileFragment;
import com.example.testfirebase.fragment.logout.LogoutFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBookActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerView;
    private AdminBookAdapter bookAdapter;
    private List<Book> bookList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check user role in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String userRole = preferences.getString("user_role", "user");

        // If user role is not admin, redirect to MainActivity
        if (!"admin".equals(userRole)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();  // Close AdminBookActivity
            return;
        }

        // User is logged in and is an admin, proceed with AdminBookActivity
        setContentView(R.layout.activity_admin_book);

        // Custom Toolbar setup
        View customToolbar = findViewById(R.id.custom_toolbar);
        ImageButton navigationIcon = customToolbar.findViewById(R.id.navigation_icon);
        navigationIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        // DrawerLayout setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set navigation header title for admin
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        navHeaderTitle.setText("Admin Dashboard");

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewAdminBooks);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddBook);

        // Initialize book list
        bookList = new ArrayList<>();

        // Set up RecyclerView with Grid Layout (2 columns)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        bookAdapter = new AdminBookAdapter(bookList);
        recyclerView.setAdapter(bookAdapter);

        // FAB click listener
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AdminBookActivity.this, AddBookActivity.class);
            startActivity(intent);
        });

        // Load books
        loadBooks();
    }

    private void loadBooks() {
        BookApi bookApi = RetrofitClient.getClient("http://192.168.1.5/bacaku/").create(BookApi.class);
        bookApi.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookList.clear();
                    bookList.addAll(response.body());
                    bookAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                // Handle error
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks(); // Reload books when returning to this activity
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Handle navigation view item clicks
        if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (itemId == R.id.nav_logout) {
            replaceFragment(new LogoutFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.END); // Close drawer from right side
        return true;
    }

    // Method to replace the current fragment with a new fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

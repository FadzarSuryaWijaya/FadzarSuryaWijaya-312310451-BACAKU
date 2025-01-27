package com.example.testfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testfirebase.admin.AdminBookActivity;
import com.example.testfirebase.fragment.profile.ProfileFragment;
import com.example.testfirebase.fragment.logout.LogoutFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recyclerViewSelfHelp;
    private RecyclerView recyclerViewFiction;
    private RecyclerView recyclerViewSciFi;
    private EditText searchInput;
    private BookAdapter bookAdapterSelfHelp;
    private BookAdapter bookAdapterFiction;
    private BookAdapter bookAdapterSciFi;
    private DrawerLayout drawerLayout;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Check if user is logged in
        if (currentUser == null) {
            // If not logged in, redirect to Login Activity
            startActivity(new Intent(this, login.class));
            finish();  // Close MainActivity
            return;
        }

        // Check user role in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
        String userRole = preferences.getString("user_role", "user");

        // If user role is admin, redirect to AdminBookActivity
        if ("admin".equals(userRole)) {
            startActivity(new Intent(this, AdminBookActivity.class));
            finish();  // Close MainActivity
            return;
        }

        // User is logged in and not an admin, proceed with MainActivity
        setContentView(R.layout.activity_main);

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set navigation header title for user
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderTitle = headerView.findViewById(R.id.nav_header_title);
        navHeaderTitle.setText("User Dashboard");

        // Initialize UI components
        searchInput = findViewById(R.id.searchInput);
        recyclerViewSelfHelp = findViewById(R.id.recyclerViewSelfHelp);
        recyclerViewFiction = findViewById(R.id.recyclerViewFiction);
        recyclerViewSciFi = findViewById(R.id.recyclerViewSciFi);

        recyclerViewSelfHelp.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFiction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSciFi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Initialize adapters
        bookAdapterSelfHelp = new BookAdapter(new ArrayList<>(), this::showBookDetail);
        bookAdapterFiction = new BookAdapter(new ArrayList<>(), this::showBookDetail);
        bookAdapterSciFi = new BookAdapter(new ArrayList<>(), this::showBookDetail);

        recyclerViewSelfHelp.setAdapter(bookAdapterSelfHelp);
        recyclerViewFiction.setAdapter(bookAdapterFiction);
        recyclerViewSciFi.setAdapter(bookAdapterSciFi);

        // Fetch books data from server
        fetchBooksData();

        // Event listener for search input
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchBooks(query);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a search term", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (itemId == R.id.nav_logout) {
            replaceFragment(new LogoutFragment());
        }

        drawerLayout.closeDrawer(GravityCompat.END);  // Use END instead of LEFT
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void fetchBooksData() {
        BookApi bookApi = RetrofitClient.getClient("http://192.168.1.5/bacaku/").create(BookApi.class);

        bookApi.getBooks().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body();

                    List<Book> selfHelpBooks = new ArrayList<>();
                    List<Book> fictionBooks = new ArrayList<>();
                    List<Book> sciFiBooks = new ArrayList<>();

                    for (Book book : books) {
                        if (book.getCategory() == null) continue;

                        switch (book.getCategory().toLowerCase()) {
                            case "self help":
                                selfHelpBooks.add(book);
                                break;
                            case "fiction":
                                fictionBooks.add(book);
                                break;
                            case "science fiction":
                                sciFiBooks.add(book);
                                break;
                        }
                    }

                    bookAdapterSelfHelp.updateBooks(selfHelpBooks);
                    bookAdapterFiction.updateBooks(fictionBooks);
                    bookAdapterSciFi.updateBooks(sciFiBooks);
                } else {
                    Log.e("MainActivity", "Failed to retrieve data");
                    Toast.makeText(MainActivity.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.e("MainActivity", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchBooks(String query) {
        BookApi bookApi = RetrofitClient.getClient("http://192.168.1.5/bacaku/").create(BookApi.class);
        bookApi.searchBooks(query).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Book> books = response.body();
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putParcelableArrayListExtra("SEARCH_RESULTS", new ArrayList<>(books));
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "No books found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed to fetch books", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Network Error: " + t.getMessage());
            }
        });
    }

    private void showBookDetail(Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_TITLE, book.getTitle());
        intent.putExtra(BookDetailActivity.EXTRA_AUTHOR, book.getAuthor());

        try {
            byte[] imageBytes = Base64.decode(book.getImageResourceId(), Base64.DEFAULT);
            File file = new File(getCacheDir(), "book_image_" + book.getTitle() + ".png");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
            }
            intent.putExtra(BookDetailActivity.EXTRA_IMAGE, file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        intent.putExtra(BookDetailActivity.EXTRA_PDF_FILE_NAME, book.getPdfUrl());
        intent.putExtra(BookDetailActivity.EXTRA_SYNOPSIS, book.getSynopsis());
        startActivity(intent);
    }

    // Method for manually opening the drawer (if needed)
    public void onMenuIconClick(View view) {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.END);  // This is set to 'END' to open the drawer from the right side
        }
    }
}

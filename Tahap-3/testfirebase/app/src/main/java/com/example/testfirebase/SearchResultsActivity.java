package com.example.testfirebase;

import android.os.Bundle;
import android.content.Intent;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView searchResultsRecyclerView;
    private BookAdapter searchAdapter;
    private ImageView buttonBack;  // Declare back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialize back button
        ImageView buttonBack = findViewById(R.id.button_back);

        // Set click listener for back button
        buttonBack.setOnClickListener(v -> finish()); // Close the activity when back button is pressed

        // Handle Android system back button press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();  // Finish current activity
            }
        });


        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchAdapter = new BookAdapter(new ArrayList<>(), this::showBookDetail);
        searchResultsRecyclerView.setAdapter(searchAdapter);

        List<Book> searchResults = getIntent().getParcelableArrayListExtra("SEARCH_RESULTS");

        if (searchResults != null) {
            searchAdapter.updateBooks(searchResults);
        } else {
            Toast.makeText(this, "No search results found", Toast.LENGTH_SHORT).show();
        }
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
}

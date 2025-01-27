package com.example.testfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;

public class BookDetailActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_AUTHOR = "author";
    public static final String EXTRA_IMAGE = "image"; // Path file lokal
    public static final String EXTRA_PDF_FILE_NAME = "EXTRA_PDF_FILE_NAME";
    public static final String EXTRA_SYNOPSIS = "synopsis";

    private static final String TAG = "BookDetailActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String author = intent.getStringExtra(EXTRA_AUTHOR);
        String imageResourceId = intent.getStringExtra(EXTRA_IMAGE); // Path file lokal
        String pdfUrl = intent.getStringExtra(EXTRA_PDF_FILE_NAME);
        String synopsis = intent.getStringExtra(EXTRA_SYNOPSIS);

        Log.d(TAG, "PDF URL in BookDetailActivity: " + pdfUrl);

        ImageView bookImage = findViewById(R.id.book_image);
        TextView bookTitle = findViewById(R.id.book_title);
        TextView bookAuthor = findViewById(R.id.book_author);
        TextView bookSynopsis = findViewById(R.id.book_synopsis); // Assuming synopsis is part of the book data
        Button readButton = findViewById(R.id.read_button);
        ImageView backButton = findViewById(R.id.button_back);

        // Load image using Glide
        File imageFile = new File(imageResourceId);
        Glide.with(this)
                .load(imageFile)
                .placeholder(R.drawable.placeholder)
                .into(bookImage);

        bookTitle.setText(title);
        bookAuthor.setText(author);
        bookSynopsis.setText(synopsis);

        if (pdfUrl != null && !pdfUrl.isEmpty()) {
            Log.d(TAG, "PDF URL is not null or empty, setting onClickListener");
            readButton.setOnClickListener(v -> {
                Intent readerIntent = new Intent(BookDetailActivity.this, StreamPdfReaderActivity.class);
                readerIntent.putExtra("PDF_URL", pdfUrl);
                startActivity(readerIntent);
            });
        } else {
            Log.e(TAG, "PDF URL is null or empty");
            readButton.setOnClickListener(v -> {
                Toast.makeText(BookDetailActivity.this, "No PDF URL available", Toast.LENGTH_SHORT).show();
            });
        }

        backButton.setOnClickListener(v -> finish());
    }
}

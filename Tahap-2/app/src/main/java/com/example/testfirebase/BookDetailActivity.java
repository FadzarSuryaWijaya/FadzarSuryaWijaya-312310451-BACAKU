package com.example.testfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BookDetailActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_AUTHOR = "author";
    public static final String EXTRA_IMAGE = "image";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String author = intent.getStringExtra(EXTRA_AUTHOR);
        int imageResourceId = intent.getIntExtra(EXTRA_IMAGE, -1);

        ImageView bookImage = findViewById(R.id.book_image);
        TextView bookTitle = findViewById(R.id.book_title);
        TextView bookAuthor = findViewById(R.id.book_author);
        TextView bookSynopsis = findViewById(R.id.book_synopsis); // Assuming synopsis is part of the book data
        Button readButton = findViewById(R.id.read_button);
        ImageView backButton = findViewById(R.id.button_back);

        bookImage.setImageResource(imageResourceId);
        bookTitle.setText(title);
        bookAuthor.setText(author);
        // Set synopsis if available
        bookSynopsis.setText("Synopsis: Lorem ipsum dolor sit amet...");

        readButton.setOnClickListener(v -> {
            // Logika untuk tombol "Read"
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
            }
        });
        // Logika untuk tombol "backButton"
    }
}

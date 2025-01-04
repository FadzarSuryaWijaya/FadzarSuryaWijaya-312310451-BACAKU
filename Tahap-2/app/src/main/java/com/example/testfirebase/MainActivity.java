package com.example.testfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSelfHelp;
    private RecyclerView recyclerViewFiction;
    private RecyclerView recyclerViewSciFi;
    private BookAdapter bookAdapterSelfHelp;
    private BookAdapter bookAdapterFiction;
    private BookAdapter bookAdapterSciFi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi RecyclerView untuk setiap kategori
        recyclerViewSelfHelp = findViewById(R.id.recyclerViewSelfHelp);
        recyclerViewFiction = findViewById(R.id.recyclerViewFiction);
        recyclerViewSciFi = findViewById(R.id.recyclerViewSciFi);

        // Set LayoutManager untuk RecyclerView
        recyclerViewSelfHelp.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewFiction.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSciFi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Buat dan set adapter untuk setiap RecyclerView
        bookAdapterSelfHelp = new BookAdapter(getBooksSelfHelp(), new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                showBookDetail(book);
            }
        });
        bookAdapterFiction = new BookAdapter(getBooksFiction(), new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                showBookDetail(book);
            }
        });
        bookAdapterSciFi = new BookAdapter(getBooksSciFi(), new BookAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Book book) {
                showBookDetail(book);
            }
        });

        recyclerViewSelfHelp.setAdapter(bookAdapterSelfHelp);
        recyclerViewFiction.setAdapter(bookAdapterFiction);
        recyclerViewSciFi.setAdapter(bookAdapterSciFi);
    }

    // Metode untuk mendapatkan daftar buku Self Help
    private List<Book> getBooksSelfHelp() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Be Not Far From Me", "by Mindy Mcginnis", R.drawable.benotfar));
        books.add(new Book("Atomic Habits", "by James Clear", R.drawable.atomichabit));
        books.add(new Book("Normal People", "by Sally Rooney", R.drawable.normalpeople));
        books.add(new Book("Every Day", "by David Lathan", R.drawable.everyday));
        return books;
    }

    // Metode untuk mendapatkan daftar buku Fiction
    private List<Book> getBooksFiction() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Harry Potter", "by J.K. Rowling", R.drawable.harrypotter));
        books.add(new Book("The Catcher In The Rye", "by J.K. Rowling", R.drawable.thecatcherintherye));
        books.add(new Book("Norwegian Wood", "by J.K. Rowling", R.drawable.norwegianwood));
        books.add(new Book("Harry Potter", "by J.K. Rowling", R.drawable.harrypotter));
        return books;
    }

    // Metode untuk mendapatkan daftar buku Science Fiction
    private List<Book> getBooksSciFi() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("Dune", "by Frank Herbert", R.drawable.dune));
        books.add(new Book("Dune", "by Frank Herbert", R.drawable.dune));
        books.add(new Book("Dune", "by Frank Herbert", R.drawable.dune));
        books.add(new Book("Dune", "by Frank Herbert", R.drawable.dune));
        return books;
    }

    // Metode untuk menampilkan detail buku
    private void showBookDetail(Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_TITLE, book.getTitle());
        intent.putExtra(BookDetailActivity.EXTRA_AUTHOR, book.getAuthor());
        intent.putExtra(BookDetailActivity.EXTRA_IMAGE, book.getImageResourceId());
        startActivity(intent);
    }
}

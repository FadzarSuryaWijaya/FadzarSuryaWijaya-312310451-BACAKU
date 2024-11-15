package com.example.lbacaku;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Data untuk setiap kategori
        List<Book> selfHelpBooks = Arrays.asList(
                new Book("Be Not Far", R.drawable.benotfar),
                new Book("Turtle Under Ice", R.drawable.turtleunderice),
                new Book("Normal People", R.drawable.normalpeople),
                new Book("Every Day", R.drawable.everyday)
        );

        List<Book> historyBooks = Arrays.asList(
                new Book("Wonder", R.drawable.wonder),
                new Book("The Catcher in the Rye", R.drawable.thecatcherintherye),
                new Book("Norwegian Wood", R.drawable.norwegianwood),
                new Book("Harry Potter", R.drawable.harrypotter)
        );

        List<Book> novelBooks = Arrays.asList(
                new Book("1984", R.drawable.cover1984),
                new Book("The Fault in Our Stars", R.drawable.thefaultinourstars),
                new Book("Animal Farm", R.drawable.animalfarm),
                new Book("The World of Ice & Fire", R.drawable.theworldoficeandfire)
        );

        // Setup RecyclerViews
        setupRecyclerView(R.id.recyclerSelfHelp, selfHelpBooks);
        setupRecyclerView(R.id.recyclerHistory, historyBooks);
        setupRecyclerView(R.id.recyclerNovel, novelBooks);
    }

    private void setupRecyclerView(int recyclerViewId, List<Book> books) {
        RecyclerView recyclerView = findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(8));
        recyclerView.setAdapter(new BookAdapter(books));
    }

    private class HorizontalSpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public HorizontalSpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.left = space;
            }
            outRect.right = space;
        }
    }
}
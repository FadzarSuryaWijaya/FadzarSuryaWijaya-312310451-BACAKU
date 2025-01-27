package com.example.testfirebase.admin;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.testfirebase.Book;
import com.example.testfirebase.R;

import java.util.List;

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder> {
    private List<Book> bookList;

    public AdminBookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public AdminBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_book, parent, false);
        return new AdminBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookViewHolder holder, int position) {
        Book book = bookList.get(position);

        holder.titleView.setText(book.getTitle());
        holder.authorView.setText(book.getAuthor());
        holder.synopsisView.setText(book.getSynopsis());

        // Decode and load image
        byte[] imageBytes = Base64.decode(book.getImageResourceId(), Base64.DEFAULT);
        Glide.with(holder.imageView.getContext())
                .asBitmap()
                .load(imageBytes)
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    static class AdminBookViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;
        TextView authorView;
        TextView synopsisView;

        public AdminBookViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.adminBookImage);
            titleView = itemView.findViewById(R.id.adminBookTitle);
            authorView = itemView.findViewById(R.id.adminBookAuthor);
            synopsisView = itemView.findViewById(R.id.adminBookSynopsis);
        }
    }
}

package com.example.testfirebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    private final List<Book> bookList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Book book);
    }

    public BookAdapter(List<Book> bookList, OnItemClickListener listener) {
        this.bookList = bookList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.bind(book, listener);
        holder.titleTextView.setText(book.getTitle());
        holder.authorTextView.setText(book.getAuthor());
        Glide.with(holder.imageView.getContext())
                .load(book.getImageResourceId())
                .placeholder(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView authorTextView;
        ImageView imageView;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.book_title);
            authorTextView = itemView.findViewById(R.id.book_author);
            imageView = itemView.findViewById(R.id.book_image);
        }

        public void bind(final Book book, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(book);
                }
            });
        }
    }
}

package com.example.testfirebase;

public class Book {
    private final String title;
    private final String author;
    private final int imageResourceId;

    public Book(String title, String author, int imageResourceId) {
        this.title = title;
        this.author = author;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

}

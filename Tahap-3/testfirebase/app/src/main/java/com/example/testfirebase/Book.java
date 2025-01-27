package com.example.testfirebase;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
    private String title;
    private String author;
    private String synopsis;
    private String pdfUrl;
    private String imageResourceId;
    private String category;

    public Book() {
        // Default constructor required for calls to DataSnapshot.getValue(Book.class)
    }

    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
        synopsis = in.readString();
        pdfUrl = in.readString();
        imageResourceId = in.readString();
        category = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(synopsis);
        dest.writeString(pdfUrl);
        dest.writeString(imageResourceId);
        dest.writeString(category);
    }

    // Getters and setters...
    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getAuthor() {

        return author;
    }

    public void setAuthor(String author) {

        this.author = author;
    }

    public String getSynopsis() {

        return synopsis;
    }

    public void setSynopsis(String synopsis) {

        this.synopsis = synopsis;
    }

    public String getPdfUrl() {

        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {

        this.pdfUrl = pdfUrl;
    }

    public String getImageResourceId() {

        return imageResourceId;
    }

    public void setImageResourceId(String imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {

        this.category = category;
    }
}

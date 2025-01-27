package com.example.testfirebase;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface BookApi {
    @GET("api.php")
    Call<List<Book>> getBooks();

    @POST("api.php")
    Call<Void> addBook(@Body Book book);

    @GET("api.php/search")
    Call<List<Book>> searchBooks(@Query("query") String query);
    // Menghapus metode yang tidak digunakan lagi
    // Add method for uploading profile image
    @Multipart
    @POST("upload.php") // replace with your actual endpoint
    Call<ResponseBody> uploadProfileImage(
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part image
    );
}


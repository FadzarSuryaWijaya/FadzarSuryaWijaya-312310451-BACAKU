package com.example.testfirebase.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testfirebase.Book;
import com.example.testfirebase.BookApi;
import com.example.testfirebase.R;
import com.example.testfirebase.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBookActivity extends AppCompatActivity {
    private static final String TAG = "AddBookActivity";

    private ImageView imagePreview;
    private EditText titleInput, authorInput, synopsisInput, pdfUrlInput; // Tambah pdfUrlInput
    private Button selectImageBtn, submitBtn;
    private Spinner categorySpinner;

    private String encodedImage;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        // Initialize views
        imagePreview = findViewById(R.id.imagePreview);
        titleInput = findViewById(R.id.titleInput);
        authorInput = findViewById(R.id.authorInput);
        synopsisInput = findViewById(R.id.synopsisInput);
        selectImageBtn = findViewById(R.id.selectImageButton);
        submitBtn = findViewById(R.id.submitButton);
        categorySpinner = findViewById(R.id.categorySpinner);
        pdfUrlInput = findViewById(R.id.pdfUrlInput); // Inisialisasi pdfUrlInput

        // Initialize ActivityResultLauncher for image picking
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            imagePreview.setImageBitmap(bitmap);

                            // Convert bitmap to base64
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // Set up button listeners
        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
        });

        submitBtn.setOnClickListener(v -> submitBook());
    }

    private void submitBook() {
        // Validate inputs
        if (titleInput.getText().toString().isEmpty() ||
                authorInput.getText().toString().isEmpty() ||
                synopsisInput.getText().toString().isEmpty() ||
                encodedImage == null ||
                pdfUrlInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create book object
        Book book = new Book();
        book.setTitle(titleInput.getText().toString());
        book.setAuthor(authorInput.getText().toString());
        book.setSynopsis(synopsisInput.getText().toString());
        book.setImageResourceId(encodedImage);
        book.setCategory(categorySpinner.getSelectedItem().toString());
        book.setPdfUrl(pdfUrlInput.getText().toString()); // Set URL PDF dari input

        // Submit to server
        BookApi bookApi = RetrofitClient.getClient("http://192.168.1.5/bacaku/").create(BookApi.class);
        bookApi.addBook(book).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddBookActivity.this, "Book added successfully", Toast.LENGTH_SHORT).show();
                    // Navigate back to admin book list
                    startActivity(new Intent(AddBookActivity.this, AdminBookActivity.class));
                    finish();
                } else {
                    Toast.makeText(AddBookActivity.this, "Failed to add book", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddBookActivity.this, "Failed to add book", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failure: " + t.getMessage());
            }
        });
    }
}

package com.example.testfirebase;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;


import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView profileUsername, profileStatus, profileEmail, profilePhoneNumber, profileAge;
    private ImageView editPhoneNumberIcon, editAgeIcon, buttonBack;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private static final int REQUEST_READ_STORAGE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        profilePicture = findViewById(R.id.profile_picture);
        profileUsername = findViewById(R.id.profile_username);
        profileStatus = findViewById(R.id.profile_status);
        profileEmail = findViewById(R.id.profile_email);
        profilePhoneNumber = findViewById(R.id.profile_phone_number);
        profileAge = findViewById(R.id.profile_age);
        editPhoneNumberIcon = findViewById(R.id.edit_phone_number_icon);
        editAgeIcon = findViewById(R.id.edit_age_icon);
        buttonBack = findViewById(R.id.button_back);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        // Handle back button
        buttonBack.setOnClickListener(v -> finish());

        // Image picker launcher
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            saveImage(selectedImageUri); // You already handle saving the image here
                            loadImage(selectedImageUri); // Load the selected image into the ImageView
                        }
                    }
                });

        if (currentUser != null) {
            // Fetch user data from Firestore
            fetchUserData();

            // Load profile picture
            loadSavedImage();

            // Set up editing icons
            editPhoneNumberIcon.setOnClickListener(v -> editProfileField(profilePhoneNumber, "phone"));
            editAgeIcon.setOnClickListener(v -> editProfileField(profileAge, "age"));

            // Profile picture click listener
            profilePicture.setOnClickListener(v -> checkAndRequestPermission());
        }
    }

    // Fetch user information from Firestore and update UI
    private void fetchUserData() {
        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());
            userRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String phone = documentSnapshot.getString("phone");
                            String age = documentSnapshot.getString("age");

                            profileUsername.setText(currentUser.getEmail().split("@")[0]);
                            profileEmail.setText(currentUser.getEmail());
                            profilePhoneNumber.setText(phone != null ? phone : "Enter Phone Number");
                            profileAge.setText(age != null ? age : "Enter Age");

                            // Load role and status
                            SharedPreferences preferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                            String userRole = preferences.getString("user_role", "user");
                            profileStatus.setText(userRole.equals("admin") ? "Admin" : "User");
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());
        }
    }

    // Edit profile field (phone or age)
    private void editProfileField(TextView textView, String fieldType) {
        textView.setVisibility(View.GONE);

        // Create an EditText for the user to input their new value
        EditText editText = new EditText(this);
        editText.setText(textView.getText());
        editText.setBackgroundResource(R.drawable.edit_text_background);

        // Add the EditText to the layout
        LinearLayout parent = (LinearLayout) textView.getParent();
        int index = parent.indexOfChild(textView);
        parent.removeView(textView);
        parent.addView(editText, index);

        // Create a save icon to save the changes
        ImageView saveIcon = new ImageView(this);
        saveIcon.setImageResource(R.drawable.ic_save);
        saveIcon.setOnClickListener(v -> saveChanges(editText, fieldType));

        // Add the save icon
        parent.addView(saveIcon);
    }

    // Save changes to Firestore
    private void saveChanges(EditText editText, String fieldType) {
        String newValue = editText.getText().toString().trim();
        if (newValue.isEmpty()) {
            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            DocumentReference userRef = db.collection("users").document(currentUser.getUid());

            // Update Firestore with the new value
            userRef.update(fieldType, newValue)
                    .addOnSuccessListener(aVoid -> {
                        // Update the UI after saving changes
                        if (fieldType.equals("phone")) {
                            profilePhoneNumber.setText(newValue);
                        } else if (fieldType.equals("age")) {
                            profileAge.setText(newValue);
                        }

                        // Hide the EditText and show the TextView again
                        editText.setVisibility(View.GONE);
                        TextView textView = fieldType.equals("phone") ? profilePhoneNumber : profileAge;
                        textView.setText(newValue);
                        textView.setVisibility(View.VISIBLE);
                        Toast.makeText(ProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Error saving changes", Toast.LENGTH_SHORT).show());
        }
    }

    // Profile image permission check
    private void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_READ_STORAGE_PERMISSION);
            } else {
                openImagePicker();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // Android 6 to 12
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE_PERMISSION);
            } else {
                openImagePicker();
            }
        } else {
            // For devices below Android 6, permissions are granted at install time
            openImagePicker();
        }
    }

    // Open image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // Add this flag
        pickImageLauncher.launch(intent);
    }

    // Save image URI to SharedPreferences
    private void saveImage(Uri imageUri) {
        SharedPreferences prefs = getSharedPreferences("profile_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("profile_image_uri", imageUri.toString());
        editor.apply();

        // Convert image URI to Bitmap and upload to server
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            //downscale image then compress image
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);// Reduced quality to 80%

            byte[] imageData = byteArrayOutputStream.toByteArray();

            // Prepare data to send to your API
            String userId = currentUser != null ? currentUser.getUid() : "";

            // Upload the image to the server
            uploadImageToServer(imageData, userId);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(Uri imageUri) {
        Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.baseline_person_24)
                .into(profilePicture);
    }

    // Upload image to the server
    private void uploadImageToServer(byte[] imageData, String userId) {
        Log.d("ImageDataLength", "Length: " + imageData.length);

        if (imageData.length == 0) {
            Log.e("Image Upload", "Image data is empty");
            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.5/bacaku/")  // Ganti dengan URL server Anda
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BookApi bookApi = retrofit.create(BookApi.class);

        // Siapkan RequestBody untuk userId
        RequestBody userIdBody = RequestBody.create(userId, MediaType.parse("text/plain"));

        // Siapkan RequestBody untuk gambar (image/jpeg atau sesuai dengan format gambar yang digunakan)
        RequestBody requestFile = RequestBody.create(imageData, MediaType.parse("image/jpeg"));

        // Membuat MultipartBody.Part untuk gambar
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", "profile.jpg", requestFile);

        // Panggil API untuk upload gambar
        Call<ResponseBody> call = bookApi.uploadProfileImage(userIdBody, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Upload Success", "Image uploaded successfully");
                    Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Upload Error", "Error: " + response.message());
                    Toast.makeText(ProfileActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload Error", "Error: " + t.getMessage());
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


        // Load saved profile image
        private void loadSavedImage() {
            if (currentUser != null) {
                String userId = currentUser.getUid();
                String imageUrl = "http://192.168.1.5/bacaku/get.php?user_id=" + userId;

                // Definisikan Transformasi Lingkaran
                Transformation transformation = new RoundedTransformationBuilder()
                        .oval(true)
                        .build();

                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.baseline_person_24)
                        .error(R.drawable.error_placeholder)
                        .transform(transformation)
                        .into(profilePicture);
            }
        }


    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied to access storage", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

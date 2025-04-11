package com.example.myfinalloginpage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner itemSpinner, hostelSpinner;
    private EditText captionInput, priceInput, descriptionInput;
    private ImageView selectedImageView;
    private Uri selectedImageUri;

    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images");

        // Initialize UI elements
        itemSpinner = findViewById(R.id.itemSpinner);
        hostelSpinner = findViewById(R.id.HostelList);
        captionInput = findViewById(R.id.captionInput);
        priceInput = findViewById(R.id.priceInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        selectedImageView = findViewById(R.id.selectedImageView);
        Button addImageButton = findViewById(R.id.addImageButton);
        Button postButton = findViewById(R.id.postButton);

        // Populate Spinners
        itemSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Fan", "Trunk", "Study Table", "Notes", "PYQs", "Other"}));

        hostelSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Shri Shanta Bai Shiksha Kutir", "Shri Shanta Soudh", "Shri Shanta Vishwa Needam", "Shri Shanta Nilaya"}));

        addImageButton.setOnClickListener(v -> openImagePicker());
        postButton.setOnClickListener(v -> handlePostButtonClick());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImageView.setImageURI(selectedImageUri);
                Log.d("PostActivity", "Selected Image URI: " + selectedImageUri.toString());
            } else {
                Log.e("PostActivity", "Image selection failed: Uri is null");
                Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handlePostButtonClick() {
        String selectedItem = itemSpinner.getSelectedItem().toString();
        String hostel = hostelSpinner.getSelectedItem().toString();
        String caption = captionInput.getText().toString().trim();
        String price = priceInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (caption.isEmpty() || price.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(this, "Please fill all fields and add an image", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToFirebase(selectedItem, caption, price, description, hostel);
    }

    private void uploadImageToFirebase(String selectedItem, String caption, String price, String description, String hostel) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Log.e("PostActivity", "selectedImageUri is null");
            Toast.makeText(this, "selectedImageUri is null", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = FirebaseStorage.getInstance().getReference(fileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getTask().isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            saveDataToFirestore(selectedItem, caption, price, description, downloadUri.toString(), hostel);
                        }).addOnFailureListener(e -> {
                            Log.e("PostActivity", "Failed to get image URL: " + e.getMessage());
                            Toast.makeText(this, "Failed to get image URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Log.e("PostActivity", "Image upload successful: " + taskSnapshot.getError().getMessage());
                        Toast.makeText(this, "Image upload successful: " + taskSnapshot.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PostActivity", "Image upload failed: " + e.getMessage());
                    Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void saveDataToFirestore(String selectedItem, String caption, String price, String description, String imageUrl, String hostel) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        long timestamp = System.currentTimeMillis();
        String contact = "9876543210"; // Dummy contact, replace with real one

        Map<String, Object> product = new HashMap<>();
        product.put("selectedItem", selectedItem);
        product.put("caption", caption);
        product.put("price", price);
        product.put("description", description);
        product.put("imageUrl", imageUrl);
        product.put("userId", userId);
        product.put("hostel", hostel);
        product.put("contact", contact);
        product.put("timestamp", timestamp);

        firestore.collection("products").add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(PostActivity.this, " ", Toast.LENGTH_SHORT).show();
                    resetFormFields();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PostActivity.this, " ", Toast.LENGTH_SHORT).show();
                });
    }

    private void resetFormFields() {
        captionInput.setText("");
        priceInput.setText("");
        descriptionInput.setText("");
        selectedImageView.setImageResource(0);
        selectedImageUri = null;
        itemSpinner.setSelection(0);
        hostelSpinner.setSelection(0);
    }
}

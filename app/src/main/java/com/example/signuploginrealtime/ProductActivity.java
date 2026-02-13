package com.example.signuploginrealtime;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProductActivity extends AppCompatActivity {

    private TextView categoryTitle, captionTextView, priceTextView, descriptionTextView, sellerContactTextView;
    private ImageView contactButton, locationButton, productImageView;

    // Update this key to the one matching your dummy entry in the RTDB
    private static final String DUMMY_PRODUCT_KEY = "dummyProduct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);

        // Initialize UI elements
        categoryTitle = findViewById(R.id.categoryTitle);
        captionTextView = findViewById(R.id.caption);
        priceTextView = findViewById(R.id.price);
        descriptionTextView = findViewById(R.id.description);
        sellerContactTextView = findViewById(R.id.sellerContact);
        contactButton = findViewById(R.id.contactButton);
        locationButton = findViewById(R.id.locationButton);
        productImageView = findViewById(R.id.productImageView); // Use a single ImageView instead of ViewPager

        contactButton.setOnClickListener(v -> openKeypad());
        locationButton.setOnClickListener(v -> openMap());


    }

    private void openKeypad() {
        String contactNumber = sellerContactTextView.getText().toString().trim();
        if (!contactNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + contactNumber));
            startActivity(intent);
        } else {
            Toast.makeText(this, "Contact number is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        Intent mapIntent = new Intent(ProductActivity.this, FindermapActivity.class);
        startActivity(mapIntent);
    }


}

package com.example.myfinalloginpage;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private GoogleMap previewMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;

    private TextView fullnameTextView; // Reference to display username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        fullnameTextView = findViewById(R.id.textView4); // Replace with actual TextView ID

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch username from Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class);
                        fullnameTextView.setText(username); // Display username
                    } else {
                        Toast.makeText(ProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Error fetching username", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }


        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the map preview in a card view
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.preview_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set click listener on the map preview card
        TextView mapPreviewCard = findViewById(R.id.mapbox);
        mapPreviewCard.setOnClickListener(v -> {
            // Open the full map activity when clicked
            Intent intent = new Intent(ProfileActivity.this, FullmapActivity.class);
            if (currentLocation != null) {
                intent.putExtra("LATITUDE", currentLocation.latitude);
                intent.putExtra("LONGITUDE", currentLocation.longitude);
            }
            startActivity(intent);
        });

        // Request location permissions if needed
        checkLocationPermission();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //setting button
        ImageView set = findViewById(R.id.imageView2);
        set.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,SettingsActivity.class);
            startActivity(intent);
            Toast.makeText(ProfileActivity.this, "Settings", Toast.LENGTH_LONG).show();
        });
        //account wala button
        ImageView acc = findViewById(R.id.imageView3);
        acc.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,AccountActivity.class);
            startActivity(intent);
            Toast.makeText(ProfileActivity.this, "Account", Toast.LENGTH_LONG).show();
        });
        //home page wala button
        ImageView home = findViewById(R.id.imageView4);
        home.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
            startActivity(intent);
            Toast.makeText(ProfileActivity.this, "Home", Toast.LENGTH_LONG).show();
        });
        //post button
        ImageView post = findViewById(R.id.imageView5);
        post.setOnClickListener( v -> {
            Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
            startActivity(intent);
            Toast.makeText(ProfileActivity.this, "Post", Toast.LENGTH_LONG).show();
                }
        );
        //help button
        ImageView help = findViewById(R.id.imageView7);
        help.setOnClickListener( v -> {
                    Intent intent = new Intent(ProfileActivity.this, helpActivity.class);
                    startActivity(intent);
                    Toast.makeText(ProfileActivity.this, "Help", Toast.LENGTH_LONG).show();
                }
        );

        //order button
        ImageView order = findViewById(R.id.imageView6);
        order.setOnClickListener( v -> {
                    Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
                    startActivity(intent);
                    Toast.makeText(ProfileActivity.this, "Order", Toast.LENGTH_LONG).show();
                }
        );
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getDeviceLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getDeviceLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                updateMapWithLocation();
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMapWithLocation() {
        if (previewMap != null && currentLocation != null) {
            previewMap.clear();
            previewMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location"));
            previewMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

            // Disable user interaction with the preview map
            previewMap.getUiSettings().setAllGesturesEnabled(false);
            previewMap.getUiSettings().setMapToolbarEnabled(false);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        previewMap = googleMap;

        // If we already have the location, update the map
        if (currentLocation != null) {
            updateMapWithLocation();
        } else {
            // Try to get location if we haven't already
            getDeviceLocation();
        }
    }
}
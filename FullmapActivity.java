package com.example.myfinalloginpage;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class FullmapActivity extends AppCompatActivity implements OnMapReadyCallback{

    private static final String TAG = "FullMapActivity";
    private GoogleMap mMap;
    private LatLng userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);

        // Get user's location from intent
        if (getIntent().hasExtra("LATITUDE") && getIntent().hasExtra("LONGITUDE")) {
            double latitude = getIntent().getDoubleExtra("LATITUDE", 0);
            double longitude = getIntent().getDoubleExtra("LONGITUDE", 0);
            userLocation = new LatLng(latitude, longitude);
        }

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.full_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Enable user location (blue dot) if we have permission
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.e(TAG, "Error enabling my location: " + e.getMessage());
        }

        // Set UI settings for full map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        // Show user's location marker
        if (userLocation != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        }

        // Load hostels from Firebase
        loadHostelsFromFirebase();
    }

    private void loadHostelsFromFirebase() {
        DatabaseReference hostelsRef = FirebaseDatabase.getInstance().getReference("hostels");

        hostelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot hostelSnapshot : dataSnapshot.getChildren()) {
                    try {
                        // Assuming your Firebase data structure has latitude and longitude fields
                        String name = hostelSnapshot.child("name").getValue(String.class);
                        Double latitude = hostelSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = hostelSnapshot.child("longitude").getValue(Double.class);

                        if (name != null && latitude != null && longitude != null) {
                            LatLng hostelLocation = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions()
                                    .position(hostelLocation)
                                    .title(name));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing hostel data: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase database error: " + databaseError.getMessage());
            }
        });
    }
}

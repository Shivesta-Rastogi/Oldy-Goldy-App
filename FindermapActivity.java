package com.example.myfinalloginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FindermapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "FindermapActivity";
    private GoogleMap mMap;
    private TextView distanceText;
    private Button findPathButton;
    private Spinner hostelSpinner;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Firebase references
    private FirebaseDatabase database;
    private DatabaseReference hostelsRef;
    private DatabaseReference userLocationRef;

    // Location client
    private FusedLocationProviderClient fusedLocationClient;

    // Current user location
    private LatLng currentUserLocation;

    // Storage for hostel data
    private Map<String, LatLng> hostelCoordinates = new HashMap<>();
    private List<String> hostelNames = new ArrayList<>();
    private String selectedHostel;

    // Default location if user location unavailable
    private static final LatLng BANASTHALI_MAIN_GATE = new LatLng(26.4015, 75.8728);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_findermap);

            Log.d(TAG, "onCreate: Initializing app");

            // Initialize Firebase
            initFirebase();

            // Initialize location services
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            // Initialize UI elements
            distanceText = findViewById(R.id.distance_text);
            findPathButton = findViewById(R.id.find_path_button);
            hostelSpinner = findViewById(R.id.hostel_spinner);

            // Set up spinner adapter
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, hostelNames);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            hostelSpinner.setAdapter(adapter);

            // Hostel selection listener
            hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedHostel = hostelNames.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });

            // Initialize map fragment
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            } else {
                Log.e(TAG, "Map fragment is null");
                Toast.makeText(this, "Error initializing map", Toast.LENGTH_SHORT).show();
            }

            // Set button click listener
            findPathButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRouteToSelectedHostel();
                }
            });

            // Load hostel data from Firebase
            loadHostelData();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initFirebase() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();

        // Get reference to hostels data
        hostelsRef = database.getReference("hostels");

        // Get reference to user location data
        // You can modify this path based on your database structure
        userLocationRef = database.getReference("users").child("current_user").child("location");
    }

    private void loadHostelData() {
        hostelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    // Clear existing data
                    hostelCoordinates.clear();
                    hostelNames.clear();

                    // Iterate through all hostels
                    for (DataSnapshot hostelSnapshot : dataSnapshot.getChildren()) {
                        String name = hostelSnapshot.child("name").getValue(String.class);
                        double latitude = hostelSnapshot.child("latitude").getValue(Double.class);
                        double longitude = hostelSnapshot.child("longitude").getValue(Double.class);

                        if (name != null) {
                            hostelNames.add(name);
                            hostelCoordinates.put(name, new LatLng(latitude, longitude));
                        }
                    }

                    // Update the spinner
                    ((ArrayAdapter)hostelSpinner.getAdapter()).notifyDataSetChanged();

                    // Select first hostel by default if available
                    if (!hostelNames.isEmpty()) {
                        selectedHostel = hostelNames.get(0);
                        hostelSpinner.setSelection(0);
                    }

                    Log.d(TAG, "Loaded " + hostelNames.size() + " hostels from Firebase");

                    // Update markers on map if it's ready
                    if (mMap != null) {
                        updateHostelMarkers();
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Error loading hostel data", e);
                    Toast.makeText(FindermapActivity.this,
                            "Error loading hostel data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Firebase database error: " + databaseError.getMessage());
                Toast.makeText(FindermapActivity.this,
                        "Database error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateHostelMarkers() {
        // Clear previous markers
        mMap.clear();

        // Add marker for current location if available
        if (currentUserLocation != null) {
            mMap.addMarker(new MarkerOptions()
                    .position(currentUserLocation)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        // Add markers for all hostels
        for (Map.Entry<String, LatLng> entry : hostelCoordinates.entrySet()) {
            mMap.addMarker(new MarkerOptions()
                    .position(entry.getValue())
                    .title(entry.getKey())
                    .snippet("Hostel")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            Log.d(TAG, "onMapReady: Map is ready");
            mMap = googleMap;

            // Enable my location button if permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                getUserLocation();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

            // Set default location to Banasthali Vidyapith
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BANASTHALI_MAIN_GATE, 0));

            // Update hostel markers if data already loaded
            updateHostelMarkers();

            // Listen for user location updates from Firebase
            listenForUserLocationUpdates();

        } catch (Exception e) {
            Log.e(TAG, "Error in onMapReady", e);
            Toast.makeText(this, "Error setting up map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentUserLocation = new LatLng(
                                    location.getLatitude(),
                                    location.getLongitude());

                            // Move camera to user location
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    currentUserLocation, 16));

                            // Update markers
                            updateHostelMarkers();

                            // Save to Firebase (optional)
                            saveUserLocationToFirebase();
                        } else {
                            Toast.makeText(FindermapActivity.this,
                                    "Unable to get current location",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserLocationToFirebase() {
        if (currentUserLocation != null) {
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("latitude", currentUserLocation.latitude);
            locationData.put("longitude", currentUserLocation.longitude);
            locationData.put("timestamp", System.currentTimeMillis());

            userLocationRef.setValue(locationData);
        }
    }

    private void listenForUserLocationUpdates() {
        // This is useful if you want to get updates from other sources
        // For example, if you have a service updating user location
        userLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.exists()) {
                        Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                        if (latitude != null && longitude != null) {
                            currentUserLocation = new LatLng(latitude, longitude);
                            updateHostelMarkers();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading user location from Firebase", e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "User location listener cancelled: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    if (mMap != null) {
                        mMap.setMyLocationEnabled(true);
                        getUserLocation();
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "Error enabling location", e);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to show the route between user location and selected hostel
    private void showRouteToSelectedHostel() {
        try {
            if (selectedHostel == null || !hostelCoordinates.containsKey(selectedHostel)) {
                Toast.makeText(this, "Please select a valid hostel", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng destination = hostelCoordinates.get(selectedHostel);
            LatLng origin = currentUserLocation;

            // If user location is not available, use main gate as default
            if (origin == null) {
                origin = BANASTHALI_MAIN_GATE;
                Toast.makeText(this, "Using default location as starting point",
                        Toast.LENGTH_SHORT).show();
            }

            // Clear previous markers and add new ones
            mMap.clear();

            // Add origin marker
            mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .title("Your Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

            // Add destination marker
            mMap.addMarker(new MarkerOptions()
                    .position(destination)
                    .title(selectedHostel)
                    .snippet("Hostel"));

            // Request directions between points
            requestDirections(origin, destination);

            // Move camera to show origin
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 16));

            Toast.makeText(this, "Calculating route to " + selectedHostel + "...",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing route", e);
            Toast.makeText(this, "Error showing route: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void requestDirections(LatLng origin, LatLng destination) {
        try {
            // Create Geo API context with API key
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .readTimeout(2, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.SECONDS)
                    .build();

            // Create directions request
            DirectionsApiRequest directionsRequest = DirectionsApi.getDirections(geoApiContext,
                            origin.latitude + "," + origin.longitude,
                            destination.latitude + "," + destination.longitude)
                    .mode(TravelMode.WALKING);

            // Execute request asynchronously
            new Thread(() -> {
                try {
                    DirectionsResult directionsResult = directionsRequest.await();
                    runOnUiThread(() -> processDirectionsResult(directionsResult));
                } catch (Exception e) {
                    Log.e(TAG, "Error fetching directions", e);
                    runOnUiThread(() -> {
                        Toast.makeText(FindermapActivity.this,
                                "Error fetching directions. Creating direct line instead.",
                                Toast.LENGTH_SHORT).show();

                        // If directions API fails, draw a direct line
                        createDirectLine(origin, destination);
                    });
                }
            }).start();
        } catch (Exception e) {
            Log.e(TAG, "Error requesting directions", e);
            Toast.makeText(this, "Error requesting directions: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();

            // Fallback to direct line
            createDirectLine(origin, destination);
        }
    }

    private void processDirectionsResult(DirectionsResult directionsResult) {
        try {
            if (directionsResult.routes.length == 0) {
                Toast.makeText(this, "No routes found, creating direct line",
                        Toast.LENGTH_SHORT).show();
                createDirectLine(currentUserLocation != null ?
                                currentUserLocation : BANASTHALI_MAIN_GATE,
                        hostelCoordinates.get(selectedHostel));
                return;
            }

            // Get the first route
            DirectionsRoute route = directionsResult.routes[0];

            // Extract the path
            List<LatLng> path = new ArrayList<>();
            for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
                path.add(new LatLng(point.lat, point.lng));
            }

            // Draw the polyline
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(path)
                    .width(12)
                    .color(Color.BLUE)
                    .geodesic(true);

            mMap.addPolyline(polylineOptions);

            // Display distance
            String distance = route.legs[0].distance.humanReadable;
            distanceText.setText("Distance to " + selectedHostel + ": " + distance);
        } catch (Exception e) {
            Log.e(TAG, "Error processing directions result", e);
            Toast.makeText(this, "Error processing directions", Toast.LENGTH_SHORT).show();

            // Fallback to direct line
            createDirectLine(currentUserLocation != null ?
                            currentUserLocation : BANASTHALI_MAIN_GATE,
                    hostelCoordinates.get(selectedHostel));
        }
    }

    // Fallback method to create a direct line between points if the Directions API fails
    private void createDirectLine(LatLng origin, LatLng destination) {
        try {
            // Draw a direct line
            PolylineOptions directLine = new PolylineOptions()
                    .add(origin)
                    .add(destination)
                    .width(12)
                    .color(Color.RED)
                    .geodesic(true);

            mMap.addPolyline(directLine);

            // Calculate straight-line distance in meters
            float[] results = new float[1];
            android.location.Location.distanceBetween(
                    origin.latitude, origin.longitude,
                    destination.latitude, destination.longitude,
                    results);

            float distanceInMeters = results[0];
            String distanceText = distanceInMeters < 1000 ?
                    String.format("%.0f meters", distanceInMeters) :
                    String.format("%.2f km", distanceInMeters/1000);

            this.distanceText.setText("Distance to " + selectedHostel + ": " +
                    distanceText + " (straight line)");
        } catch (Exception e) {
            Log.e(TAG, "Error creating direct line", e);
            Toast.makeText(this, "Error creating route visualization", Toast.LENGTH_SHORT).show();
        }
    }
}

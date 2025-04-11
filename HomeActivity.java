
package com.example.myfinalloginpage;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.core.app.NotificationCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView adRecyclerView;
    private AdAdapter adAdapter;
    private List<AdModel> adList;
    private FirebaseFirestore db;

    private ImageView trunkImage, fanImage, cycleImage, bookImage;
    private TextView trunkCaption, fanCaption, cycleCaption, bookCaption;
    private TextView trunkDescription, fanDescription, cycleDescription, bookDescription;
    private TextView trunkLocation, fanLocation, cycleLocation, bookLocation;
    private static final String CHANNEL_ID = "welcome_channel";

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        createNotificationChannel();
        showWelcomeNotification();

        //ads
        adRecyclerView = findViewById(R.id.ad_recycler_view);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        db = FirebaseFirestore.getInstance();
        adList = new ArrayList<>();
        adAdapter = new AdAdapter(this, adList);
        adRecyclerView.setAdapter(adAdapter);

        fetchAdvertisements();


        ImageView btnPost = findViewById(R.id.imageView11);
        btnPost.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, PostActivity.class);
            startActivity(intent);
        });

        ImageView imageView3 = findViewById(R.id.imageView10);
        imageView3.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        ImageView imageView27 = findViewById(R.id.imageView27);
        imageView27.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView imageView7 = findViewById(R.id.imageView7);
        imageView7.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        ImageView imageView8 = findViewById(R.id.imageView8);
        imageView8.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
            startActivity(intent);
        });

        View bottomNavBar = findViewById(R.id.bottomNavBar);
        if (bottomNavBar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bottomNavBar, (v, insets) -> {
                int bottomInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bottomInset);
                return insets;
            });
        }

        initializeProductViews();
        loadLatestProducts();

        // Description - Trunk
        LinearLayout linearLayoutTrunk = findViewById(R.id.linearLayoutTrunk);
        if (linearLayoutTrunk != null) {
            linearLayoutTrunk.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                intent.putExtra("category", "trunk");
                startActivity(intent);
            });
        }

        //  Description - Fan
        LinearLayout linearLayoutFan = findViewById(R.id.linearLayoutFan);
        if (linearLayoutFan != null) {
            linearLayoutFan.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                intent.putExtra("category", "fan");  // Standardized lowercase "fan"
                startActivity(intent);
            });
        }

        // Description - Cycle
        LinearLayout linearLayoutCycle = findViewById(R.id.linearLayoutCycle);
        if (linearLayoutCycle != null) {
            linearLayoutCycle.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                intent.putExtra("category", "cycle");
                startActivity(intent);
            });
        }
        // Description - Books
        LinearLayout linearLayoutBooks = findViewById(R.id.linearLayoutBooks);
        if (linearLayoutBooks != null) {
            linearLayoutBooks.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, ProductActivity.class);
                intent.putExtra("category", "books");
                startActivity(intent);
            });
        }

        Button fanButton = findViewById(R.id.fanButton);
        if (fanButton != null) {
            fanButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, fan.class);
                intent.putExtra("category", "fan");
                startActivity(intent);
            });
        }

        // Categories - Trunk Button
        Button TrunkButton = findViewById(R.id.TrunkButton);
        if (TrunkButton != null) {
            TrunkButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, trunk.class);
                intent.putExtra("category", "Trunk");
                startActivity(intent);
            });
        }

        // Categories - Cycle Button
        Button CycleButton = findViewById(R.id.CycleButton);
        if (CycleButton != null) {
            CycleButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, Cycle.class);
                intent.putExtra("category", "Cycle");
                startActivity(intent);
            });
        }
    }

    private void initializeProductViews() {
        trunkImage = findViewById(R.id.trunkImage);
        fanImage = findViewById(R.id.fanImage);
        cycleImage = findViewById(R.id.cycleImage);
        bookImage = findViewById(R.id.bookImage);

        trunkCaption = findViewById(R.id.trunkCaption);
        fanCaption = findViewById(R.id.fanCaption);
        cycleCaption = findViewById(R.id.cycleCaption);
        bookCaption = findViewById(R.id.bookCaption);

        trunkDescription = findViewById(R.id.trunkDescription);
        fanDescription = findViewById(R.id.fanDescription);
        cycleDescription = findViewById(R.id.cycleDescription);
        bookDescription = findViewById(R.id.bookDescription);

        trunkLocation = findViewById(R.id.trunkLocation);
        fanLocation = findViewById(R.id.fanLocation);
        cycleLocation = findViewById(R.id.cycleLocation);
        bookLocation = findViewById(R.id.bookLocation);
    }

    private void loadLatestProducts() {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("products");
        productsRef.orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                Collections.sort(productList, (p1, p2) -> Long.compare(p2.getTimestamp(), p1.getTimestamp()));
                updateProductViews(productList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProductViews(List<Product> products) {
        for (Product product : products) {
            switch (product.getItem().toLowerCase()) {
                case "trunk":
                    updateProductUI(trunkImage, trunkCaption, trunkDescription, trunkLocation, product);
                    break;
                case "fan":
                    updateProductUI(fanImage, fanCaption, fanDescription, fanLocation, product);
                    break;
                case "cycle":
                    updateProductUI(cycleImage, cycleCaption, cycleDescription, cycleLocation, product);
                    break;
                case "book":
                    updateProductUI(bookImage, bookCaption, bookDescription, bookLocation, product);
                    break;
            }
        }
    }

    private void updateProductUI(ImageView imageView, TextView caption, TextView description, TextView location, Product product) {
        Glide.with(this).load(product.getImageUrl()).into(imageView);
        caption.setText(product.getCaption());
        description.setText(product.getDescription());
        location.setText(product.getLocation());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Welcome Channel";
            String description = "Channel for welcome notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showWelcomeNotification() {
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isNotificationShown = prefs.getBoolean("notification_shown", false);

        if (!isNotificationShown) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("from_notification", true);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Welcome!")
                    .setContentText("Explore amazing deals now.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notification_shown", true);
            editor.apply();
        }
    }
    //ads
    private void fetchAdvertisements() {
        db.collection("Advertisements")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        adList.clear();
                        QuerySnapshot documents = task.getResult();
                        if (documents != null && !documents.isEmpty()) {
                            for (DocumentSnapshot doc : documents) {
                                AdModel ad = doc.toObject(AdModel.class);
                                adList.add(ad);
                            }
                            // Shuffle the ads to display in random order
                            Collections.shuffle(adList);
                            adAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(this, "No ads!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

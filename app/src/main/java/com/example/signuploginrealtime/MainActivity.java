
package com.example.signuploginrealtime;

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

public class MainActivity extends AppCompatActivity {

    private RecyclerView adRecyclerView;

    private static final String CHANNEL_ID = "welcome_channel";

    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        createNotificationChannel();
        showWelcomeNotification();

        //ads
        adRecyclerView = findViewById(R.id.ad_recycler_view);
        adRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ImageView btnPost = findViewById(R.id.imageView11);
        btnPost.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        });

        ImageView imageView3 = findViewById(R.id.imageView10);
        imageView3.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        ImageView imageView27 = findViewById(R.id.imageView27);
        imageView27.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        ImageView imageView7 = findViewById(R.id.imageView7);
        imageView7.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ImageView imageView8 = findViewById(R.id.imageView8);
        imageView8.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PostActivity.class);
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

        // Description - Trunk
        LinearLayout linearLayoutTrunk = findViewById(R.id.linearLayoutTrunk);
        if (linearLayoutTrunk != null) {
            linearLayoutTrunk.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("category", "trunk");
                startActivity(intent);
            });
        }

        //  Description - Fan
        LinearLayout linearLayoutFan = findViewById(R.id.linearLayoutFan);
        if (linearLayoutFan != null) {
            linearLayoutFan.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("category", "fan");  // Standardized lowercase "fan"
                startActivity(intent);
            });
        }

        // Description - Cycle
        LinearLayout linearLayoutCycle = findViewById(R.id.linearLayoutCycle);
        if (linearLayoutCycle != null) {
            linearLayoutCycle.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("category", "cycle");
                startActivity(intent);
            });
        }
        // Description - Books
        LinearLayout linearLayoutBooks = findViewById(R.id.linearLayoutBooks);
        if (linearLayoutBooks != null) {
            linearLayoutBooks.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                intent.putExtra("category", "books");
                startActivity(intent);
            });
        }

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
        String channelId = "default_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8+ create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your own icon
                .setContentTitle("Welcome!")
                .setContentText("Thanks for opening the app.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }



}
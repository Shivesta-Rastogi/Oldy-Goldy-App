package com.example.signuploginrealtime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {

    Button btnSignOut;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        btnSignOut = findViewById(R.id.btnSignOut);

        btnSignOut.setOnClickListener(view -> {
            mAuth.signOut(); // Sign out user
            Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close HomeActivity
        });

        RelativeLayout accountLayout = findViewById(R.id.Account);

        // Set OnClickListener
        accountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to AccountActivity
                Intent intent = new Intent(SettingActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout reportLayout = findViewById(R.id.Report);

        // Set OnClickListener for Report
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to ReportActivity
                Intent intent = new Intent(SettingActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout passwordLayout = findViewById(R.id.password);

        // Set OnClickListener for password
        passwordLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to passwordActivity
                Intent intent = new Intent(SettingActivity.this, passwordActivity.class);
                startActivity(intent);
            }
        });

        RelativeLayout help_supportLayout = findViewById(R.id.help_support);

        // Set OnClickListener for help
        help_supportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to helpActivity
                Intent intent = new Intent(SettingActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

    }

}
package com.example.myfinalloginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView btn;
    EditText inputUsername, inputEmail, inputPassword, inputConfirmPassword;
    Button btnSignup;

    FirebaseAuth mAuth; // Firebase Authentication
    FirebaseDatabase database;

    ProgressBar progressBar;

    DatabaseReference usersRef;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the title
        getSupportActionBar().hide(); // Hide the Action Bar

        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        btn = findViewById(R.id.alreadyHaveAccount);
        inputUsername = findViewById(R.id.inputUsername);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignup = findViewById(R.id.btnSignup);
        progressBar = findViewById(R.id.progressBar);

        btnSignup.setOnClickListener(view -> checkCredentials());

        btn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class); // Redirect to LoginActivity
            startActivity(intent);
            finish();
        }
    }

    private void checkCredentials() {
        progressBar.setVisibility(View.VISIBLE);

        String username = inputUsername.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || username.length() < 8) {
            showError(inputUsername, "Your username must be at least 8 characters long!");
            progressBar.setVisibility(View.GONE);
        } else if (!email.contains("@")) {
            showError(inputEmail, "Email is not valid!");
            progressBar.setVisibility(View.GONE);
        } else if (password.isEmpty() || password.length() < 8) {
            showError(inputPassword, "Password must be at least 8 characters long!");
            progressBar.setVisibility(View.GONE);
        } else if (confirmPassword.isEmpty() || !confirmPassword.equals(password)) {
            showError(inputConfirmPassword, "Passwords do not match!");
            progressBar.setVisibility(View.GONE);
        } else {
            registerUser(username, email, password);
        }
    }



    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
    private void registerUser(String username, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Get User ID GET PASSO
                        FirebaseUser user = mAuth.getCurrentUser();
                        String userId = user.getUid();

                        // Store User Data in Database
                        User newUser = new User(username, email);
                        usersRef.child(userId).setValue(newUser).addOnCompleteListener(dbTask -> {
                            if (dbTask.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Database Error: " + dbTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    public class User {
        public String username, email;

        public User() {} // Default constructor for Firebase

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }

}



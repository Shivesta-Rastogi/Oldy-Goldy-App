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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword;
    Button btnLogin;
    TextView btnSignup;
    ProgressBar progressBar;  // ✅ Declare progressBar here
    FirebaseAuth mAuth;


    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance(); // Ensure it's initialized
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // Hide the Action Bar
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.textViewSignUp);
        progressBar = findViewById(R.id.progressBar);  // ✅ Initialize progressBar

        btnLogin.setOnClickListener(view -> loginUser());

        btnSignup.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!email.contains("@")) {
            showError(inputEmail, "Enter a valid email!");
        } else if (password.isEmpty() || password.length() < 8) {
            showError(inputPassword, "Password must be at least 8 characters long!");
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void showError(EditText input, String message) {
        input.setError(message);
        input.requestFocus();
    }
}

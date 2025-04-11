package com.example.myfinalloginpage;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {
    private EditText reporterId, reportedId, reason;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Reports");

        // Initialize UI components
        reporterId = findViewById(R.id.reporterId);
        reportedId = findViewById(R.id.reportedId);
        reason = findViewById(R.id.reason);
        Button btnReport = findViewById(R.id.btnReport);

        // Set click listener for the Report button
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void submitReport() {
        String reporter = reporterId.getText().toString().trim();
        String reported = reportedId.getText().toString().trim();
        String reportReason = reason.getText().toString().trim();

        if (TextUtils.isEmpty(reporter) || TextUtils.isEmpty(reported) || TextUtils.isEmpty(reportReason)) {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique report ID based on timestamp
        String reportId = databaseReference.push().getKey();

        // Get current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create report object
        Report report = new Report(reportId, reporter, reported, reportReason, timestamp);

        // Save data to Firebase
        assert reportId != null;
        databaseReference.child(reportId).setValue(report)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ReportActivity.this, "Report submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after submission
                })
                .addOnFailureListener(e -> Toast.makeText(ReportActivity.this, "Failed to submit report!", Toast.LENGTH_SHORT).show());
    }

    // Report Model Class
    public static class Report {
        public String reportId, reporterId, reportedId, reason, timestamp;

        public Report() { }

        public Report(String reportId, String reporterId, String reportedId, String reason, String timestamp) {
            this.reportId = reportId;
            this.reporterId = reporterId;
            this.reportedId = reportedId;
            this.reason = reason;
            this.timestamp = timestamp;
        }
    }
}

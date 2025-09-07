package com.example.barcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DashboardActivity extends AppCompatActivity {

    TextView tvWelcome, tvTotalValue, tvTotalCoins, tv1PesoCount, tv5PesoCount, tv10PesoCount, tv20PesoCount;
    Button btnStartCounting, btnSyncData, btnLogout;
    Map<Integer, Integer> coinCounts = new HashMap<>();
    int totalCoinCount = 0;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sharedPreferences = getSharedPreferences("UserCredentials", MODE_PRIVATE);

        // --- Get username from the session ---
        String username = sharedPreferences.getString("loggedInUser", null);
        String role = (username != null) ? sharedPreferences.getString(username + "_role", null) : null;

        // --- Access Control ---
        if (username == null || !"Staff".equals(role)) {
            Toast.makeText(this, "Access Denied. Please log in as Staff.", Toast.LENGTH_LONG).show();
            logout();
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Staff Dashboard");
        }

        // Initialize UI...
        tvWelcome = findViewById(R.id.tvWelcome);
        // ... (initialize other views)
        btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + username + "!");

        // ... (rest of your logic for counting)

        btnLogout.setOnClickListener(v -> logout());

        updateDashboard();
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedInUser");
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void simulateCoinCounting() {
        // ... (This method remains unchanged)
    }

    private void updateDashboard() {
        // ... (This method remains unchanged)
    }
}
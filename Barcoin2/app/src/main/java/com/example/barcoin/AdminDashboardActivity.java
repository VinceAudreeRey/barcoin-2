package com.example.barcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AdminDashboardActivity extends AppCompatActivity {

    // User Management
    TextView tvUserCount;
    LinearLayout userContainer;
    SharedPreferences sharedPreferences;

    // Coin Counting Dashboard
    TextView tvWelcome, tvTotalValue, tvTotalCoins, tv1PesoCount, tv5PesoCount, tv10PesoCount, tv20PesoCount;
    Button btnStartCounting, btnSyncData, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        sharedPreferences = getSharedPreferences("UserCredentials", MODE_PRIVATE);

        // Get username from session (preferred way)
        String username = sharedPreferences.getString("loggedInUser", null);
        String role = (username != null) ? sharedPreferences.getString(username + "_role", null) : null;

        // Access Control: only Owner allowed
        if (username == null || !"Owner".equals(role)) {
            Toast.makeText(this, "Access Denied. Please log in as Owner.", Toast.LENGTH_LONG).show();
            logout(); // use dedicated logout
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Owner Panel");
        }

        // Initialize UI
        tvUserCount = findViewById(R.id.tvUserCount);
        userContainer = findViewById(R.id.userContainer);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvTotalCoins = findViewById(R.id.tvTotalCoins);
        tv1PesoCount = findViewById(R.id.tv1PesoCount);
        tv5PesoCount = findViewById(R.id.tv5PesoCount);
        tv10PesoCount = findViewById(R.id.tv10PesoCount);
        tv20PesoCount = findViewById(R.id.tv20PesoCount);
        btnStartCounting = findViewById(R.id.btnStartCounting);
        btnSyncData = findViewById(R.id.btnSyncData);
        btnLogout = findViewById(R.id.btnLogout);

        tvWelcome.setText("Welcome, " + username + "!");

        // Load users
        loadAndDisplayUsers();

        // Coin counting simulation
        btnStartCounting.setOnClickListener(v -> simulateCoinCounting());

        // Sync data (future)
        btnSyncData.setOnClickListener(v ->
                Toast.makeText(this, "Syncing data... (future feature)", Toast.LENGTH_SHORT).show()
        );

        // Logout
        btnLogout.setOnClickListener(v -> logout());
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // Remove only session, not all data
        editor.remove("loggedInUser");
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loadAndDisplayUsers() {
        userContainer.removeAllViews();

        Map<String, ?> allEntries = sharedPreferences.getAll();
        Set<String> usernames = new HashSet<>();

        for (String key : allEntries.keySet()) {
            if (!key.contains("_")) {
                usernames.add(key);
            }
        }

        tvUserCount.setText("Total Registered Users: " + usernames.size());

        for (String user : usernames) {
            String userRole = sharedPreferences.getString(user + "_role", "N/A");
            String email = sharedPreferences.getString(user + "_email", "N/A");
            String phone = sharedPreferences.getString(user + "_phone", "N/A");
            String address = sharedPreferences.getString(user + "_address", "N/A");

            // User block container
            LinearLayout userBlock = new LinearLayout(this);
            userBlock.setOrientation(LinearLayout.VERTICAL);
            userBlock.setPadding(24, 24, 24, 24);
            userBlock.setBackgroundColor(0xFFFFFFFF);

            LinearLayout.LayoutParams blockParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            blockParams.setMargins(0, 0, 0, 24);
            userBlock.setLayoutParams(blockParams);

            // User info
            TextView tvInfo = new TextView(this);
            tvInfo.setText("Username: " + user +
                    "\nRole: " + userRole +
                    "\nEmail: " + email +
                    "\nPhone: " + phone +
                    "\nAddress: " + address);
            tvInfo.setTextSize(16f);
            tvInfo.setTextColor(0xFF333333);

            // Remove button
            Button btnRemove = new Button(this);
            btnRemove.setText("Remove");
            btnRemove.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_red_dark));
            btnRemove.setTextColor(0xFFFFFFFF);

            btnRemove.setOnClickListener(v -> removeUser(user));

            // Add views to block
            userBlock.addView(tvInfo);
            userBlock.addView(btnRemove);

            // Add block to container
            userContainer.addView(userBlock);
        }
    }

    private void removeUser(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.contains(username)) {
            editor.remove(username);
            editor.remove(username + "_role");
            editor.remove(username + "_email");
            editor.remove(username + "_phone");
            editor.remove(username + "_address");
            editor.apply();

            Toast.makeText(this, "User " + username + " removed.", Toast.LENGTH_SHORT).show();
            loadAndDisplayUsers();
        } else {
            Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Coin Counting Simulation ---
    private void simulateCoinCounting() {
        Random random = new Random();

        int onePeso = random.nextInt(50);
        int fivePeso = random.nextInt(30);
        int tenPeso = random.nextInt(20);
        int twentyPeso = random.nextInt(10);

        int totalCoins = onePeso + fivePeso + tenPeso + twentyPeso;
        int totalValue = (onePeso * 1) + (fivePeso * 5) + (tenPeso * 10) + (twentyPeso * 20);

        tv1PesoCount.setText(onePeso + " pcs");
        tv5PesoCount.setText(fivePeso + " pcs");
        tv10PesoCount.setText(tenPeso + " pcs");
        tv20PesoCount.setText(twentyPeso + " pcs");

        tvTotalCoins.setText("From " + totalCoins + " coins");
        tvTotalValue.setText("₱ " + totalValue + ".00");

        Toast.makeText(this, "Coin counting simulated!", Toast.LENGTH_SHORT).show();
    }
}

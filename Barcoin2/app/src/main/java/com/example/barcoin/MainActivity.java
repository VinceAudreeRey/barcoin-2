package com.example.barcoin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button btnLogin;
    TextView tvGoRegister;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserCredentials", MODE_PRIVATE);

        // --- START: SESSION CHECK ---
        // Check if a user is already logged in before showing the login screen
        if (sharedPreferences.contains("loggedInUser")) {
            String loggedInUser = sharedPreferences.getString("loggedInUser", null);
            String role = sharedPreferences.getString(loggedInUser + "_role", "Staff"); // Default to Staff

            Intent intent;
            if ("Owner".equals(role)) {
                intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(MainActivity.this, DashboardActivity.class);
            }
            // We don't need to pass the username via intent extra anymore,
            // as dashboards will now read it from SharedPreferences.
            startActivity(intent);
            finish(); // Close MainActivity so the user can't go back to it
            return;   // Stop the rest of onCreate from running
        }
        // --- END: SESSION CHECK ---

        // If no user is logged in, proceed to show the login layout
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoRegister = findViewById(R.id.tvGoRegister);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString();

            String savedHashedPass = sharedPreferences.getString(username, null);
            String enteredHashedPass = hashPassword(password);

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else if (savedHashedPass == null) {
                Toast.makeText(this, "Username not found!", Toast.LENGTH_SHORT).show();
            } else if (savedHashedPass.equals(enteredHashedPass)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // --- START: SAVE SESSION ---
                // Save the logged-in user's username to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loggedInUser", username);
                editor.apply();
                // --- END: SAVE SESSION ---

                String role = sharedPreferences.getString(username + "_role", "Staff");

                Intent intent;
                if ("Owner".equals(role)) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, DashboardActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
            }
        });

        tvGoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
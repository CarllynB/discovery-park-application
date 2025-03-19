package com.example.discoveryparkmap;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private Button guestButton;
    private TextView registerText;
    private DatabaseHelper databaseHelper;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Initialize shared preferences for session management
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (isLoggedIn()) {
            navigateToClassesActivity();
            return;
        }

        // Initialize UI components
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        guestButton = findViewById(R.id.guestButton);
        registerText = findViewById(R.id.registerText);

        // Set up login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Set up guest button click listener
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go directly to map activity as guest
                Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapIntent);
            }
        });

        // Set up register text click listener
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to register activity
                Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    private void saveLoginSession(int userId, String username) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    private void attemptLogin() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check credentials in database
        int userId = databaseHelper.getUserIdByCredentials(username, password);

        // If user not found in database, try demo credentials
        if (userId == -1 && ((username.equals("demo") && password.equals("password")) ||
                (username.equals("admin") && password.equals("admin123")))) {
            // Create demo user in database for backward compatibility
            userId = (int) databaseHelper.addUser("Demo User", "demo@unt.edu", username, password);
        }

        if (userId != -1) {
            // Successful login
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            saveLoginSession(userId, username);
            navigateToClassesActivity();
        } else {
            // Failed login
            Toast.makeText(this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToClassesActivity() {
        Intent classesIntent = new Intent(MainActivity.this, ClassesActivity.class);
        classesIntent.putExtra("USER_ID", preferences.getInt(KEY_USER_ID, -1));
        classesIntent.putExtra("USERNAME", preferences.getString(KEY_USERNAME, ""));
        startActivity(classesIntent);
        finish(); // Close login activity
    }
}
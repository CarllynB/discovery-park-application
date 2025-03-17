package com.example.discoveryparkmap;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class MapActivity extends AppCompatActivity {

    private ImageView mapImageView;
    private Button firstFloorButton;
    private Button secondFloorButton;
    private Button backButton;
    private TextInputEditText roomSearchInput;
    private Button searchButton;
    private int currentFloor = 1; // Default to first floor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize UI components
        mapImageView = findViewById(R.id.mapImageView);
        firstFloorButton = findViewById(R.id.firstFloorButton);
        secondFloorButton = findViewById(R.id.secondFloorButton);
        roomSearchInput = findViewById(R.id.roomSearchInput);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);

        // Set initial map image
        showFloor(1);

        // Check if a room number was passed in the intent
        if (getIntent().hasExtra("ROOM_NUMBER")) {
            String roomNumber = getIntent().getStringExtra("ROOM_NUMBER");
            roomSearchInput.setText(roomNumber);
            searchRoom();
        }

        // Setup floor buttons
        firstFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloor(1);
            }
        });

        secondFloorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFloor(2);
            }
        });

        // Setup search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRoom();
            }
        });

        // Setup back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous activity
                finish();
            }
        });
    }

    private void showFloor(int floor) {
        currentFloor = floor;

        // Update button states for visual feedback
        if (floor == 1) {
            firstFloorButton.setEnabled(false);
            secondFloorButton.setEnabled(true);
            mapImageView.setImageResource(R.drawable.dp_floor1);
        } else {
            firstFloorButton.setEnabled(true);
            secondFloorButton.setEnabled(false);
            mapImageView.setImageResource(R.drawable.dp_floor2);
        }
    }

    private void searchRoom() {
        String roomNumber = roomSearchInput.getText().toString().trim();
        if (roomNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a room number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Simple demo logic - in a real app you'd have a database of rooms
        if (roomNumber.startsWith("B1") || roomNumber.startsWith("b1") ||
                roomNumber.startsWith("A1") || roomNumber.startsWith("a1") ||
                roomNumber.startsWith("C1") || roomNumber.startsWith("c1") ||
                roomNumber.startsWith("D1") || roomNumber.startsWith("d1") ||
                roomNumber.startsWith("E1") || roomNumber.startsWith("e1") ||
                roomNumber.startsWith("F1") || roomNumber.startsWith("f1") ||
                roomNumber.startsWith("G1") || roomNumber.startsWith("g1") ||
                roomNumber.startsWith("J1") || roomNumber.startsWith("j1") ||
                roomNumber.startsWith("K1") || roomNumber.startsWith("k1")) {
            showFloor(1);
            Toast.makeText(this, "Room " + roomNumber + " is on the first floor", Toast.LENGTH_SHORT).show();
            // Here you would also highlight the room location
        } else if (roomNumber.startsWith("B2") || roomNumber.startsWith("b2") ||
                roomNumber.startsWith("A2") || roomNumber.startsWith("a2") ||
                roomNumber.startsWith("C2") || roomNumber.startsWith("c2") ||
                roomNumber.startsWith("D2") || roomNumber.startsWith("d2") ||
                roomNumber.startsWith("E2") || roomNumber.startsWith("e2") ||
                roomNumber.startsWith("F2") || roomNumber.startsWith("f2") ||
                roomNumber.startsWith("K2") || roomNumber.startsWith("k2") ||
                roomNumber.startsWith("M2") || roomNumber.startsWith("m2")) {
            showFloor(2);
            Toast.makeText(this, "Room " + roomNumber + " is on the second floor", Toast.LENGTH_SHORT).show();
            // Here you would also highlight the room location
        } else {
            Toast.makeText(this, "Room " + roomNumber + " not found", Toast.LENGTH_SHORT).show();
        }
    }
}
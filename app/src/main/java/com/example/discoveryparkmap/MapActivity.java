package com.example.discoveryparkmap;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.app.AlertDialog;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private ImageView mapImageView;
    private Button firstFloorButton;
    private Button secondFloorButton;
    private Button backButton;
    private TextInputEditText roomSearchInput;
    private Button searchButton;
    private Spinner startLocationSpinner;

    private Landmark selectedLandmark;
    private RouteManager routeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapImageView = findViewById(R.id.mapImageView);
        firstFloorButton = findViewById(R.id.firstFloorButton);
        secondFloorButton = findViewById(R.id.secondFloorButton);
        backButton = findViewById(R.id.backButton);
        roomSearchInput = findViewById(R.id.roomSearchInput);
        searchButton = findViewById(R.id.searchButton);
        startLocationSpinner = findViewById(R.id.startLocationSpinner);

        routeManager = new RouteManager();
        showFloor(1);

        List<Landmark> landmarkList = routeManager.getLandmarks();
        List<String> landmarkNames = new ArrayList<>();
        for (Landmark lm : landmarkList) {
            landmarkNames.add(lm.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, landmarkNames);
        startLocationSpinner.setAdapter(adapter);

        startLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLandmark = landmarkList.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedLandmark = null;
            }
        });

        firstFloorButton.setOnClickListener(v -> showFloor(1));
        secondFloorButton.setOnClickListener(v -> showFloor(2));
        backButton.setOnClickListener(v -> finish());
        searchButton.setOnClickListener(v -> searchRoom());

        if (getIntent().hasExtra("ROOM_NUMBER")) {
            String roomNumber = getIntent().getStringExtra("ROOM_NUMBER");
            roomSearchInput.setText(roomNumber);
            searchRoom();
        }
    }

    private void showFloor(int floor) {
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
        String roomNumber = roomSearchInput.getText().toString().trim().toLowerCase();
        if (roomNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a room number", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int roomDigits = Integer.parseInt(roomNumber.replaceAll("[^0-9]", ""));

            // Load .jpg image manually by URI
            Uri imageUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + roomNumber);
            mapImageView.setImageURI(imageUri);

            if (roomDigits >= 100 && roomDigits < 200) {
                showFloor(1);
            } else if (roomDigits >= 200 && roomDigits < 300) {
                showFloor(2);
            }

            if (selectedLandmark != null) {
                Toast.makeText(this, "Starting from: " + selectedLandmark.getName(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Room not found or invalid format", Toast.LENGTH_SHORT).show();
        }
    }
}

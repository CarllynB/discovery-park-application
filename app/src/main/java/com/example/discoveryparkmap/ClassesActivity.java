package com.example.discoveryparkmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ClassesActivity extends AppCompatActivity {
    private RecyclerView classesRecyclerView;
    private Button addClassButton;
    private Button backButton;
    private Button logoutButton;
    private ArrayList<ClassInfo> classList;
    private ClassAdapter adapter;
    private DatabaseHelper databaseHelper;
    private int userId;
    private String username;
    private static final String PREF_NAME = "UserSession";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        // Initialize database helper
        databaseHelper = DatabaseHelper.getInstance(this);

        // Initialize UI components
        classesRecyclerView = findViewById(R.id.classesRecyclerView);
        addClassButton = findViewById(R.id.addClassButton);
        backButton = findViewById(R.id.backButton);
        logoutButton = findViewById(R.id.logoutButton); // Make sure this exists in your layout

        // Get user info from intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        username = getIntent().getStringExtra("USERNAME");

        // Set the title with username
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(username + "'s Classes");

        // Load classes from database
        loadClassesFromDatabase();

        // Set up RecyclerView
        adapter = new ClassAdapter(classList);
        classesRecyclerView.setAdapter(adapter);
        classesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add class button
        addClassButton.setOnClickListener(v -> {
            showAddClassDialog();
        });

        // Set up back button - navigate to map instead of finishing
        backButton.setOnClickListener(v -> {
            // Navigate to map activity
            Intent mapIntent = new Intent(ClassesActivity.this, MapActivity.class);
            startActivity(mapIntent);
        });

        // Set up logout button
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                logout();
            });
        }
    }

    private void loadClassesFromDatabase() {
        // If logged in, get classes from database
        if (userId != -1) {
            classList = new ArrayList<>(databaseHelper.getClassesForUser(userId));

            // If no classes found, maybe it's an existing user from before database implementation
            if (classList.isEmpty()) {
                loadDemoClasses(); // Load demo classes as fallback
            }
        } else {
            // Fallback to demo classes if not logged in or ID not found
            loadDemoClasses();
        }
    }

    private void loadDemoClasses() {
        classList = new ArrayList<>();

        // Create room objects
        Room b155Room = new Room("B155", 1, "B", 0, 0, true);
        Room f231Room = new Room("F231", 2, "F", 0, 0, true);
        Room e130Room = new Room("E130", 1, "E", 0, 0, true);

        // Add example classes - only CSCE and engineering classes
        classList.add(new ClassInfo("CSCE 3444", "Software Engineering", b155Room, "MW", "10:00 AM", "11:20 AM"));
        classList.add(new ClassInfo("CSCE 4115", "Formal Languages", f231Room, "TR", "1:00 PM", "2:20 PM"));
        classList.add(new ClassInfo("CSCE 3612", "Computer Organization", e130Room, "MWF", "9:00 AM", "9:50 AM"));
    }

    private void showAddClassDialog() {
        // Create a dialog with a form for adding a class
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Class");

        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        builder.setView(dialogView);

        // Get references to the input fields
        final EditText classCodeInput = dialogView.findViewById(R.id.classCodeInput);
        final EditText classNameInput = dialogView.findViewById(R.id.classNameInput);
        final EditText roomNumberInput = dialogView.findViewById(R.id.roomNumberInput);
        final EditText daysInput = dialogView.findViewById(R.id.daysInput);
        final EditText startTimeInput = dialogView.findViewById(R.id.startTimeInput);
        final EditText endTimeInput = dialogView.findViewById(R.id.endTimeInput);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get input values
                String classCode = classCodeInput.getText().toString().trim();
                String className = classNameInput.getText().toString().trim();
                String roomNumber = roomNumberInput.getText().toString().trim();
                String days = daysInput.getText().toString().trim();
                String startTime = startTimeInput.getText().toString().trim();
                String endTime = endTimeInput.getText().toString().trim();

                // Validate inputs
                if (classCode.isEmpty() || className.isEmpty() || roomNumber.isEmpty() ||
                        days.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(ClassesActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Determine floor based on room number
                int floor = 1;
                if (roomNumber.startsWith("B2") || roomNumber.startsWith("A2") ||
                        roomNumber.startsWith("C2") || roomNumber.startsWith("D2") ||
                        roomNumber.startsWith("E2") || roomNumber.startsWith("F2") ||
                        roomNumber.startsWith("K2") || roomNumber.startsWith("M2")) {
                    floor = 2;
                }

                // Create room and class
                Room room = new Room(roomNumber, floor, roomNumber.substring(0, 1), 0, 0, true);
                ClassInfo newClass = new ClassInfo(classCode, className, room, days, startTime, endTime);

                // Save to database if logged in
                if (userId != -1) {
                    long classId = databaseHelper.addClass(userId, newClass);
                    if (classId != -1) {
                        // Add to the list and update UI
                        classList.add(newClass);
                        adapter.notifyItemInserted(classList.size() - 1);
                        Toast.makeText(ClassesActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ClassesActivity.this, "Error adding class to database", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Just add to list for guest users (won't be saved)
                    classList.add(newClass);
                    adapter.notifyItemInserted(classList.size() - 1);
                    Toast.makeText(ClassesActivity.this, "Class added (not saved - guest mode)", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Add logout method
    private void logout() {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear session
                        SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();

                        // Return to login
                        Intent loginIntent = new Intent(ClassesActivity.this, MainActivity.class);
                        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(loginIntent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Class adapter for RecyclerView
    private class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
        private List<ClassInfo> classes;

        public ClassAdapter(List<ClassInfo> classes) {
            this.classes = classes;
        }

        @Override
        public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_class, parent, false);
            return new ClassViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ClassViewHolder holder, int position) {
            ClassInfo classInfo = classes.get(position);
            holder.classCodeText.setText(classInfo.getClassCode());
            holder.classNameText.setText(classInfo.getClassName());
            holder.roomText.setText("Room: " + classInfo.getRoom().getRoomNumber());
            holder.timeText.setText(classInfo.getDays() + " " +
                    classInfo.getStartTime() + " - " +
                    classInfo.getEndTime());

            holder.itemView.setOnClickListener(v -> {
                // Navigate to map showing this room
                Intent mapIntent = new Intent(ClassesActivity.this, MapActivity.class);
                mapIntent.putExtra("ROOM_NUMBER", classInfo.getRoom().getRoomNumber());
                startActivity(mapIntent);
            });
        }

        @Override
        public int getItemCount() {
            return classes.size();
        }

        class ClassViewHolder extends RecyclerView.ViewHolder {
            TextView classCodeText, classNameText, roomText, timeText;

            public ClassViewHolder(View itemView) {
                super(itemView);
                classCodeText = itemView.findViewById(R.id.classCodeText);
                classNameText = itemView.findViewById(R.id.classNameText);
                roomText = itemView.findViewById(R.id.roomText);
                timeText = itemView.findViewById(R.id.timeText);
            }
        }
    }

    // Override back button to show logout confirmation
    @Override
    public void onBackPressed() {
        // Navigate to map instead of back
        Intent mapIntent = new Intent(ClassesActivity.this, MapActivity.class);
        startActivity(mapIntent);
    }
}
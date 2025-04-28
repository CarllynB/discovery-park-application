package com.example.discoveryparkmap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ClassesActivity extends AppCompatActivity {

    private RecyclerView classesRecyclerView;
    private Button addClassButton, backButton, logoutButton;
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

        databaseHelper = DatabaseHelper.getInstance(this);

        classesRecyclerView = findViewById(R.id.classesRecyclerView);
        addClassButton = findViewById(R.id.addClassButton);
        backButton = findViewById(R.id.backButton);
        logoutButton = findViewById(R.id.logoutButton);

        userId = getIntent().getIntExtra("USER_ID", -1);
        username = getIntent().getStringExtra("USERNAME");

        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(username + "'s Classes");

        loadClassesFromDatabase();

        adapter = new ClassAdapter(classList);
        classesRecyclerView.setAdapter(adapter);
        classesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        addClassButton.setOnClickListener(v -> showAddClassDialog());
        backButton.setOnClickListener(v -> startActivity(new Intent(ClassesActivity.this, MapActivity.class)));

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logout());
        }
    }

    private void loadClassesFromDatabase() {
        if (userId != -1) {
            classList = new ArrayList<>(databaseHelper.getClassesForUser(userId));
        } else {
            loadDemoClasses();
        }
    }

    private void loadDemoClasses() {
        classList = new ArrayList<>();
        Room room = new Room("B155", 1, "B", 0, 0, true);
        classList.add(new ClassInfo("CSCE 3444", "Software Engineering", room, "MW", "10:00 AM", "11:20 AM",
                false, "", "", "", ""));
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Class");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        builder.setView(dialogView);

        final EditText classCodeInput = dialogView.findViewById(R.id.classCodeInput);
        final EditText classNameInput = dialogView.findViewById(R.id.classNameInput);
        final EditText roomNumberInput = dialogView.findViewById(R.id.roomNumberInput);
        final EditText daysInput = dialogView.findViewById(R.id.daysInput);
        final EditText startTimeInput = dialogView.findViewById(R.id.startTimeInput);
        final EditText endTimeInput = dialogView.findViewById(R.id.endTimeInput);
        final CheckBox recitationCheckBox = dialogView.findViewById(R.id.recitationCheckBox);
        final EditText recitationRoomInput = dialogView.findViewById(R.id.recitationRoomInput);
        final EditText recitationStartTimeInput = dialogView.findViewById(R.id.recitationStartTimeInput);
        final EditText recitationEndTimeInput = dialogView.findViewById(R.id.recitationEndTimeInput);
        final EditText notesInput = dialogView.findViewById(R.id.notesInput);

        recitationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recitationRoomInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            recitationStartTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            recitationEndTimeInput.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        builder.setPositiveButton("Add", (dialog, which) -> {
            String classCode = classCodeInput.getText().toString().trim();
            String className = classNameInput.getText().toString().trim();
            String roomNumber = roomNumberInput.getText().toString().trim();
            String days = daysInput.getText().toString().trim();
            String startTime = startTimeInput.getText().toString().trim();
            String endTime = endTimeInput.getText().toString().trim();
            boolean hasRecitation = recitationCheckBox.isChecked();
            String recitationRoom = recitationRoomInput.getText().toString().trim();
            String recitationStartTime = recitationStartTimeInput.getText().toString().trim();
            String recitationEndTime = recitationEndTimeInput.getText().toString().trim();
            String notes = notesInput.getText().toString().trim();

            if (classCode.isEmpty() || className.isEmpty() || roomNumber.isEmpty() ||
                    days.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(ClassesActivity.this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int floor = (roomNumber.startsWith("B2") || roomNumber.startsWith("A2") ||
                    roomNumber.startsWith("C2") || roomNumber.startsWith("D2") ||
                    roomNumber.startsWith("E2") || roomNumber.startsWith("F2") ||
                    roomNumber.startsWith("K2") || roomNumber.startsWith("M2")) ? 2 : 1;

            Room room = new Room(roomNumber, floor, roomNumber.substring(0, 1), 0, 0, true);

            ClassInfo newClass = new ClassInfo(classCode, className, room, days, startTime, endTime,
                    hasRecitation, recitationRoom, recitationStartTime, recitationEndTime, notes);

            if (userId != -1) {
                long classId = databaseHelper.addClass(userId, newClass);
                if (classId != -1) {
                    classList.add(newClass);
                    adapter.notifyItemInserted(classList.size() - 1);
                    Toast.makeText(this, "Class added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error adding class", Toast.LENGTH_SHORT).show();
                }
            } else {
                classList.add(newClass);
                adapter.notifyItemInserted(classList.size() - 1);
                Toast.makeText(this, "Class added (guest mode)", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                    preferences.edit().clear().apply();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
        private List<ClassInfo> classes;

        public ClassAdapter(List<ClassInfo> classes) {
            this.classes = classes;
        }

        @NonNull
        @Override
        public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
            return new ClassViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
            ClassInfo classInfo = classes.get(position);

            holder.classCodeText.setText(classInfo.getClassCode());
            holder.classNameText.setText(classInfo.getClassName());
            holder.roomText.setText("Room: " + classInfo.getRoom().getRoomNumber());
            holder.timeText.setText(classInfo.getDays() + " " + classInfo.getStartTime() + " - " + classInfo.getEndTime());

            if (classInfo.hasRecitation()) {
                holder.recitationText.setVisibility(View.VISIBLE);
                holder.recitationText.setText("Recitation: " + classInfo.getRecitationRoom() + " " +
                        classInfo.getRecitationStartTime() + " - " + classInfo.getRecitationEndTime());
            } else {
                holder.recitationText.setVisibility(View.GONE);
            }

            if (!classInfo.getNotes().isEmpty()) {
                holder.notesText.setVisibility(View.VISIBLE);
                holder.notesText.setText("Notes: " + classInfo.getNotes());
            } else {
                holder.notesText.setVisibility(View.GONE);
            }

            holder.deleteButton.setOnClickListener(v -> {
                if (userId != -1) {
                    boolean success = databaseHelper.deleteClass(classInfo.getId());
                    if (success) {
                        int index = holder.getAdapterPosition();
                        classList.remove(index);
                        notifyItemRemoved(index);
                        Toast.makeText(ClassesActivity.this, "Class deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ClassesActivity.this, "Error deleting class", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    int index = holder.getAdapterPosition();
                    classList.remove(index);
                    notifyItemRemoved(index);
                    Toast.makeText(ClassesActivity.this, "Class deleted (guest mode)", Toast.LENGTH_SHORT).show();
                }
            });

            holder.itemView.setOnClickListener(v -> {
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
            TextView classCodeText, classNameText, roomText, timeText, recitationText, notesText;
            Button deleteButton;

            public ClassViewHolder(@NonNull View itemView) {
                super(itemView);
                classCodeText = itemView.findViewById(R.id.classCodeText);
                classNameText = itemView.findViewById(R.id.classNameText);
                roomText = itemView.findViewById(R.id.roomText);
                timeText = itemView.findViewById(R.id.timeText);
                recitationText = itemView.findViewById(R.id.recitationText);
                notesText = itemView.findViewById(R.id.notesText);
                deleteButton = itemView.findViewById(R.id.deleteButton);
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ClassesActivity.this, MapActivity.class));
    }
}

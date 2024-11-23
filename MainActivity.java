package com.example.fitnesslog;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText dateInput, activityInput, durationInput, notesInput;
    private Button addButton, clearButton;
    private ListView entriesListView;

    private ArrayList<String> fitnessEntries;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        dateInput = findViewById(R.id.dateInput);
        activityInput = findViewById(R.id.activityInput);
        durationInput = findViewById(R.id.durationInput);
        notesInput = findViewById(R.id.notesInput);
        addButton = findViewById(R.id.addButton);
        clearButton = findViewById(R.id.clearButton);
        entriesListView = findViewById(R.id.entriesListView);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("FitnessLog", MODE_PRIVATE);

        // Load saved entries
        loadEntries();

        // Set up ListView adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fitnessEntries);
        entriesListView.setAdapter(adapter);

        // Date Picker for Date Input
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                dateInput.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Add Button Click Listener
        addButton.setOnClickListener(v -> {
            String date = dateInput.getText().toString();
            String activity = activityInput.getText().toString();
            String duration = durationInput.getText().toString();
            String notes = notesInput.getText().toString();

            if (!date.isEmpty() && !activity.isEmpty() && !duration.isEmpty()) {
                String entry = "Date: " + date + "\nActivity: " + activity + "\nDuration: " + duration + " mins\nNotes: " + notes;
                fitnessEntries.add(entry);
                adapter.notifyDataSetChanged();

                // Save entries
                saveEntries();

                // Clear inputs
                dateInput.setText("");
                activityInput.setText("");
                durationInput.setText("");
                notesInput.setText("");

                Toast.makeText(this, "Entry added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please fill all required fields.", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear Button Click Listener
        clearButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear All Entries")
                    .setMessage("Are you sure you want to clear all entries?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        fitnessEntries.clear();
                        adapter.notifyDataSetChanged();
                        saveEntries();
                        Toast.makeText(this, "All entries cleared!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void saveEntries() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(fitnessEntries);
        editor.putString("entries", json);
        editor.apply();
    }

    private void loadEntries() {
        String json = sharedPreferences.getString("entries", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            fitnessEntries = new Gson().fromJson(json, type);
        } else {
            fitnessEntries = new ArrayList<>();
        }
    }
}

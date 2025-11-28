package com.example.waterreminder;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText firstName, lastName, email, age, height, weight, dailyGoal;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private Button updateBtn, logoutBtn, backBtn;
    private DatabaseHelper db;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        dailyGoal = findViewById(R.id.dailyGoal);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        maleRadioButton = findViewById(R.id.maleRadioButton);
        femaleRadioButton = findViewById(R.id.femaleRadioButton);
        updateBtn = findViewById(R.id.updateBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        backBtn = findViewById(R.id.backBtn);

        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        loadUserProfile();

        updateBtn.setOnClickListener(v -> updateProfile());
        logoutBtn.setOnClickListener(v -> logout());
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadUserProfile() {
        Cursor cursor = db.getUser(userId);
        if (cursor.moveToFirst()) {
            firstName.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            lastName.setText(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
            email.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            age.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("age"))));
            height.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("height"))));
            weight.setText(String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("weight"))));
            dailyGoal.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("daily_goal"))));

            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            if (gender != null) {
                if (gender.equals("Male")) {
                    maleRadioButton.setChecked(true);
                } else if (gender.equals("Female")) {
                    femaleRadioButton.setChecked(true);
                }
            }
        }
        cursor.close();
    }

    private void updateProfile() {
        String firstNameStr = firstName.getText().toString();
        String lastNameStr = lastName.getText().toString();
        String emailStr = email.getText().toString();
        int ageInt = Integer.parseInt(age.getText().toString());
        float heightFlt = Float.parseFloat(height.getText().toString());
        float weightFlt = Float.parseFloat(weight.getText().toString());
        int dailyGoalInt = Integer.parseInt(dailyGoal.getText().toString());

        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        String genderStr = "";
        if (selectedGenderId == R.id.maleRadioButton) {
            genderStr = "Male";
        } else if (selectedGenderId == R.id.femaleRadioButton) {
            genderStr = "Female";
        }

        if (db.updateUser(userId, firstNameStr, lastNameStr, emailStr, ageInt, heightFlt, weightFlt, genderStr, dailyGoalInt)) {
            Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // No need to clear SharedPreferences anymore
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

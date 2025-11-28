package com.example.waterreminder;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView greeting, consumedAmount;
    private ProgressBar waterProgress;
    private Button add250ml, add500ml, profileBtn, historyBtn;
    private int dailyGoal = 2000;
    private int currentConsumption = 0;
    private int userId;
    private DatabaseHelper db;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private static final String CHANNEL_ID = "water_reminder_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        greeting = findViewById(R.id.greeting);
        consumedAmount = findViewById(R.id.consumedAmount);
        waterProgress = findViewById(R.id.waterProgress);
        add250ml = findViewById(R.id.add250ml);
        add500ml = findViewById(R.id.add500ml);
        profileBtn = findViewById(R.id.profileBtn);
        historyBtn = findViewById(R.id.historyBtn);

        db = new DatabaseHelper(this);
        userId = getIntent().getIntExtra("userId", -1);

        loadUserData();
        loadWaterIntake();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }

        createNotificationChannel();
        scheduleNotifications();

        add250ml.setOnClickListener(v -> addWater(250));
        add500ml.setOnClickListener(v -> addWater(500));

        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });

        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Water Reminder";
            String description = "Channel for water reminder notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotifications() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE);

        // Schedule for 10 AM
        Calendar calendar10 = Calendar.getInstance();
        calendar10.set(Calendar.HOUR_OF_DAY, 10);
        calendar10.set(Calendar.MINUTE, 0);
        calendar10.set(Calendar.SECOND, 0);

        // Schedule for 4 PM
        Calendar calendar16 = Calendar.getInstance();
        calendar16.set(Calendar.HOUR_OF_DAY, 16);
        calendar16.set(Calendar.MINUTE, 0);
        calendar16.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar10.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent1);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar16.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
        loadWaterIntake();
    }

    private void loadUserData() {
        Cursor cursor = db.getUser(userId);
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            dailyGoal = cursor.getInt(cursor.getColumnIndexOrThrow("daily_goal"));
            greeting.setText("Hello, " + firstName + "!");
        }
        cursor.close();
    }

    private void loadWaterIntake() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.getWaterIntake(userId, today);
        if (cursor.moveToFirst()) {
            currentConsumption = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
        }
        cursor.close();
        updateUI();
    }

    private void addWater(int amount) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        db.addWaterIntake(userId, today, amount);
        loadWaterIntake();
    }

    private void updateUI() {
        consumedAmount.setText("Today's consumption: " + currentConsumption + " ml");
        waterProgress.setMax(dailyGoal);
        waterProgress.setProgress(currentConsumption);
    }
}

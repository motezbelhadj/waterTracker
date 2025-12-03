package com.example.waterreminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<ConsumptionEntry> consumptionHistory;
    private DatabaseHelper dbHelper;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", -1);
        int dailyGoal = 0;

        if (userId != -1) {
            consumptionHistory = dbHelper.getConsumptionHistory(userId);
            dailyGoal = dbHelper.getDailyGoal(userId);
        } else {
            consumptionHistory = new ArrayList<>();
        }

        historyAdapter = new HistoryAdapter(consumptionHistory, dailyGoal);
        historyRecyclerView.setAdapter(historyAdapter);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

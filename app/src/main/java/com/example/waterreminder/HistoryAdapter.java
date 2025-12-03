package com.example.waterreminder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private ArrayList<ConsumptionEntry> consumptionHistory;
    private int dailyGoal;

    public HistoryAdapter(ArrayList<ConsumptionEntry> consumptionHistory, int dailyGoal) {
        this.consumptionHistory = consumptionHistory;
        this.dailyGoal = dailyGoal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsumptionEntry entry = consumptionHistory.get(position);
        holder.dateTextView.setText(entry.getDate());
        holder.consumptionTextView.setText(String.valueOf(entry.getConsumption()) + " ml");

        if (dailyGoal > 0) {
            int percentage = (int) (((double) entry.getConsumption() / dailyGoal) * 100);
            holder.percentageTextView.setText(percentage + "%");
        } else {
            holder.percentageTextView.setText("0%");
        }
    }

    @Override
    public int getItemCount() {
        return consumptionHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView consumptionTextView;
        public TextView percentageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            consumptionTextView = itemView.findViewById(R.id.consumptionTextView);
            percentageTextView = itemView.findViewById(R.id.percentageTextView);
        }
    }
}

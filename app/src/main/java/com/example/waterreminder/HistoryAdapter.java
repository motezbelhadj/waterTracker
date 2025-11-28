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

    public HistoryAdapter(ArrayList<ConsumptionEntry> consumptionHistory) {
        this.consumptionHistory = consumptionHistory;
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
    }

    @Override
    public int getItemCount() {
        return consumptionHistory.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView consumptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            consumptionTextView = itemView.findViewById(R.id.consumptionTextView);
        }
    }
}

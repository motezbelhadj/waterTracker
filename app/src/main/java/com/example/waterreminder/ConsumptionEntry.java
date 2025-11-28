package com.example.waterreminder;

public class ConsumptionEntry {
    private String date;
    private int consumption;

    public ConsumptionEntry(String date, int consumption) {
        this.date = date;
        this.consumption = consumption;
    }

    public String getDate() {
        return date;
    }

    public int getConsumption() {
        return consumption;
    }
}

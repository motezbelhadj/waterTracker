package com.example.waterreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "WaterReminder.db";
    private static final int DATABASE_VERSION = 4; // Incremented version

    // User table
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_GENDER = "gender";
    private static final String COLUMN_DAILY_GOAL = "daily_goal";

    // Water intake table
    private static final String TABLE_WATER_INTAKE = "water_intake";
    private static final String COLUMN_INTAKE_ID = "id";
    private static final String COLUMN_INTAKE_USER_ID = "user_id";
    private static final String COLUMN_INTAKE_DATE = "date";
    private static final String COLUMN_INTAKE_AMOUNT = "amount";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_LAST_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT UNIQUE," +
                COLUMN_PASSWORD + " TEXT," +
                COLUMN_AGE + " INTEGER," +
                COLUMN_HEIGHT + " REAL," +
                COLUMN_WEIGHT + " REAL," +
                COLUMN_GENDER + " TEXT," +
                COLUMN_DAILY_GOAL + " INTEGER DEFAULT 2000" + ")"; // Added default value

        String CREATE_WATER_INTAKE_TABLE = "CREATE TABLE " + TABLE_WATER_INTAKE + "(" +
                COLUMN_INTAKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_INTAKE_USER_ID + " INTEGER," +
                COLUMN_INTAKE_DATE + " TEXT," +
                COLUMN_INTAKE_AMOUNT + " INTEGER," +
                "FOREIGN KEY(" + COLUMN_INTAKE_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_WATER_INTAKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WATER_INTAKE);
        onCreate(db);
    }

    // --- User methods ---
    public boolean registerUser(String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public Cursor checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{email, password});
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public Cursor getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public int getDailyGoal(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_DAILY_GOAL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        int dailyGoal = 0;
        if (cursor.moveToFirst()) {
            dailyGoal = cursor.getInt(0);
        }
        cursor.close();
        return dailyGoal;
    }

    public boolean updateUser(int userId, String firstName, String lastName, String email, int age, float height, float weight, String gender, int dailyGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_GENDER, gender);
        values.put(COLUMN_DAILY_GOAL, dailyGoal);
        int rows = db.update(TABLE_USERS, values, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    // --- Water intake methods ---
    public boolean addWaterIntake(int userId, String date, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INTAKE_USER_ID, userId);
        values.put(COLUMN_INTAKE_DATE, date);
        values.put(COLUMN_INTAKE_AMOUNT, amount);
        long result = db.insert(TABLE_WATER_INTAKE, null, values);
        return result != -1;
    }

    public Cursor getWaterIntake(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT SUM(" + COLUMN_INTAKE_AMOUNT + ") as total FROM " + TABLE_WATER_INTAKE +
                " WHERE " + COLUMN_INTAKE_USER_ID + "=? AND " + COLUMN_INTAKE_DATE + "=?", new String[]{String.valueOf(userId), date});
    }

    public void resetWaterIntake(int userId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WATER_INTAKE, COLUMN_INTAKE_USER_ID + "=? AND " + COLUMN_INTAKE_DATE + "=?", new String[]{String.valueOf(userId), date});
    }

    public ArrayList<ConsumptionEntry> getConsumptionHistory(int userId) {
        ArrayList<ConsumptionEntry> historyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_INTAKE_DATE + ", SUM(" + COLUMN_INTAKE_AMOUNT + ") as total " +
                        "FROM " + TABLE_WATER_INTAKE +
                        " WHERE " + COLUMN_INTAKE_USER_ID + " = ? " +
                        "GROUP BY " + COLUMN_INTAKE_DATE +
                        " ORDER BY " + COLUMN_INTAKE_DATE + " DESC",
                new String[]{String.valueOf(userId)}
        );

        if (cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                int totalConsumption = cursor.getInt(1);
                historyList.add(new ConsumptionEntry(date, totalConsumption));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }
}

package com.example.healthconnect;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper instance;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "HealthConnect.db";

    public static final String TABLE_PATIENT = "Patient";
    public static final String COLUMN_PATIENT_ID = "PatientID"; // Primary Key
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_HEIGHT = "Height";
    public static final String COLUMN_WEIGHT = "Weight";
    public static final String COLUMN_DATE_OF_BIRTH = "DateOfBirth";
    public static final String COLUMN_CONTACT_NUMBER = "ContactNumber";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // avoid creating too many instance of DatabaseHelper
    public static synchronized DatabaseHelper getInstance(Context context) {
        // if there is no db object created
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPatientTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        onCreate(db);
    }
    public void closeDatabase() {
        if (instance != null && instance.getWritableDatabase().isOpen()) {
            instance.getWritableDatabase().close();
            // Reset the instance to allow future reinitialization
            instance = null;
        }
    }

    private void createPatientTable(SQLiteDatabase db) {
        String CREATE_PATIENT_TABLE = "CREATE TABLE " + TABLE_PATIENT + "("
                + COLUMN_PATIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_HEIGHT + " REAL, "
                + COLUMN_WEIGHT + " REAL, "
                + COLUMN_DATE_OF_BIRTH + " TEXT, "
                + COLUMN_CONTACT_NUMBER + " TEXT)";
        db.execSQL(CREATE_PATIENT_TABLE);

        // Prepare demo data
        String insertPatientQuery = "INSERT INTO " + TABLE_PATIENT + " ("
                + COLUMN_NAME + ", " + COLUMN_HEIGHT + ", "
                + COLUMN_WEIGHT + ", " + COLUMN_DATE_OF_BIRTH + ", " + COLUMN_CONTACT_NUMBER + ") "
                + "VALUES (?, ?, ?, ?, ?)";


        SQLiteStatement statement = db.compileStatement(insertPatientQuery);

        statement.bindString(1, "Tim Nguyen");
        statement.bindString(2, "1985-07-15"); // we will use date format when displaying
        statement.bindString(3, "0987654321");
        statement.bindDouble(4, 175);
        statement.bindDouble(5, 70);
        statement.executeInsert();

        statement.bindString(1, "Phyo Thaw");
        statement.bindString(2, "1990-03-22");
        statement.bindString(3, "0912345678");
        statement.bindDouble(4, 180);
        statement.bindDouble(5, 75);
        statement.executeInsert();

        statement.bindString(1, "Bruno Beserra");
        statement.bindString(2, "1988-11-10");
        statement.bindString(3, "0998765432");
        statement.bindDouble(4, 178);
        statement.bindDouble(5, 80);
        statement.executeInsert();
    }
    public long insertPatient(String name, double height, double weight, String dateOfBirth, String contactNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        String insertPatientQuery = "INSERT INTO " + TABLE_PATIENT + " ("
                + COLUMN_NAME + ", " + COLUMN_HEIGHT + ", "
                + COLUMN_WEIGHT + ", " + COLUMN_DATE_OF_BIRTH + ", " + COLUMN_CONTACT_NUMBER + ") "
                + "VALUES (?, ?, ?, ?, ?)";

        SQLiteStatement statement = db.compileStatement(insertPatientQuery);
        statement.bindString(1, name);
        statement.bindDouble(2, height);
        statement.bindDouble(3, weight);
        statement.bindString(4, dateOfBirth);
        statement.bindString(5, contactNumber);

        long rowId = statement.executeInsert();
        statement.close();
        return rowId;
    }

    // Method to get all patients
    public List<Patient> getAllPatients() {
        List<Patient> patientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PATIENT,
                null,
                null,
                null,
                null,
                null,
                COLUMN_NAME + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Patient patient = new Patient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NUMBER))
                );
                patientList.add(patient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return patientList;
    }

    // Method to search patients by name
    public List<Patient> searchPatients(String query) {
        List<Patient> patientList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PATIENT,
                null,
                COLUMN_NAME + " LIKE ?",
                new String[]{"%" + query + "%"},
                null,
                null,
                COLUMN_NAME + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Patient patient = new Patient(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_PATIENT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_HEIGHT)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NUMBER))
                );
                patientList.add(patient);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return patientList;
    }

}

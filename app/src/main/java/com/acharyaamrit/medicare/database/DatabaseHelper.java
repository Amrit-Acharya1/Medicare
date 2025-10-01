package com.acharyaamrit.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.acharyaamrit.medicare.model.Patient;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "medicare.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // patience tables for the application
        String createPatientTable = "CREATE TABLE patient (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "token TEXT," +
                "patient_id INTEGER," +
                "user_type TEXT," +
                "name TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "dob TEXT," +
                "address TEXT," +
                "blood_group TEXT," +
                "lat TEXT," +
                "longt TEXT," +
                "emergency_contact TEXT," +
                "gender TEXT)";
        db.execSQL(createPatientTable);

        String createDoctorTable = "CREATE TABLE doctor (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "token TEXT," +
                "doctor_id INTEGER," +
                "user_type TEXT," +
                "name TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "dob TEXT," +
                "address TEXT," +
                "speciality TEXT," +
                "clinic TEXT," +
                "gender TEXT," +
                "topic TEXT)";
        db.execSQL(createDoctorTable);

        String createPharmacyTable = "CREATE TABLE pharmacy (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "token TEXT," +
                "pharmacy_id INTEGER," +
                "user_type TEXT," +
                "name TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "dob TEXT," +
                "address TEXT," +
                "pan_no TEXT," +
                "lan TEXT," +
                "longt TEXT," +
                "clicnic TEXT)";
        db.execSQL(createPharmacyTable);


        //general + clinic
        String createClinicTable = "CREATE TABLE clinic (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "token TEXT," +
                "clinic_id INTEGER," +
                "user_type TEXT," +
                "name TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "dob TEXT," +
                "address TEXT," +
                "lan TEXT," +
                "longt TEXT)";
        db.execSQL(createClinicTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS patience");
        db.execSQL("DROP TABLE IF EXISTS doctor");
        db.execSQL("DROP TABLE IF EXISTS pharmacy");
        db.execSQL("DROP TABLE IF EXISTS clinic");

        onCreate(db);
    }

    public void insertPatient(Patient patient, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", patient.getId());
        values.put("token", token);
        values.put("patient_id", patient.getPatient_id());
        values.put("user_type", patient.getUser_type());
        values.put("name", patient.getName());
        values.put("email", patient.getEmail());
        values.put("contact", patient.getContact());
        values.put("dob", patient.getDob());
        values.put("address", patient.getAddress());
        values.put("blood_group", patient.getBlood_group());
        values.put("lat", patient.getLat());
        values.put("longt", patient.getLongt());
        values.put("emergency_contact", patient.getEmergency_contact());
        values.put("gender", patient.getGender());

        // Clear existing patient data to avoid duplicates
        db.delete("patient", null, null);
        db.insert("patient", null, values);
        db.close();
    }
    public void deletePatient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("patient", "id" + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Patient getPatientByToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("patient", null, "token = ?", new String[]{token}, null, null, null);
        Patient patient = null;

        if (cursor.moveToFirst()) {
            patient = new Patient();
            patient.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            patient.setPatient_id(cursor.getInt(cursor.getColumnIndexOrThrow("patient_id")));
            patient.setUser_type(cursor.getString(cursor.getColumnIndexOrThrow("user_type")));
            patient.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            patient.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            patient.setContact(cursor.getString(cursor.getColumnIndexOrThrow("contact")));
            patient.setDob(cursor.getString(cursor.getColumnIndexOrThrow("dob")));
            patient.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            patient.setBlood_group(cursor.getString(cursor.getColumnIndexOrThrow("blood_group")));
            patient.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
            patient.setLongt(cursor.getString(cursor.getColumnIndexOrThrow("longt")));
            patient.setEmergency_contact(cursor.getString(cursor.getColumnIndexOrThrow("emergency_contact")));
            patient.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
        }

        cursor.close();
        db.close();
        return patient;
    }



}

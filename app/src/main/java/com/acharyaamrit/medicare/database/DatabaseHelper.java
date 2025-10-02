package com.acharyaamrit.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.acharyaamrit.medicare.model.Clicnic;
import com.acharyaamrit.medicare.model.Doctor;
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.Pharmacy;

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
                "clicnic TEXT," +
                "gender TEXT)";
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
                "lat TEXT," +
                "longt TEXT," +
                "clicnic TEXT)";
        db.execSQL(createPharmacyTable);


        //general + clinic
        String createClinicTable = "CREATE TABLE clicnic (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "token TEXT," +
                "clicnic_id INTEGER," +
                "user_type TEXT," +
                "name TEXT," +
                "email TEXT," +
                "contact TEXT," +
                "dob TEXT," +
                "address TEXT," +
                "lat TEXT," +
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





    public void insertDoctor(Doctor doctor, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", doctor.getId());
        values.put("token", token);
        values.put("doctor_id", doctor.getDoctor_id());
        values.put("user_type", doctor.getUser_type());
        values.put("name", doctor.getName());
        values.put("email", doctor.getEmail());
        values.put("contact", doctor.getContact());
        values.put("dob", doctor.getDob());
        values.put("address", doctor.getAddress());
        values.put("speciality", doctor.getSpeciality());
        values.put("clicnic", doctor.getClicnic());
        values.put("gender", doctor.getGender());


        // Clear existing patient data to avoid duplicates
        db.delete("doctor", null, null);
        db.insert("doctor", null, values);
        db.close();

    }

    public void deleteDoctor(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("doctor", "id" + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }


    public Doctor getDoctorByToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("doctor", null, "token = ?", new String[]{token}, null, null, null);

        Doctor doctor = null;

        if (cursor.moveToFirst()) {
            doctor = new Doctor();
            doctor.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            doctor.setDoctor_id(cursor.getInt(cursor.getColumnIndexOrThrow("doctor_id")));
            doctor.setUser_type(cursor.getString(cursor.getColumnIndexOrThrow("user_type")));
            doctor.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            doctor.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            doctor.setContact(cursor.getString(cursor.getColumnIndexOrThrow("contact")));
            doctor.setDob(cursor.getString(cursor.getColumnIndexOrThrow("dob")));
            doctor.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            doctor.setSpeciality(cursor.getString(cursor.getColumnIndexOrThrow("speciality")));
            doctor.setClicnic(cursor.getString(cursor.getColumnIndexOrThrow("clicnic")));
            doctor.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));

        }

        cursor.close();
        db.close();
        return doctor;
    }



    public void insertPharmacy(Pharmacy pharmacy, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", pharmacy.getId());
        values.put("token", token);
        values.put("pharmacy_id", pharmacy.getPharmacy_id());
        values.put("user_type", pharmacy.getUser_type());
        values.put("name", pharmacy.getName());
        values.put("email", pharmacy.getEmail());
        values.put("contact", pharmacy.getContact());
        values.put("dob", pharmacy.getDob());
        values.put("address", pharmacy.getAddress());
        values.put("pan_no", pharmacy.getPan_no());
        values.put("lat", pharmacy.getLat());
        values.put("longt", pharmacy.getLongt());
        values.put("clicnic", pharmacy.getClicnic());


        // Clear existing patient data to avoid duplicates
        db.delete("pharmacy", null, null);
        db.insert("pharmacy", null, values);
        db.close();

    }

    public void deletePharmacy(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("pharmacy", "id" + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Pharmacy getPharmacyByToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("pharmacy", null, "token = ?", new String[]{token}, null, null, null);

        Pharmacy pharmacy = null;

        if (cursor.moveToFirst()) {
            pharmacy = new Pharmacy();
            pharmacy.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            pharmacy.setPharmacy_id(cursor.getInt(cursor.getColumnIndexOrThrow("pharmacy_id")));
            pharmacy.setUser_type(cursor.getString(cursor.getColumnIndexOrThrow("user_type")));
            pharmacy.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            pharmacy.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            pharmacy.setContact(cursor.getString(cursor.getColumnIndexOrThrow("contact")));
            pharmacy.setDob(cursor.getString(cursor.getColumnIndexOrThrow("dob")));
            pharmacy.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            pharmacy.setPan_no(cursor.getString(cursor.getColumnIndexOrThrow("pan_no")));
            pharmacy.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
            pharmacy.setLongt(cursor.getString(cursor.getColumnIndexOrThrow("longt")));
            pharmacy.setClicnic(cursor.getString(cursor.getColumnIndexOrThrow("clicnic")));
        }

        cursor.close();
        db.close();
        return pharmacy;
    }



    public void insertClicnic(Clicnic clicnic, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", clicnic.getId());
        values.put("token", token);
        values.put("clicnic_id", clicnic.getClicnic_id());
        values.put("user_type", clicnic.getUser_type());
        values.put("name", clicnic.getName());
        values.put("email", clicnic.getEmail());
        values.put("contact", clicnic.getContact());
        values.put("dob", clicnic.getDob());
        values.put("address", clicnic.getAddress());
        values.put("lat", clicnic.getLat());
        values.put("longt", clicnic.getLongt());


        // Clear existing patient data to avoid duplicates
        db.delete("clicnic", null, null);
        db.insert("clicnic", null, values);
        db.close();

    }

    public void deleteClicnic(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("clicnic", "id" + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
    public Clicnic getClicnicByToken(String token) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("clicnic", null, "token = ?", new String[]{token}, null, null, null);

        Clicnic clicnic = null;

        if (cursor.moveToFirst()) {
            clicnic = new Clicnic();
            clicnic.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            clicnic.setClicnic_id(cursor.getInt(cursor.getColumnIndexOrThrow("clicnic_id")));
            clicnic.setUser_type(cursor.getString(cursor.getColumnIndexOrThrow("user_type")));
            clicnic.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            clicnic.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            clicnic.setContact(cursor.getString(cursor.getColumnIndexOrThrow("contact")));
            clicnic.setDob(cursor.getString(cursor.getColumnIndexOrThrow("dob")));
            clicnic.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            clicnic.setLat(cursor.getString(cursor.getColumnIndexOrThrow("lat")));
            clicnic.setLongt(cursor.getString(cursor.getColumnIndexOrThrow("longt")));
        }

        cursor.close();
        db.close();
        return clicnic;
    }

}

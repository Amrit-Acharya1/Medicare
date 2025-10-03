package com.acharyaamrit.medicare.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.acharyaamrit.medicare.model.Clicnic;
import com.acharyaamrit.medicare.model.CurrentPreciptionResponse;
import com.acharyaamrit.medicare.model.Doctor;
import com.acharyaamrit.medicare.model.Patient;
import com.acharyaamrit.medicare.model.Pharmacy;
import com.acharyaamrit.medicare.model.patientModel.CurrentPreciption;
import com.acharyaamrit.medicare.model.patientModel.Preciption;

import java.util.ArrayList;
import java.util.List;

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

        String CREATE_CURRENT_PRESCRIPTIONS_TABLE = "CREATE TABLE current_prescriptions (" +
                "id INTEGER PRIMARY KEY," +
                "doctor_id TEXT ," +
                "patient_id TEXT ," +
                "created_at TEXT )";
        db.execSQL(CREATE_CURRENT_PRESCRIPTIONS_TABLE);


        String CREATE_PRESCRIPTION_ITEMS_TABLE = "CREATE TABLE prescription_items (" +
                "id INTEGER PRIMARY KEY," +
                "prescription_relation_id INTEGER NOT NULL," +
                "medicine_id INTEGER NOT NULL," +
                "frequency TEXT NOT NULL," +
                "duration TEXT NOT NULL," +
                "duration_type TEXT NOT NULL," +
                "qty TEXT NOT NULL," +
                "note TEXT," +
                "created_at TEXT NOT NULL," +
                "FOREIGN KEY (prescription_relation_id) REFERENCES current_prescriptions(id) ON DELETE CASCADE)";

        db.execSQL(CREATE_PRESCRIPTION_ITEMS_TABLE);

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

    public long insertCurrentPreciption(CurrentPreciption prescription) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", prescription.getId());
        values.put("doctor_id", prescription.getDoctor_id());
        values.put("patient_id", prescription.getPatient_id());
        values.put("created_at", prescription.getCreated_at());
        long data =  db.insert("current_prescriptions", null, values);

        return data;
    }

    public long insertPreciptionItem(Preciption item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", item.getId());
        values.put("prescription_relation_id", item.getPreciption_relation_id());
        values.put("medicine_id", item.getMedicine_id());
        values.put("frequency", item.getFrequency());
        values.put("duration", item.getDuration());
        values.put("duration_type", item.getDuration_type());
        values.put("qty", item.getQty());
        values.put("note", item.getNote());
        values.put("created_at", item.getCreated_at());
        long data= db.insert("prescription_items", null, values);
        db.close();
        return data;
    }


    public void deleteCurrentPreciption() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("current_prescriptions", null, null);
    }

    // Delete a specific Preciption item
    public void deletePreciptionItem() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("prescription_items", null, null);
    }

    public CurrentPreciptionResponse getCurrentPreciptionWithItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "cp.id AS prescription_id, cp.doctor_id, cp.patient_id, " +
                "cp.created_at AS prescription_created_at, " +
                "pi.id AS item_id, pi.prescription_relation_id, pi.medicine_id, " +
                "pi.frequency, pi.duration, pi.duration_type, " +
                "pi.qty, pi.note, pi.created_at AS item_created_at " +
                "FROM current_prescriptions cp " +
                "LEFT JOIN prescription_items pi ON cp.id = pi.prescription_relation_id ";

        Cursor cursor = db.rawQuery(query, null);
        CurrentPreciptionResponse response = new CurrentPreciptionResponse();
        CurrentPreciption prescription = null;
        List<Preciption> items = new ArrayList<>();

        try {
            if (cursor != null && cursor.moveToFirst()) {
                // Initialize prescription with data from the first row
                prescription = new CurrentPreciption();

                int prescriptionIdIndex = cursor.getColumnIndexOrThrow("prescription_id");
                int doctorIdIndex = cursor.getColumnIndexOrThrow("doctor_id");
                int patientIdIndex = cursor.getColumnIndexOrThrow("patient_id");
                int prescriptionCreatedAtIndex = cursor.getColumnIndexOrThrow("prescription_created_at");

                prescription.setId(cursor.getInt(prescriptionIdIndex));
                prescription.setDoctor_id(cursor.isNull(doctorIdIndex) ? null : cursor.getString(doctorIdIndex));
                prescription.setPatient_id(cursor.isNull(patientIdIndex) ? null : cursor.getString(patientIdIndex));
                prescription.setCreated_at(cursor.isNull(prescriptionCreatedAtIndex) ? null : cursor.getString(prescriptionCreatedAtIndex));

                // Get column indices for items (do this once outside the loop for performance)
                int itemIdIndex = cursor.getColumnIndexOrThrow("item_id");
                int prescriptionRelationIdIndex = cursor.getColumnIndexOrThrow("prescription_relation_id");
                int medicineIdIndex = cursor.getColumnIndexOrThrow("medicine_id");
                int frequencyIndex = cursor.getColumnIndexOrThrow("frequency");
                int durationIndex = cursor.getColumnIndexOrThrow("duration");
                int durationTypeIndex = cursor.getColumnIndexOrThrow("duration_type");
                int qtyIndex = cursor.getColumnIndexOrThrow("qty");
                int noteIndex = cursor.getColumnIndexOrThrow("note");
                int itemCreatedAtIndex = cursor.getColumnIndexOrThrow("item_created_at");

                do {
                    // Check if there is a valid prescription item
                    if (!cursor.isNull(itemIdIndex)) {
                        Preciption item = new Preciption();
                        item.setId(cursor.getInt(itemIdIndex));
                        item.setPreciption_relation_id(cursor.isNull(prescriptionRelationIdIndex) ? null : String.valueOf(cursor.getInt(prescriptionRelationIdIndex)));
                        item.setMedicine_id(cursor.isNull(medicineIdIndex) ? null : cursor.getString(medicineIdIndex));
                        item.setFrequency(cursor.isNull(frequencyIndex) ? null : cursor.getString(frequencyIndex));
                        item.setDuration(cursor.isNull(durationIndex) ? null : cursor.getString(durationIndex));
                        item.setDuration_type(cursor.isNull(durationTypeIndex) ? null : cursor.getString(durationTypeIndex));
                        item.setQty(cursor.isNull(qtyIndex) ? null : cursor.getString(qtyIndex));
                        item.setNote(cursor.isNull(noteIndex) ? null : cursor.getString(noteIndex));
                        item.setCreated_at(cursor.isNull(itemCreatedAtIndex) ? null : cursor.getString(itemCreatedAtIndex));
                        items.add(item);
                    }
                } while (cursor.moveToNext());
            } else {
                android.util.Log.w("Database", "No prescription found  " );
            }
        } catch (IllegalArgumentException e) {
            android.util.Log.e("Database", "Column not found in cursor: " + e.getMessage(), e);
        } catch (Exception e) {
            android.util.Log.e("Database", "Error fetching prescription with items: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (prescription != null) {
            prescription.setPreciptionList(items); // Set the list (even if empty)
            response.setCurrentPreciption(prescription);
        }

        return response;
    }

}

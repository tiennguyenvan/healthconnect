package com.example.healthconnect;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class PatientFormActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private $ inThis;
    private long patientId = -1;  // Default to -1 if no patient ID is passed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_form);

        dbHelper = DatabaseHelper.getInstance(this);
        inThis = $.in(this);

        // Retrieve PATIENT_ID from intent (if available)
        patientId = getIntent().getLongExtra("PATIENT_ID", -1);
        if (patientId != -1) {
            populateFormForEditing(patientId);
        }

        // Set up date picker for DOB field with max date as today
        inThis.onClick(R.id.etPatientDOB).doAction(this::showDatePickerDialog);
        inThis.onFocus(R.id.etPatientDOB).doAction(this::showDatePickerDialog);

        // Set up back button to go back to the patient list
        inThis.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);

        // Set up submit button to add or update patient
        inThis.onClick(R.id.btSubmitPatient).doAction(() -> {
            if (!validateDOB(inThis.getTextFrom(R.id.etPatientDOB))) {
                inThis.showToast("Date of Birth cannot be in the future.");
                return;
            }

            // Insert or update patient data
            if (patientId == -1) {
                // New patient
                patientId = dbHelper.insertPatient(
                        inThis.getTextFrom(R.id.etPatientName),
                        inThis.getDoubleFrom(R.id.etPatientHeight),
                        inThis.getDoubleFrom(R.id.etPatientWeight),
                        inThis.getTextFrom(R.id.etPatientDOB),
                        inThis.getTextFrom(R.id.etPatientPhone)
                );
                inThis.showToast("Patient Added with ID: " + patientId);
            } else {
                // Update existing patient
                dbHelper.updatePatient(
                        patientId,
                        inThis.getTextFrom(R.id.etPatientName),
                        inThis.getDoubleFrom(R.id.etPatientHeight),
                        inThis.getDoubleFrom(R.id.etPatientWeight),
                        inThis.getTextFrom(R.id.etPatientDOB),
                        inThis.getTextFrom(R.id.etPatientPhone)
                );
                inThis.showToast("Patient Updated with ID: " + patientId);
            }

            // Navigate back to PatientProfileActivity
            Intent intent = new Intent(this, PatientProfileActivity.class);
            intent.putExtra("PATIENT_ID", patientId);
            startActivity(intent);
        });
    }

    // Shows a date picker dialog with today's date as the maximum
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            inThis.on(R.id.etPatientDOB).setText(selectedDate);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    // Validates if the DOB is not in the future
    private boolean validateDOB(String dob) {
        final Calendar today = Calendar.getInstance();
        Calendar dobCalendar = Calendar.getInstance();
        try {
            String[] parts = dob.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;
            int day = Integer.parseInt(parts[2]);
            dobCalendar.set(year, month, day);
        } catch (Exception e) {
            return false; // Invalid date format
        }
        return !dobCalendar.after(today);
    }

    // Populates form fields with patient data if editing
    private void populateFormForEditing(long patientId) {
        Patient patient = dbHelper.getPatientById(patientId);
        if (patient != null) {
            inThis.on(R.id.etPatientName).setText(patient.getName());
            inThis.on(R.id.etPatientHeight).setText(String.valueOf(patient.getHeight()));
            inThis.on(R.id.etPatientWeight).setText(String.valueOf(patient.getWeight()));
            inThis.on(R.id.etPatientDOB).setText(patient.getDateOfBirth());
            inThis.on(R.id.etPatientPhone).setText(patient.getContactNumber());
        }
    }
}

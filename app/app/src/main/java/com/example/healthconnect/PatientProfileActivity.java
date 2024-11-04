package com.example.healthconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PatientProfileActivity extends AppCompatActivity {
    private $ inThis;
    private DatabaseHelper dbHelper;
    private long patientId;
    private Patient patient;
    private TextView btBackToMain;
    private TextView btToPatientForm;
    private TextView tvPatientDetails;
    private final String LogTag = "PatientProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);

        // Initialize views
        btBackToMain = findViewById(R.id.btBackToMain);
        btToPatientForm = findViewById(R.id.btToPatientForm);
        tvPatientDetails = findViewById(R.id.tvPatientDetails);

        // Retrieve the PATIENT_ID from the Intent
        patientId = getIntent().getLongExtra("PATIENT_ID", -1);
        if (patientId == -1) {
            // Handle the error case where patientId is not passed
            finish(); // Close the activity if no patient ID is provided
            return;
        }

        // Get patient data from the database
        dbHelper = DatabaseHelper.getInstance(this);
        patient = dbHelper.getPatientById(patientId);
        if (patient == null) {
            // Handle the case when patient data is not found
            finish();
            return;
        }

        // Log the patient data to ensure correct retrieval
//        Log.d(LogTag, "Patient Data - Name: " + patient.getName() +
//                ", Height: " + patient.getHeight() +
//                ", Weight: " + patient.getWeight() +
//                ", Date of Birth: " + patient.getDateOfBirth() +
//                ", Contact Number: " + patient.getContactNumber());
        // Format the date of birth



        // Set the patient's name in the back button text
        btBackToMain.setText(getString(R.string.back_with_patient_name, patient.getName()));

        // Set up the back button to return to the patient list
        inThis.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);

        // Set up the edit button to navigate to the patient form for editing
        inThis.onClick(R.id.btToPatientForm).doAction(() -> {
            Intent intent = new Intent(this, PatientFormActivity.class);
            intent.putExtra("PATIENT_ID", patientId); // Pass patientId for editing
            startActivity(intent);
        });

        // Display patient details using string placeholders
        String patientDetails = getString(
                R.string.patient_details_format,
                patient.getHeight(),
                patient.getWeight(),
                formatDateOfBirth(patient.getDateOfBirth()),
                formatPhoneNumber(patient.getContactNumber())
        );
        tvPatientDetails.setText(patientDetails);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh patient data when activity resumes
        patient = dbHelper.getPatientById(patientId);
        if (patient != null) {
            btBackToMain.setText(getString(R.string.back_with_patient_name, patient.getName()));
            String patientDetails = getString(
                    R.string.patient_details_format,
                    patient.getHeight(),
                    patient.getWeight(),
                    formatDateOfBirth(patient.getDateOfBirth()),
                    formatPhoneNumber(patient.getContactNumber())
            );
            tvPatientDetails.setText(patientDetails);
        }
    }

    private String formatDateOfBirth(String dateOfBirth) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        try {
            Date date = originalFormat.parse(dateOfBirth);
            return targetFormat.format(date);
        } catch (ParseException e) {
            Log.e(LogTag, "Date parsing error: " + e.getMessage());
            return dateOfBirth; // Return the original date if parsing fails
        }
    }

    private String formatPhoneNumber(String contactNumber) {
        // Check if the phone number already has dashes
        if (contactNumber.matches("\\d{3}-\\d{3}-\\d{4}")) {
            return contactNumber; // Return as is if already formatted
        }

        // Remove any non-numeric characters
        contactNumber = contactNumber.replaceAll("[^\\d]", "");

        // Format the number if it has 10 digits
        if (contactNumber.length() == 10) {
            return contactNumber.replaceFirst("(\\d{3})(\\d{3})(\\d{4})", "$1-$2-$3");
        } else {
            // Return as is if it doesn't match the expected 10-digit length
            Log.e(LogTag, "Unexpected phone number length: " + contactNumber);
            return contactNumber;
        }
    }
}

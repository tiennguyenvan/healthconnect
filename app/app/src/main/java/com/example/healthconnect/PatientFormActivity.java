package com.example.healthconnect;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PatientFormActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private $ inThis;
    private long patientId = -1;  // Default to -1 if no patient ID is passed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHelper = DatabaseHelper.getInstance(this);
        inThis = $.in(this);


        // Retrieve PATIENT_ID from intent (if available)
        patientId = getIntent().getLongExtra(getString(R.string.key_patient_id), -1);
        if (patientId != -1) {
            populateFormForEditing(patientId);
            inThis.on(R.id.btSubmitPatient).setText(R.string.update_patient);
        }

        inThis.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);
        inThis.onFocus(R.id.etPatientDOB).pickDate();
        inThis.onClick(R.id.etPatientDOB).pickDate();
//        inThis.onClick(R.id.btSubmitPatient).showToast("TEST");
        inThis.onClick(R.id.btSubmitPatient).doAction(() -> {
            if (!validateInputs()) {
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
                inThis.showToast(getString(R.string.noti_patient_added, String.valueOf(patientId)));
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
                inThis.showToast(getString(R.string.noti_patient_updated, String.valueOf(patientId)));
            }

            // Pass the patient ID back to PatientProfileActivity
//            Intent intent = new Intent(this, PatientProfileActivity.class);
//            intent.putExtra("PATIENT_ID", patientId);
//            startActivity(intent);
            inThis.passToScreen(PatientProfileActivity.class, R.string.key_patient_id, patientId);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateInput(R.id.etPatientName,   R.id.tvPatientNameError);
        isValid &= inThis.validateInput(R.id.etPatientHeight, R.id.tvPatientHeightError);
        isValid &= inThis.validateInput(R.id.etPatientWeight, R.id.tvPatientWeightError);
        isValid &= inThis.validateInput(R.id.etPatientDOB,    R.id.tvPatientDOBError);
        isValid &= inThis.validateInput(R.id.etPatientPhone,  R.id.tvPatientPhoneError);
        return isValid;
    }

    private void populateFormForEditing(long patientId) {
        Patient patient = dbHelper.getPatientById(patientId);

        if (patient != null) {
            inThis.on(R.id.btBackToMain).setText(getString(R.string.back_with_patient_name, patient.getName()));
            inThis.on(R.id.etPatientName).setText(patient.getName());
            inThis.on(R.id.etPatientHeight).setText(String.valueOf(patient.getHeight()));
            inThis.on(R.id.etPatientWeight).setText(String.valueOf(patient.getWeight()));
            inThis.on(R.id.etPatientDOB).setText(patient.getDateOfBirth());
            inThis.on(R.id.etPatientPhone).setText(patient.getContactNumber());
        } else {
            inThis.showToast(getString(R.string.noti_no_patient_found, String.valueOf(patientId)));;
        }
    }
}
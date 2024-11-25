package com.example.healthconnect.activities.patient;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.models.Patient;

public class PatientFormActivity extends AppCompatActivity {
    private DbTable<Patient> patientTable;
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
        patientTable = DbTable.getInstance(this, Patient.class);
        inThis = $.in(this);

        // Retrieve PATIENT_ID from intent (if available)
        patientId = getIntent().getLongExtra(getString(R.string.key_patient_id), -1);
        if (patientId != -1) {
            populateFormForEditing(patientId);
        }

        // Set up date picker for DOB field with max date as today
        inThis.onClick(R.id.etPatientDOB).pickPastDate();
        inThis.onFocus(R.id.etPatientDOB).pickPastDate();

        // Set up back button to go back to the patient list
        inThis.onClick(R.id.btBackToPatientListOrProfile).doAction(() -> {
            if (patientId != -1) {
                inThis.passToScreen(PatientProfileActivity.class, R.string.key_patient_id, patientId);
            } else {
                inThis.goToScreen(PatientListActivity.class);
            }
        });


        // Set up submit button to add or update patient
        inThis.onClick(R.id.btSubmitPatient).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            // Insert or update patient data
            Patient patient = new Patient();
            patient.setName(inThis.getTextFrom(R.id.etPatientName));
            patient.setHeight(inThis.getDoubleFrom(R.id.etPatientHeight));
            patient.setWeight(inThis.getDoubleFrom(R.id.etPatientWeight));
            patient.setDateOfBirth(inThis.getTextFrom(R.id.etPatientDOB));
            patient.setContactNumber(inThis.getTextFrom(R.id.etPatientPhone));
            if (patientId == -1) {
                patientId = patientTable.add(patient);
                inThis.showToast(getString(R.string.noti_patient_added, String.valueOf(patientId)));
            } else {
                // Update existing patient
                patientTable.update(patientId, patient);
                inThis.showToast(getString(R.string.noti_patient_updated, String.valueOf(patientId)));
            }

            // Navigate back to PatientProfileActivity
            inThis.passToScreen(PatientProfileActivity.class, R.string.key_patient_id, patientId);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateInput(R.id.etPatientName, R.id.tvPatientNameError);
        isValid &= inThis.validateInput(R.id.etPatientHeight, R.id.tvPatientHeightError);
        isValid &= inThis.validateInput(R.id.etPatientWeight, R.id.tvPatientWeightError);
        isValid &= inThis.validateInput(R.id.etPatientDOB, R.id.tvPatientDOBError);
        isValid &= inThis.validateInput(R.id.etPatientPhone, R.id.tvPatientPhoneError);
        return isValid;
    }

    private void populateFormForEditing(long patientId) {
        Patient patient = patientTable.getById(patientId);

        if (patient != null) {
            inThis.on(R.id.btBackToPatientListOrProfile).setText(getString(R.string.back_with_patient_name, patient.getName()));
            inThis.on(R.id.etPatientName).setText(patient.getName());
            inThis.on(R.id.etPatientHeight).setText(String.valueOf(patient.getHeight()));
            inThis.on(R.id.etPatientWeight).setText(String.valueOf(patient.getWeight()));
            inThis.on(R.id.etPatientDOB).setText(patient.getDateOfBirth());
            inThis.on(R.id.etPatientPhone).setText(patient.getContactNumber());
            inThis.on(R.id.btSubmitPatient).setText(R.string.update_patient);
        } else {
            inThis.showToast(getString(R.string.noti_no_patient_found, String.valueOf(patientId)));
            ;
        }
    }
}
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

public class PatientProfileActivity extends AppCompatActivity {
    private $ inThis;

    private DbTable<Patient> patientTable;
    private long patientId;
    private Patient patient;

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
        patientId = getIntent().getLongExtra(getString(R.string.key_patient_id), -1);
        if (patientId == -1) {
            finish(); // Fixme: Close the activity if no patient ID is provided
            return;
        }

        patientTable = DbTable.getInstance(this, Patient.class);

        patient = patientTable.getById(patientId);
        if (patient == null) {
            finish();
            return;
        }

        inThis.on(R.id.btMedicationListToMain).setText(getString(R.string.back_with_patient_name, patient.getName()));
        inThis.onClick(R.id.btMedicationListToMain).goToScreen(PatientListActivity.class);
        inThis.onClick(R.id.btToPatientForm).doAction(() -> {
            inThis.passToScreen(PatientFormActivity.class, R.string.key_patient_id, patientId);
        });

        showPatientDetails(patient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        patient = patientTable.getById(patientId);
        if (patient != null) {
            showPatientDetails(patient);
        }
    }

    private void showPatientDetails(Patient patient) {
        inThis.on(R.id.btMedicationListToMain).setText(getString(R.string.back_with_patient_name, patient.getName()));
        String patientDetails = getString(
                R.string.patient_details_format,
                patient.getHeight(),
                patient.getWeight(),
                inThis.formatDateOfBirth(patient.getDateOfBirth()),
                inThis.formatPhoneNumber(patient.getContactNumber())
        );
        inThis.on(R.id.tvPatientDetails).setText(patientDetails);
    }
}

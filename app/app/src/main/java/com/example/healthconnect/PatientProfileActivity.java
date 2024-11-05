package com.example.healthconnect;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PatientProfileActivity extends AppCompatActivity {
    private $ inThis;
    private DatabaseHelper dbHelper;
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

        dbHelper = DatabaseHelper.getInstance(this);
        patient = dbHelper.getPatientById(patientId);
        if (patient == null) {
            finish();
            return;
        }

        inThis.on(R.id.btBackToMain).setText(getString(R.string.back_with_patient_name, patient.getName()));
        inThis.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);
        inThis.onClick(R.id.btToPatientForm).doAction(() -> {
            inThis.passToScreen(PatientFormActivity.class, R.string.key_patient_id, patientId);
        });

        showPatientDetails(patient);
    }

    @Override
    protected void onResume() {
        super.onResume();
        patient = dbHelper.getPatientById(patientId);
        if (patient != null) {
            showPatientDetails(patient);
        }
    }

    private void showPatientDetails(Patient patient) {
        inThis.on(R.id.btBackToMain).setText(getString(R.string.back_with_patient_name, patient.getName()));
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

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
    private String LogTag = "PatientFormActivity";

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
        inThis.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);
        inThis.onFocus(R.id.etPatientDOB).pickDate();
        inThis.onClick(R.id.etPatientDOB).pickDate();
        inThis.onClick(R.id.btSubmitPatient).showToast("TEST");
        inThis.onClick(R.id.btSubmitPatient).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            long patientId = dbHelper.insertPatient(
                    inThis.getTextFrom  (R.id.etPatientName),
                    inThis.getDoubleFrom(R.id.etPatientHeight),
                    inThis.getDoubleFrom(R.id.etPatientWeight),
                    inThis.getTextFrom  (R.id.etPatientDOB),
                    inThis.getTextFrom  (R.id.etPatientPhone)
            );
            inThis.showToast("Patient Added with ID: " + patientId);
            inThis.goToScreen(PatientProfileActivity.class);
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
}
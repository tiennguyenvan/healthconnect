package com.example.healthconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PatientFormActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private $ $_;
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

        $_ = $.in(this);
        $_.onClick(R.id.btBackToMain).goToScreen(PatientListActivity.class);
        $_.onFocus(R.id.etPatientDOB).pickDate();
        $_.onClick(R.id.etPatientDOB).pickDate();
        $_.onClick(R.id.btSubmitPatient).showToast("TEST");
        $_.onClick(R.id.btSubmitPatient).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            long patientId = dbHelper.insertPatient(
                    $_.getTextFrom(R.id.etPatientName),
                    $_.getDoubleFrom(R.id.etPatientHeight),
                    $_.getDoubleFrom(R.id.etPatientWeight),
                    $_.getTextFrom(R.id.etPatientDOB),
                    $_.getTextFrom(R.id.etPatientPhone)
            );
            $_.showToast("Patient Added with ID: " + patientId);
            $_.goToScreen(PatientProfileActivity.class);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= $_.validateInput(R.id.etPatientName,   R.id.tvPatientNameError);
        isValid &= $_.validateInput(R.id.etPatientHeight, R.id.tvPatientHeightError);
        isValid &= $_.validateInput(R.id.etPatientWeight, R.id.tvPatientWeightError);
        isValid &= $_.validateInput(R.id.etPatientDOB,    R.id.tvPatientDOBError);
        isValid &= $_.validateInput(R.id.etPatientPhone,  R.id.tvPatientPhoneError);
        return isValid;
    }
}
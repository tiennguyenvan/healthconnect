package com.example.healthconnect.activities.appointment;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Patient;

public class AppointmentNewActivity extends AppCompatActivity {
    private $ inThis;
    private DbTable<Patient> patientTable;
    private DbTable<Appointment> appointmentTable;
    private long patientId = -1;  // Default to -1 if no patient ID is passed
    private long appointmentId = -1;  // Default to -1 if no appointment ID is passed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inThis = $.in(this);
        inThis.onClick(R.id.btBackToMain).goToScreen(AppointmentListActivity.class);
        patientTable = DbTable.getInstance(this, Patient.class);
        appointmentTable = DbTable.getInstance(this, Appointment.class);

        patientId = getIntent().getLongExtra(getString(R.string.key_patient_id), -1);
        Patient patient = patientTable.getById(patientId);
        if (patientId != -1) {
            inThis.on(R.id.tvPatientInfo).setText(getString(R.string.appointment_patient_name, patient.getName()));
        }
    }
}
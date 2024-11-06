package com.example.healthconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.activities.appointment.appointmentUpdate;
import com.example.healthconnect.activities.patient.PatientListActivity;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.core.$;
import com.example.healthconnect.models.Patient;

public class MainActivity extends AppCompatActivity {
    private DbTable<Patient> patientTable;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        patientTable = DbTable.getInstance(this, Patient.class);

        inThis.on(R.id.tvPatientCount).setText(String.valueOf(patientTable.size()));
        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        inThis.onClick(R.id.btAppointments).goToScreen(appointmentUpdate.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patientTable.closeDatabase();
    }
}
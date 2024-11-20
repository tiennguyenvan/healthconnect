package com.example.healthconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.activities.appointment.AppointmentListActivity;
import com.example.healthconnect.activities.diagnose.DiagnoseListActivity;
import com.example.healthconnect.activities.medication.MedicationListActivity;
import com.example.healthconnect.activities.patient.PatientListActivity;
import com.example.healthconnect.activities.symptom.SymptomListActivity;
import com.example.healthconnect.activities.treatment.TreatmentListActivity;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.models.Treatment;

public class MainActivity extends AppCompatActivity {
    private DbTable<Patient> patientTable;
    private DbTable<Medication> medicationTable;
    private DbTable<Appointment> appointmentTable;
    private DbTable<Symptom> symptomTable;
    private DbTable<Treatment> treatmentTable;
    private DbTable<Diagnose> diagnoseTable;
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
        medicationTable = DbTable.getInstance(this, Medication.class);
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        symptomTable = DbTable.getInstance(this, Symptom.class);
        treatmentTable = DbTable.getInstance(this, Treatment.class);
        diagnoseTable = DbTable.getInstance(this, Diagnose.class);


        inThis.on(R.id.tvPatientCount).setText(String.valueOf(patientTable.size()));
        inThis.on(R.id.tvMedicationCount).setText(String.valueOf(medicationTable.size()));
        inThis.on(R.id.tvAppointmentCount).setText(String.valueOf(appointmentTable.size()));
        inThis.on(R.id.tvSymptomCount).setText(String.valueOf(symptomTable.size()));
        inThis.on(R.id.tvTreatmentCount).setText(String.valueOf(treatmentTable.size()));
        inThis.on(R.id.tvDiagnoseCount).setText(String.valueOf(diagnoseTable.size()));

        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        inThis.onClick(R.id.btAppointments).goToScreen(AppointmentListActivity.class);
        inThis.onClick(R.id.btMedications).goToScreen(MedicationListActivity.class);
        inThis.onClick(R.id.btSymptoms).goToScreen(SymptomListActivity.class);
        inThis.onClick(R.id.btTreatments).goToScreen(TreatmentListActivity.class);
        inThis.onClick(R.id.btDiagnoses).goToScreen(DiagnoseListActivity.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patientTable.closeDatabase();
        appointmentTable.closeDatabase();
    }
}
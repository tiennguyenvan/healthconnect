package com.example.healthconnect;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import java.util.Comparator;
import java.util.List;

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

        updateLatestAppointment();
        updateMedicationClosestToEmpty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        patientTable.closeDatabase();
        appointmentTable.closeDatabase();
    }

    private void updateLatestAppointment() {
        List<Appointment> appointments = appointmentTable.getAll();
        TextView latest = findViewById(R.id.tvAppointmentLatest);
        if (appointments.isEmpty()) {
            inThis.on(R.id.tvAppointmentLatest).setVisibility(View.GONE);
            return;
        }

        // Sort appointments by startDateTime closest to NOW
        appointments.sort(Appointment::sortByStartDateTime);
        Appointment latestAppointment = appointments.get(0);
        Patient patient = patientTable.getById(latestAppointment.getPatient_id());
        String latestAppointmentText = String.format(
                "%s  %s",
                patient != null ? patient.getName() : "Unknown",
                latestAppointment.getFormatStartDateTime()
        );

        inThis.on(R.id.tvAppointmentLatest).setText(latestAppointmentText);
        inThis.on(R.id.tvAppointmentLatest).setVisibility(View.VISIBLE);
    }

    private void updateMedicationClosestToEmpty() {
        List<Medication> medications = medicationTable.getAll();
        if (medications.isEmpty()) {
            inThis.on(R.id.tvMedicationEmpty).setVisibility(View.GONE);
            return;
        }

        // Find medication with lowest stock percentage using Comparator.comparingDouble
        Medication lowestStockMedication = Medication.getClosetToEmpty(medications);

        // Construct and display the closest-to-empty medication string
        String medicationEmptyText = String.format(
                getString(R.string.medication_main_page_status),
                lowestStockMedication.getMedicationName(),
                lowestStockMedication.getStock(),
                lowestStockMedication.getMaxStock()
        );

        inThis.on(R.id.tvMedicationEmpty).setText(medicationEmptyText);
        inThis.on(R.id.tvMedicationEmpty).setVisibility(View.VISIBLE);
    }

}
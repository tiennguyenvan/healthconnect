package com.example.healthconnect.activities.patient;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.models.Treatment;
import com.example.healthconnect.views.SearchRecyclerView;

import java.util.List;
import java.util.Map;

public class PatientProfileActivity extends AppCompatActivity {
    private $ inThis;

    private DbTable<Patient> patientTable;
    private DbTable<Appointment> appointmentTable; // For consultations/appointments
    private DbTable<Symptom> symptomTable;
    private DbTable<Diagnose> diagnoseTable;
    private DbTable<Treatment> treatmentTable;
    private DbTable<Medication> medicationTable;
    private List<Medication> medications;
    private Map<Long, String> medicationIdsNames;
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
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        symptomTable = DbTable.getInstance(this, Symptom.class);
        diagnoseTable = DbTable.getInstance(this, Diagnose.class);
        treatmentTable = DbTable.getInstance(this, Treatment.class);
        medicationTable = DbTable.getInstance(this, Medication.class);
        medications = medicationTable.getAll();
        medicationIdsNames = medicationTable.objectsToIdsNames(medications, Medication::getMedicationName);

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
        loadConsultations();
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
        String patientDetails = getString(R.string.patient_details_format, patient.getHeight(), patient.getWeight(), inThis.formatDateOfBirth(patient.getDateOfBirth()), inThis.formatPhoneNumber(patient.getContactNumber()));
        inThis.on(R.id.tvPatientDetails).setText(patientDetails);
    }

    private void loadConsultations() {
        // Fetch consultations associated with this patient
        List<Appointment> consultations = appointmentTable.getBy(Appointment.columnPatientId(), patientId);

        // Bind consultations to the RecyclerView
        SearchRecyclerView<Appointment> rvConsultations = findViewById(R.id.srPatientConsultations);
        rvConsultations.setInputEnable(false);
        rvConsultations.setItemList(consultations);
        rvConsultations.setItemLayout(R.layout.component_consultation_item); // Bind to the consultation item layout

        // Populate each item in the RecyclerView
        rvConsultations.setOnBindItem((itemView, appointment) -> {
            TextView tvDateTime = itemView.findViewById(R.id.tvConsultationDateTime);
            TextView tvSymptoms = itemView.findViewById(R.id.tvConsultationSymptoms);
            TextView tvDiagnoses = itemView.findViewById(R.id.tvConsultationDiagnoses);
            TextView tvTreatments = itemView.findViewById(R.id.tvConsultationTreatments);

            // Bind consultation date and time
            tvDateTime.setText(appointment.getFormatStartDateTime());


            // Bind diagnoses
            if (appointment.getDiagnoses().isEmpty()) {
//                tvDiagnoses.setVisibility(View.GONE);
                tvDiagnoses.setText(R.string.waiting_diagnoses);
            } else {
//                tvDiagnoses.setVisibility(View.VISIBLE);
                tvDiagnoses.setText(TextUtils.join(", ", diagnoseTable.idsStringToObjectFields(appointment.getDiagnoses(), Diagnose::getName)));
            }


            // Bind symptoms
            if (appointment.getSymptoms().isEmpty()) {
                tvSymptoms.setVisibility(View.GONE);
            } else {
                tvSymptoms.setVisibility(View.VISIBLE);
                tvSymptoms.setText(TextUtils.join(", ", symptomTable.idsStringToObjectFields(appointment.getSymptoms(), Symptom::getName)));
            }


            // Bind treatments
            if (appointment.getTreatments().isEmpty()) {
                tvTreatments.setVisibility(View.GONE);
            } else {
                tvTreatments.setVisibility(View.VISIBLE);
                String treatmentSelectedNames = TextUtils.join("\n- ", treatmentTable.idsStringToObjectFields(appointment.getTreatments(), t -> t.getName(medicationIdsNames)));
                if (!treatmentSelectedNames.isEmpty()) {
                    treatmentSelectedNames = "- " + treatmentSelectedNames;
                }
                tvTreatments.setText(treatmentSelectedNames);
            }
        });

//        rvConsultations.setOnClickItem((appointment) -> {
//            // Open the AppointmentFormActivity for editing the selected consultation
//            inThis.passToScreen(AppointmentFormActivity.class, R.string.key_appointment_id, appointment.getId());
//        });
    }
}

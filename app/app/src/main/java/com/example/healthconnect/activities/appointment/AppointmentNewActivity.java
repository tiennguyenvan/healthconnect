package com.example.healthconnect.activities.appointment;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.activities.patient.PatientProfileActivity;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Patient;

import java.util.List;

public class AppointmentNewActivity extends AppCompatActivity {
    private $ inThis;
    private DbTable<Patient> patientTable;
    private DbTable<Appointment> appointmentTable;
    private long patientId = -1;  // Default to -1 if no patient ID is passed
    private long appointmentId = -1;  // Default to -1 if no appointment ID is passed
    List<Appointment> appointmentList;
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
        inThis.onClick(R.id.btAppointmentListToMain).goToScreen(AppointmentListActivity.class);
        patientTable = DbTable.getInstance(this, Patient.class);
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        appointmentList = appointmentTable.getAll();
        appointmentId = getIntent().getLongExtra(getString(R.string.key_appointment_id), -1);

        patientId = getIntent().getLongExtra(getString(R.string.key_patient_id), -1);
        Patient patient = patientTable.getById(patientId);
        if (patientId != -1) {
            inThis.on(R.id.tvPatientInfo).setText(getString(R.string.appointment_patient_name, patient.getName()));
        }

        inThis.onClick(R.id.btnAddAppointment).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            Appointment appointment = new Appointment();
            appointment.setPatient_id(patient.getId());
            appointment.setDate(inThis.getTextFrom(R.id.etStartDate));
            appointment.setStartTime(inThis.getTextFrom(R.id.etStartTime));
            appointment.setEndTime(inThis.getTextFrom(R.id.etEndTime));

            if (appointmentId == -1) {
                appointmentId = appointmentTable.add(appointment);
                inThis.showToast(getString(R.string.noti_appointment_added, String.valueOf(appointmentId)));
            } else {
                // Update existing patient
                appointmentTable.update(appointmentId, appointment);
                inThis.showToast(getString(R.string.noti_appointment_updated, String.valueOf(appointmentId)));
            }

            // Navigate back to PatientProfileActivity
            inThis.passToScreen(AppointmentListActivity.class);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateInput(R.id.etStartDate, R.id.tvAppointmentDateError);
        isValid &= inThis.validateInput(R.id.etStartTime, R.id.tvAppointmentStartTimeError);
        isValid &= inThis.validateInput(R.id.etEndTime, R.id.tvAppointmentEndTimeError);
        return isValid;
    }

}
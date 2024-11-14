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

import java.util.List;

public class AppointmentUpdateActivity extends AppCompatActivity {
    private $ inThis;
    private DbTable<Appointment> appointmentTable;
    private long appointmentId = -1;
    private long patientId = -1;
    private DbTable<Patient> patientTable;
    List<Appointment> appointments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inThis = $.in(this);
        inThis.onClick(R.id.btUpdAppointmentToMain).goToScreen(AppointmentListActivity.class);

        appointmentTable = DbTable.getInstance(this, Appointment.class);
        patientTable = DbTable.getInstance(this, Patient.class);
        appointments = appointmentTable.getAll();
        appointmentId = getIntent().getLongExtra(getString(R.string.key_appointment_id), -1);
        Appointment appointment = appointmentTable.getById(appointmentId);

        patientId = appointment.getPatient_id();
        Patient patient = patientTable.getById(patientId);

        if (patientId != -1) {
            inThis.on(R.id.patientName).setText(getString(R.string.appointment_patient_name, patient.getName()));
        }


        if (appointmentId != -1) {

            populateFormForEditing(appointmentId);
        }

        inThis.onClick(R.id.btnUpdateAppointment).doAction(() -> {
            if(!validateInputs()) {
                return;
            }
            appointment.setDate(inThis.getTextFrom(R.id.etStartDate));
            appointment.setStartTime(inThis.getTextFrom(R.id.etStartTime));
            appointment.setEndTime(inThis.getTextFrom(R.id.etEndTime));
            appointmentTable.update(appointmentId, appointment);
            appointmentTable.update(appointmentId, appointment);
            inThis.showToast(getString(R.string.noti_appointment_updated, String.valueOf(appointmentId)));
            inThis.passToScreen(AppointmentListActivity.class);
        });

        inThis.onClick(R.id.btnCancelAppointment).doAction(() -> {
            if (appointmentId != -1) {
                appointmentTable.delete(appointmentId); // Assuming delete method exists
                inThis.showToast(getString(R.string.noti_appointment_deleted, String.valueOf(appointmentId)));
                inThis.passToScreen(AppointmentListActivity.class);
            } else {
                inThis.showToast(getString(R.string.noti_no_appointment_found));
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateInput(R.id.etStartDate, R.id.tvAppointmentDateError);
        isValid &= inThis.validateInput(R.id.etStartTime, R.id.tvAppointmentStartTimeError);
        isValid &= inThis.validateInput(R.id.etEndTime, R.id.tvAppointmentEndTimeError);
        return isValid;
    }

    private void populateFormForEditing(long appointmentId) {
        Appointment appointment = appointmentTable.getById(appointmentId);
        if (appointment != null) {
            inThis.on(R.id.btUpdAppointmentToMain).setText(R.string.edit_appointments_header_text);
            inThis.on(R.id.etStartDate).setText(appointment.getDate());
            inThis.on(R.id.etStartTime).setText(appointment.getStartTime());
            inThis.on(R.id.etEndTime).setText(appointment.getEndTime());
        } else {
            inThis.showToast(getString(R.string.noti_no_appointment_found, String.valueOf(appointmentId)));
        }
    }
}

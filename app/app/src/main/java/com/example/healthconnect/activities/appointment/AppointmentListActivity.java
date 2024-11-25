package com.example.healthconnect.activities.appointment;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.MainActivity;
import com.example.healthconnect.R;
import com.example.healthconnect.activities.patient.PatientProfileActivity;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.views.SearchRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class AppointmentListActivity extends AppCompatActivity {
    DbTable<Appointment> appointmentDbTable;
    DbTable<Patient> patientDbTable;
    private List<Appointment> appointments;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        inThis = $.in(this);
        appointmentDbTable = DbTable.getInstance(this, Appointment.class);
        patientDbTable = DbTable.getInstance(this, Patient.class);

        inThis.onClick(R.id.btAppointmentListToAppointmentForm).goToScreen(AppointmentFormActivity.class);
        inThis.onClick(R.id.btAppointmentListToMain).goToScreen(MainActivity.class);

        SearchRecyclerView<Appointment> srvAppointment = findViewById(R.id.rvAppointmentList);
        appointments = appointmentDbTable.getAll();

        appointments.sort(Appointment::sortByStartDateTime);
        srvAppointment.setItemList(appointments);

        srvAppointment.setItemLayout(R.layout.component_appointment_item);

        srvAppointment.setOnBindItem((itemView, appointment) -> {
            TextView apStat = itemView.findViewById(R.id.tvAppointmentStatus);
            TextView apPatient = itemView.findViewById(R.id.tvAppointmentPatientName);
            TextView apStart = itemView.findViewById(R.id.tvAppointmentStartTime);
            apPatient.setText(patientDbTable.getById(appointment.getPatient_id()).getName());
            apStart.setText(appointment.getFormatStartDateTime());
            appointment.applyStatusOnTV(itemView, apStat);
        });

        srvAppointment.setOnClickItem((appointment -> {
            inThis.passToScreen(AppointmentFormActivity.class, getString(R.string.key_appointment_id), appointment.getId());
//            inThis.passToScreen(PatientProfileActivity.class, R.string.key_appointment_id, appointment.getId(), R.string.key_patient_id, appointment.getPatient_id());
        }));


        srvAppointment.setOnSearch(query -> {

            List<Patient> matchPatients = patientDbTable.getBy(Patient.columnName(), query);
            if (matchPatients.isEmpty()) {
                return new ArrayList<Appointment>();
            }
            List<Long> matchPatientIds = patientDbTable.objectsToFields(matchPatients, Patient::getId);
            List<Appointment> matchedAppointments = appointmentDbTable.searchByColumnInValues(Appointment.columnPatientId(), matchPatientIds);
            matchedAppointments.sort(Appointment::sortByStartDateTime);
            return appointments;
        });
    }
}

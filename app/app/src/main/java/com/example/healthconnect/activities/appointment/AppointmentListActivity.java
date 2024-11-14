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
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.views.SearchRecyclerView;

import java.util.List;

public class AppointmentListActivity extends AppCompatActivity {
    private $ inThis;
    private DbTable<Appointment> appointmentTable;
    private DbTable<Patient> patientTable;

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
        inThis.onClick(R.id.btMedicationListToMain).goToScreen(MainActivity.class);
        inThis.onClick(R.id.bt_ToAppointmentForm).goToScreen(PatientListForAppointmentActivity.class);

        inThis = $.in(this);
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        patientTable = DbTable.getInstance(this, Patient.class);

        SearchRecyclerView<Appointment> appointmentSearch = findViewById(R.id.rvAppointmentList);
        appointmentSearch.setItemList(appointmentTable.getAll());
        appointmentSearch.setItemLayout(R.layout.component_appointment_item);
        appointmentSearch.setOnBindItem((itemView, appointment) -> {
            TextView tvAppointmentDetails = itemView.findViewById(R.id.tvAppointmentDetails);
            Patient patient = patientTable.getById(appointment.getPatient_id());

            String appointmentDetails = patient.getName() + ", " + appointment.getDate() + " | " + appointment.getStartTime() + " - " + appointment.getEndTime();
            tvAppointmentDetails.setText(appointmentDetails);
        });

        appointmentSearch.setOnClickItem((appointment -> {
            inThis.passToScreen(AppointmentUpdateActivity.class, getString(R.string.key_appointment_id), appointment.getId());
        }));
        appointmentSearch.setOnSearch(query -> appointmentTable.searchBy(Appointment.columnAppointmentName(), query));
    }
}
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
            TextView tvMedicationAppointmentName = itemView.findViewById(R.id.tvAppointmentName);
            Patient patient = patientTable.getById(appointment.getPatient_id());
            tvMedicationAppointmentName.setText(patient.getName());
        });
        appointmentSearch.setOnClickItem((appointment -> {
            inThis.passToScreen(AppointmentUpdateActivity.class);
        }));
        appointmentSearch.setOnSearch(query -> appointmentTable.searchBy(Medication.columnMedicationName(), query));



        /*
        // Initialize RecyclerView and Adapter
        rvAppointmentList = findViewById(R.id.rvAppointmentList);
        rvAppointmentList.setLayoutManager(new LinearLayoutManager(this));

        patientTable = DbTable.getInstance(this, Patient.class);
        patientList = patientTable.getAll();
        // I need to figure out how to get just patient name and appointment date (can be null if no appointments for patient)
        // not sure about this part, if list shows all the patients, or if appears everyone but dates are not displayed if no appointment

        /*
        * appointmentlist
        * app.getPatientId
        * */

//        appointmentListRvAdapter = new PatientListRvAdapter(patientList, patient -> {
//            inThis.passToScreen(AppointmentUpdateActivity.class);
//        });
//        rvAppointmentList.setAdapter(appointmentListRvAdapter);
//
//        // Initialize SearchView
//        searchView = findViewById(R.id.sVPatientList);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // Perform final search when the user submits the query
//                filterPatients(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // Filter as the user types
//                filterPatients(newText);
//                return true;
//            }
//        });
    }
    private void filterPatients(String query) {
        List<Patient> filteredList = patientTable.searchBy(Patient.columnName(), query);
//        appointmentListRvAdapter.updateList(filteredList);
    }

//    */
//}
}
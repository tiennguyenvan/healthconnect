package com.example.healthconnect.activities.appointment;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthconnect.MainActivity;
import com.example.healthconnect.R;
import com.example.healthconnect.activities.patient.PatientFormActivity;
import com.example.healthconnect.activities.patient.PatientListRvAdapter;
import com.example.healthconnect.activities.patient.PatientProfileActivity;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Patient;

import java.util.List;

public class PatientListForAppointment extends AppCompatActivity {
    private $ inThis;
    private RecyclerView rvPatientList;
    private PatientListRvAdapter patientListRvAdapter;
    private List<Patient> patientList;

    private SearchView searchView;
    private DbTable<Patient> patientTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_list_for_appointment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        inThis.onClick(R.id.btBackToMain).goToScreen(appointmentList.class);

        // Initialize RecyclerView and Adapter
        rvPatientList = findViewById(R.id.rvPatientList);
        rvPatientList.setLayoutManager(new LinearLayoutManager(this));

        patientTable = DbTable.getInstance(this, Patient.class);
        patientList = patientTable.getAll();

        patientListRvAdapter = new PatientListRvAdapter(patientList, patient -> {
            inThis.passToScreen(NewAppointment.class, R.string.key_patient_id, patient.getId());
        });
        rvPatientList.setAdapter(patientListRvAdapter);

        // Initialize SearchView
        searchView = findViewById(R.id.sVPatientList);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform final search when the user submits the query
                filterPatients(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter as the user types
                filterPatients(newText);
                return true;
            }
        });
    }

    private void filterPatients(String query) {
        List<Patient> filteredList = patientTable.searchBy(Patient.columnName(), query);
        patientListRvAdapter.updateList(filteredList);
    }
}

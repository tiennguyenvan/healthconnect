package com.example.healthconnect;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatientListActivity extends AppCompatActivity {
    private $ inThis;
    private RecyclerView rvPatientList;
    private PatientAdapter patientAdapter;
    private List<Patient> patientList;
    private DatabaseHelper dbHelper;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inThis = $.in(this);
        inThis.onClick(R.id.btBackToMain).goToScreen(MainActivity.class);
        inThis.onClick(R.id.btToPatientForm).goToScreen(PatientFormActivity.class);


        // Initialize RecyclerView and Adapter
        rvPatientList = findViewById(R.id.rvPatientList);
        rvPatientList.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = DatabaseHelper.getInstance(this);
        patientList = dbHelper.getAllPatients();

        patientAdapter = new PatientAdapter(patientList, patient -> {
            // Handle item click to navigate to PatientProfileActivity
            Intent intent = new Intent(this, PatientProfileActivity.class);
            intent.putExtra("PATIENT_ID", patient.getId());
            startActivity(intent);
        });
        rvPatientList.setAdapter(patientAdapter);

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
        List<Patient> filteredList = dbHelper.searchPatients(query);
        patientAdapter.updateList(filteredList);
    }
}
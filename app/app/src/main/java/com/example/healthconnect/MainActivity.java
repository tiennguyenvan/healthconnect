package com.example.healthconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Patient;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
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
        dbHelper = DatabaseHelper.getInstance(this);
        inThis = $.in(this);
        inThis.onClick(R.id.btPatients).goToScreen(PatientListActivity.class);
        DbTable patientTable = DbTable.getInstance(this, Patient.class);


        //
//        Patient p = new Patient();
//        p.setName("TimNguyen");
//        p.setHeight(175);
//        p.setWeight(70);
//        p.setDateOfBirth("1985-07-15");
//        p.setContactNumber("0987654321");
//        patientTable.add(p);
        inThis.on(R.id.tvPatientCount).setText(String.valueOf(patientTable.size()));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.closeDatabase();
    }
}
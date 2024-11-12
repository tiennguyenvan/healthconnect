package com.example.healthconnect.activities.medication;

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
import com.example.healthconnect.views.SearchRecyclerView;

public class MedicationListActivity extends AppCompatActivity {
    DbTable<Medication> medicationDbTable;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        inThis = $.in(this);
        medicationDbTable = DbTable.getInstance(this, Medication.class);

        inThis.onClick(R.id.btToMedicationForm).goToScreen(MedicationFormActivity.class);
        inThis.onClick(R.id.btBackToMain).goToScreen(MainActivity.class);

        SearchRecyclerView<Medication> medicationSearch = findViewById(R.id.medicationSearchList);
        medicationSearch.setItemList(medicationDbTable.getAll());
        medicationSearch.setItemLayout(R.layout.component_medication_item);
        medicationSearch.setOnBindItem((itemView, medication) -> {
            TextView tvMedicationName = itemView.findViewById(R.id.tvMedicationName);
            tvMedicationName.setText(medication.getMedicationName());
        });
        medicationSearch.setOnClickItem((medication -> {
            inThis.passToScreen(MedicationFormActivity.class, getString(R.string.key_medication_id), medication.getId());
        }));
        medicationSearch.setOnSearch(query -> medicationDbTable.searchBy(Medication.columnMedicationName(), query));

    }
}
package com.example.healthconnect.activities.treatment;

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
import com.example.healthconnect.models.Treatment;
import com.example.healthconnect.views.SearchRecyclerView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TreatmentListActivity extends AppCompatActivity {
    DbTable<Treatment> treatmentDbTable;
    DbTable<Medication> medicationDbTable;
    private $ inThis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treatment_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        treatmentDbTable = DbTable.getInstance(this, Treatment.class);
        medicationDbTable = DbTable.getInstance(this, Medication.class);
        List<Medication>  allMedications = medicationDbTable.getAll();
        Map<Long, String> medicationNames = Medication.mapToIdsNames(allMedications);

        inThis.onClick(R.id.btTreatmentListToTreatmentForm).goToScreen(TreatmentFormActivity.class);
        inThis.onClick(R.id.btTreatmentListToMain).goToScreen(MainActivity.class);

        SearchRecyclerView<Treatment> srvTreatment = findViewById(R.id.srvTreatment);
        srvTreatment.setItemList(treatmentDbTable.getAll());
        srvTreatment.setItemLayout(R.layout.component_treatment_item);
        srvTreatment.setOnBindItem((itemView, treatment) -> {
            ((TextView) itemView.findViewById(R.id.tvTreatmentName)).setText(treatment.getName(medicationNames));
        });
        srvTreatment.setOnClickItem((treatment -> {
            inThis.passToScreen(TreatmentFormActivity.class, getString(R.string.key_treatment_id), treatment.getId());
        }));
        srvTreatment.setOnSearch(query -> {

            List<Medication> matchedMedications = medicationDbTable.searchBy(Medication.columnMedicationName(), query);
            List<Long> medicationIds = matchedMedications.stream()
                    .map(Medication::getId)
                    .collect(Collectors.toList());

            return treatmentDbTable.searchByColumnInValues(Treatment.columnMedicationId(), medicationIds);
        });


    }
}

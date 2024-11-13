package com.example.healthconnect.activities.symptom;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.views.LabeledInputField;

import java.util.List;

public class SymptomFormActivity extends AppCompatActivity {
    private DbTable<Symptom> symptomTable;
    private $ inThis;
    private long symptomId = -1;
    List<Symptom> symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_symptom_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        symptomTable = DbTable.getInstance(this, Symptom.class);
        symptomId = getIntent().getLongExtra(getString(R.string.key_symptom_id), -1);

        if (symptomId != -1) {
            populateFormForEditing(symptomId);
        }

        inThis.onClick(R.id.btSymptomFormToSymptomList).goToScreen(SymptomListActivity.class);
        inThis.onClick(R.id.btSubmitSymptom).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            // Insert or update symptom data
            Symptom symptom = new Symptom();
            symptom.setName(inThis.getTextFrom(R.id.etSymptomName));

            if (symptomId == -1) {
                symptomId = symptomTable.add(symptom);
                inThis.showToast(getString(R.string.noti_symptom_added, String.valueOf(symptomId)));
            } else {
                // Update existing symptom
                symptomTable.update(symptomId, symptom);
                inThis.showToast(getString(R.string.noti_symptom_updated, String.valueOf(symptomId)));
            }

            inThis.passToScreen(SymptomListActivity.class, R.string.key_symptom_id, symptomId);
        });

    }

    private boolean validateInputs() {
        return inThis.validateLabeledInput(R.id.etSymptomName);
    }

    private void populateFormForEditing(long symptomId) {
        Symptom symptom = symptomTable.getById(symptomId);
//        inThis.log("Max Stock " + symptom.getMaxStock() + " from ID " + symptomId);
        if (symptom != null) {
            inThis.on(R.id.btSymptomFormToSymptomList).setText(R.string.edit_symptom_header_text);
            inThis.on(R.id.etSymptomName).setText(symptom.getName());
            inThis.on(R.id.btSubmitSymptom).setText(R.string.update_symptom);
        } else {
            inThis.showToast(getString(R.string.noti_no_symptom_found, String.valueOf(symptomId)));
            ;
        }
    }
}
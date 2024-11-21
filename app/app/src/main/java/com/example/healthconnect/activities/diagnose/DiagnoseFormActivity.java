package com.example.healthconnect.activities.diagnose;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.models.Treatment;
import com.example.healthconnect.views.SelectableAutocompleteView;

import java.util.List;
import java.util.Map;

public class DiagnoseFormActivity extends AppCompatActivity {
    private DbTable<Diagnose> diagnoseTable;
    private DbTable<Symptom> symptomTable;
    private DbTable<Medication> medicationTable;
    private DbTable<Treatment> treatmentTable;
    private $ inThis;
    private long diagnoseId = -1;
    private SelectableAutocompleteView symptomPicker, treatmentPicker;
    private Map<Long, String> symptomNames, treatmentNames, medicationNames;
    private List<Symptom> symptoms;
    private List<Treatment> treatments;
    private List<Medication> medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diagnose_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        diagnoseTable = DbTable.getInstance(this, Diagnose.class);

        symptomTable = DbTable.getInstance(this, Symptom.class);
        symptoms = symptomTable.getAll();

        treatmentTable = DbTable.getInstance(this, Treatment.class);
        treatments = treatmentTable.getAll();

        medicationTable = DbTable.getInstance(this, Medication.class);
        medications = medicationTable.getAll();
        medicationNames = Medication.objectsToIdsNames(medications);

        symptomPicker = findViewById(R.id.saDiagnoseSymptoms);
        symptomPicker.setSuggestions(symptomTable.objectsToFields(symptoms, Symptom::getName));

        treatmentPicker = findViewById(R.id.saDiagnoseTreatments);
        treatmentPicker.setSuggestions(treatmentTable.objectsToFields(treatments, (treatment) -> treatment.getName(medicationNames)));

        diagnoseId = getIntent().getLongExtra(getString(R.string.key_diagnose_id), -1);
        if (diagnoseId != -1) {
            populateFormForEditing(diagnoseId);
        }

        inThis.onClick(R.id.btBackToDiagnoseList).goToScreen(DiagnoseListActivity.class);
        inThis.onClick(R.id.btSubmitDiagnose).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            // Insert or update diagnosis data
            Diagnose diagnose = new Diagnose();
            diagnose.setName(inThis.getTextFrom(R.id.etDiagnoseName));
            diagnose.setSymptomIds(symptomTable.objectFieldsToIdsString(
                    symptoms, symptomPicker.getSelectedItems(), Symptom::getName
            ));
            diagnose.setTreatmentIds(treatmentTable.objectFieldsToIdsString(
                    treatments, treatmentPicker.getSelectedItems(), (treatment) -> treatment.getName(medicationNames)
            ));

            if (diagnoseId == -1) {
                diagnoseId = diagnoseTable.add(diagnose);
                inThis.showToast(getString(R.string.noti_diagnose_added, String.valueOf(diagnoseId)));
            } else {
                diagnoseTable.update(diagnoseId, diagnose);
                inThis.showToast(getString(R.string.noti_diagnose_updated, String.valueOf(diagnoseId)));
            }

            inThis.passToScreen(DiagnoseListActivity.class, R.string.key_diagnose_id, diagnoseId);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateLabeledInput(R.id.etDiagnoseName);

        if (symptomPicker.getSelectedItems().isEmpty()) {
            symptomPicker.setError(getString(R.string.warning_invalid_symptoms));
            isValid = false;
        } else {
            symptomPicker.clearError();
        }
        if (treatmentPicker.getSelectedItems().isEmpty()) {
            treatmentPicker.setError(getString(R.string.warning_invalid_treatments));
            isValid = false;
        }
        else {
            treatmentPicker.clearError();
        }

        List<Long> selectedMedicationIds = treatmentTable.objectFieldsToObjectFields(treatments, treatmentPicker.getSelectedItems(),
                (treatment) -> treatment.getName(medicationNames),
                Treatment::getMedicationId
        );
        String conflictWarning = Medication.getConflictWarningFromIds(medications, selectedMedicationIds);

        if (!conflictWarning.isEmpty() && !conflictWarning.isBlank()) {
            isValid = false;
            inThis.log(conflictWarning);
            treatmentPicker.setError(conflictWarning);
        } else {
            treatmentPicker.clearError();
        }

        return isValid;
    }

    private void populateFormForEditing(long diagnoseId) {
        Diagnose diagnose = diagnoseTable.getById(diagnoseId);
        if (diagnose != null) {
            inThis.on(R.id.btBackToDiagnoseList).setText(R.string.edit_diagnose_header_text);
            inThis.on(R.id.etDiagnoseName).setText(diagnose.getName());

            List<String> selectedSymptoms = symptomTable.idsStringToObjectFields(
                    diagnose.getSymptomIds(), Symptom::getName
            );
            List<String> selectedTreatments = treatmentTable.idsStringToObjectFields(
                    diagnose.getTreatmentIds(), (treatment) -> treatment.getName(medicationNames)
            );

            symptomPicker.setSelectedItems(selectedSymptoms);
            treatmentPicker.setSelectedItems(selectedTreatments);

            inThis.on(R.id.btSubmitDiagnose).setText(R.string.update_diagnose);
        } else {
            inThis.showToast(getString(R.string.noti_no_diagnose_found, String.valueOf(diagnoseId)));
        }
    }
}

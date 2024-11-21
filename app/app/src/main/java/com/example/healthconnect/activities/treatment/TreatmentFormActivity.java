package com.example.healthconnect.activities.treatment;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Treatment;
import com.example.healthconnect.views.SelectableAutocompleteView;

import java.util.List;
import java.util.Map;

public class TreatmentFormActivity extends AppCompatActivity {
    private DbTable<Treatment> treatmentTable;
    private DbTable<Medication> medicationTable;
    private $ inThis;
    private long treatmentId = -1;
    private SelectableAutocompleteView medicationPicker;
    private Map<Long, String> medicationNames;
    private List<Medication> medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_treatment_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        treatmentTable = DbTable.getInstance(this, Treatment.class);
        medicationTable = DbTable.getInstance(this, Medication.class);
        medications = medicationTable.getAll();
        medicationNames = Medication.objectsToIdsNames(medications);

        treatmentId = getIntent().getLongExtra(getString(R.string.key_treatment_id), -1);

        medicationPicker = findViewById(R.id.saTreatmentMedicationName);
        medicationPicker.setAllowOneItem(true);
        // Set medication suggestions in the autocomplete picker
        medicationPicker.setSuggestions(medicationTable.objectsToFields(medications, Medication::getMedicationName));

        if (treatmentId != -1) {
            populateFormForEditing(treatmentId);
        }

        inThis.onClick(R.id.btBackToTreatmentList).goToScreen(TreatmentListActivity.class);
        inThis.onClick(R.id.btSubmitTreatment).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            // Insert or update treatment data
            Treatment treatment = new Treatment();
            treatment.setDose(inThis.getTextFrom(R.id.etTreatmentDose));
            treatment.setDuration(inThis.getTextFrom(R.id.etTreatmentDuration));
            treatment.setNote(inThis.getTextFrom(R.id.etTreatmentNote));

            String selectedMedicationName = medicationPicker.getSelectedItems().isEmpty()
                    ? null
                    : medicationPicker.getSelectedItems().get(0);

            Long medicationId = medicationNames.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(selectedMedicationName))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (medicationId != null) {
                treatment.setMedicationId(medicationId);
            } else {
                inThis.showToast(getString(R.string.warning_invalid_medication));
                return;
            }

            if (treatmentId == -1) {
                treatmentId = treatmentTable.add(treatment);
                inThis.showToast(getString(R.string.noti_treatment_added, String.valueOf(treatmentId)));
            } else {
                treatmentTable.update(treatmentId, treatment);
                inThis.showToast(getString(R.string.noti_treatment_updated, String.valueOf(treatmentId)));
            }

            inThis.passToScreen(TreatmentListActivity.class, R.string.key_treatment_id, treatmentId);
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateLabeledInput(R.id.etTreatmentDose, "\\d+x/(day|week)");
        isValid &= inThis.validateLabeledInput(R.id.etTreatmentDuration, "\\d+ (days|weeks)");
        isValid &= inThis.validateLabeledInput(R.id.etTreatmentNote);

        if (medicationPicker.getSelectedItems().isEmpty()) {
            medicationPicker.setError(getString(R.string.warning_invalid_medication));
            return false;
        }
        medicationPicker.clearError();
        return isValid;
    }

    private void populateFormForEditing(long treatmentId) {
        Treatment treatment = treatmentTable.getById(treatmentId);
        if (treatment != null) {
            inThis.on(R.id.btBackToTreatmentList).setText(R.string.edit_treatment_header_text);
            inThis.on(R.id.etTreatmentDose).setText(treatment.getDose());
            inThis.on(R.id.etTreatmentDuration).setText(treatment.getDuration());
            inThis.on(R.id.etTreatmentNote).setText(treatment.getNote());

            String medicationName = medicationNames.get(treatment.getMedicationId());
            if (medicationName != null) {
                medicationPicker.setSelectedItems(List.of(medicationName));
            }
            inThis.on(R.id.btSubmitTreatment).setText(R.string.update_treatment);
        } else {
            inThis.showToast(getString(R.string.noti_no_treatment_found, String.valueOf(treatmentId)));
        }
    }
}

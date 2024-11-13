package com.example.healthconnect.activities.medication;

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
import com.example.healthconnect.views.LabeledInputField;
import com.example.healthconnect.views.SelectableAutocompleteView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MedicationFormActivity extends AppCompatActivity {
    private DbTable<Medication> medicationTable;
    private $ inThis;
    private long medicationId = -1;
    SelectableAutocompleteView conflictPicker;
    List<Medication> medications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medication_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);
        medicationTable = DbTable.getInstance(this, Medication.class);
        medications = medicationTable.getAll();
        medicationId = getIntent().getLongExtra(getString(R.string.key_medication_id), -1);

        conflictPicker = findViewById(R.id.saMedicationConflicts);


        conflictPicker.setSuggestions(medicationTable.objectsToFields(medications, Medication::getMedicationName));

        if (medicationId != -1) {
            populateFormForEditing(medicationId);
        }

        inThis.onClick(R.id.btBackToMedicationList).goToScreen(MedicationListActivity.class);
        inThis.onClick(R.id.btSubmitMedication).doAction(() -> {
            if (!validateInputs()) {
                return;
            }

            // Insert or update medication data
            Medication medication = new Medication();
            medication.setMedicationName(inThis.getTextFrom(R.id.etMedicationName));
            medication.setStock(inThis.getDoubleFrom(R.id.etMedicationStock));
            medication.setMaxStock(inThis.getDoubleFrom(R.id.etMedicationMaxStock));
            medication.setConflicts(medicationTable.objectFieldsToIdsString(
                    medications, conflictPicker.getSelectedItems(), Medication::getMedicationName
            ));

            if (medicationId == -1) {
                medicationId = medicationTable.add(medication);
                inThis.showToast(getString(R.string.noti_medication_added, String.valueOf(medicationId)));
            } else {
                // Update existing medication
                medicationTable.update(medicationId, medication);
                inThis.showToast(getString(R.string.noti_medication_updated, String.valueOf(medicationId)));
            }

            inThis.passToScreen(MedicationListActivity.class, R.string.key_medication_id, medicationId);
        });

    }

    private boolean validateInputs() {
        boolean isValid = true;
        isValid &= inThis.validateLabeledInput(R.id.etMedicationName);
        isValid &= inThis.validateLabeledInput(R.id.etMedicationStock);
        isValid &= inThis.validateLabeledInput(R.id.etMedicationMaxStock);
        if (isValid) {
            double stock = inThis.getDoubleFrom(R.id.etMedicationStock);
            double maxStock = inThis.getDoubleFrom(R.id.etMedicationMaxStock);
            if (stock > maxStock) {
                ((LabeledInputField) findViewById(R.id.etMedicationMaxStock)).setError(getString(R.string.err_max_stock_must_be_larger_than_stock));
                return false;
            }
        }
        return isValid;
    }

    private void populateFormForEditing(long medicationId) {
        Medication medication = medicationTable.getById(medicationId);
//        inThis.log("Max Stock " + medication.getMaxStock() + " from ID " + medicationId);
        if (medication != null) {
            inThis.on(R.id.btBackToMedicationList).setText(R.string.edit_medication_header_text);
            inThis.on(R.id.etMedicationName).setText(medication.getMedicationName());
            inThis.on(R.id.etMedicationStock).setText(String.valueOf(medication.getStock()));
            inThis.on(R.id.etMedicationMaxStock).setText(String.valueOf(medication.getMaxStock()));
            conflictPicker.setSelectedItems(medicationTable.idsStringToObjectFields(medication.getConflicts(), Medication::getMedicationName));
            inThis.on(R.id.btSubmitMedication).setText(R.string.update_medication);
        } else {
            inThis.showToast(getString(R.string.noti_no_medication_found, String.valueOf(medicationId)));
            ;
        }
    }
}
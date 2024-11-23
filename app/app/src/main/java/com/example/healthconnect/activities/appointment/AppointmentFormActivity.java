package com.example.healthconnect.activities.appointment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthconnect.R;
import com.example.healthconnect.controllers.$;
import com.example.healthconnect.controllers.DbTable;
import com.example.healthconnect.models.Appointment;
import com.example.healthconnect.models.AppointmentStatus;
import com.example.healthconnect.models.Diagnose;
import com.example.healthconnect.models.Medication;
import com.example.healthconnect.models.Patient;
import com.example.healthconnect.models.Symptom;
import com.example.healthconnect.models.Treatment;
import com.example.healthconnect.views.LabeledInputField;
import com.example.healthconnect.views.SelectableAutocompleteView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppointmentFormActivity extends AppCompatActivity {

    private DbTable<Appointment> appointmentTable;
    private DbTable<Patient> patientTable;
    private DbTable<Diagnose> diagnoseTable;
    private DbTable<Symptom> symptomTable;
    private DbTable<Treatment> treatmentTable;
    private DbTable<Medication> medicationTable;

    private $ inThis;
    private long appointmentId = -1;

    private SelectableAutocompleteView patientPicker, diagnosisPicker, symptomPicker, treatmentPicker;
    private LabeledInputField etStartDate, etStartTime, etDuration;
    private TextView tvMasterError;
    private Button btCancel;

    private List<Patient> patients;
    private List<Diagnose> diagnoses;
    private List<Symptom> symptoms;
    private List<Treatment> treatments;
    private List<Medication> medications;

    // For mapping IDs to names
    private Map<Long, String> patientIdsNames;
    private Map<Long, String> diagnoseIdsNames;
    private Map<Long, Diagnose> diagnoseIdsObjects;
    private Map<Long, String> symptomIdsNames;
    private Map<Long, String> treatmentIdsNames;
    private Map<Long, String> medicationIdsNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_appointment_form);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inThis = $.in(this);

        // Initialize DbTables
        appointmentTable = DbTable.getInstance(this, Appointment.class);
        patientTable = DbTable.getInstance(this, Patient.class);
        diagnoseTable = DbTable.getInstance(this, Diagnose.class);
        symptomTable = DbTable.getInstance(this, Symptom.class);
        treatmentTable = DbTable.getInstance(this, Treatment.class);
        medicationTable = DbTable.getInstance(this, Medication.class);

        // Load data
        patients = patientTable.getAll();
        diagnoses = diagnoseTable.getAll();
        symptoms = symptomTable.getAll();
        treatments = treatmentTable.getAll();
        medications = medicationTable.getAll();

        // Map IDs to Names using the new methods
        patientIdsNames = patientTable.objectsToIdsNames(patients, Patient::getName);
        diagnoseIdsNames = diagnoseTable.objectsToIdsNames(diagnoses, Diagnose::getName);
        diagnoseIdsObjects = diagnoseTable.objectsToIdsObjects(diagnoses);
        symptomIdsNames = symptomTable.objectsToIdsNames(symptoms, Symptom::getName);
        medicationIdsNames = medicationTable.objectsToIdsNames(medications, Medication::getMedicationName);
        treatmentIdsNames = treatmentTable.objectsToIdsNames(treatments, t -> t.getName(medicationIdsNames));

        patientPicker = findViewById(R.id.saAppointmentPatient);
        diagnosisPicker = findViewById(R.id.saAppointmentDiagnoses);
        symptomPicker = findViewById(R.id.saAppointmentSymptoms);
        treatmentPicker = findViewById(R.id.saAppointmentTreatments);
        etStartDate = findViewById(R.id.etAppointmentStartDate);
        etStartTime = findViewById(R.id.etAppointmentStartTime);
        etDuration = findViewById(R.id.etAppointmentDuration);
        tvMasterError = findViewById(R.id.tvMasterAppointmentError);
        btCancel = findViewById(R.id.btCancelAppointment);

        patientPicker.setAllowOneItem(true);
        patientPicker.setSuggestions(new ArrayList<>(patientIdsNames.values()));
        diagnosisPicker.setSuggestions(new ArrayList<>(diagnoseIdsNames.values()));
        symptomPicker.setSuggestions(new ArrayList<>(symptomIdsNames.values()));
        treatmentPicker.setSuggestions(treatmentTable.objectsToFields(treatments, t -> t.getName(medicationIdsNames)));

        appointmentId = getIntent().getLongExtra(getString(R.string.key_appointment_id), -1);

        if (appointmentId != -1) {
            populateFormForEditing(appointmentId);
        }

        inThis.onClick(R.id.btBackToAppointmentList).goToScreen(AppointmentListActivity.class);

        inThis.onClick(R.id.etAppointmentStartDate).pickFutureDate();
        inThis.onClick(R.id.etAppointmentStartTime).pickTime();

        inThis.onClick(R.id.btSubmitAppointment).doAction(() -> {

            Appointment appointment = validateInputsAndCreateAppointment();
            if (appointment == null) {
                return;
            }

            if (appointmentId == -1) {
                appointmentId = appointmentTable.add(appointment);
                inThis.showToast(getString(R.string.noti_appointment_added, String.valueOf(appointmentId)));
            } else {
                appointmentTable.update(appointmentId, appointment);
                inThis.showToast(getString(R.string.noti_appointment_updated, String.valueOf(appointmentId)));
            }

            inThis.passToScreen(AppointmentListActivity.class, R.string.key_appointment_id, appointmentId);
        });

        // Handle treatment suggestions based on selected diagnoses
        diagnosisPicker.setOnChange(this::autoUpdateTreatments);

        // Show or hide cancel button based on appointment status
        if (appointmentId != -1) {
            Appointment appointment = appointmentTable.getById(appointmentId);
            if (appointment != null) {
                AppointmentStatus status = appointment.getStatus(LocalDateTime.now());
                if (status == AppointmentStatus.UPCOMING) {
                    btCancel.setVisibility(View.VISIBLE);
                    btCancel.setOnClickListener(v -> {
                        appointmentTable.delete(appointmentId);
                        inThis.showToast(getString(R.string.noti_appointment_deleted, String.valueOf(appointmentId)));
                        inThis.goToScreen(AppointmentListActivity.class);
                    });
                }
            }
        }

        // Check for treatment conflicts
        treatmentPicker.setOnChange(this::checkTreatmentConflicts);
    }

    private void autoUpdateTreatments(List<String> selectedDiagnoses) {
        List<String> selectedTreatments = treatmentPicker.getSelectedItems();
        // Get selected diagnoses
        List<Long> selectedDiagnoseIds = diagnoseTable.objectFieldsToObjectFields(diagnoses, selectedDiagnoses, Diagnose::getName, Diagnose::getId);

        // Get treatments associated with selected diagnoses
        Set<String> uniqueTreatmentNames = new HashSet<>(selectedTreatments);
        for (Long diagnoseId : selectedDiagnoseIds) {
            Diagnose diagnose = diagnoseIdsObjects.get(diagnoseId);
            if (diagnose == null) continue;
            List<String> treatmentNames= treatmentTable.idsStringToObjectFields(diagnose.getTreatmentIds(), t -> t.getName(medicationIdsNames));
            uniqueTreatmentNames.addAll(treatmentNames);
        }

        treatmentPicker.setSuggestions(uniqueTreatmentNames.stream().toList());
    }

    private void checkTreatmentConflicts(List<String> selectedTreatmentNames) {

        List<Long> selectedMedicationIds = treatmentTable.objectFieldsToObjectFields(treatments, selectedTreatmentNames, t -> t.getName(medicationIdsNames), Treatment::getMedicationId);

        String conflictWarning = Medication.getConflictWarningFromIds(medications, selectedMedicationIds);
        if (!conflictWarning.isEmpty()) {
            treatmentPicker.setError(conflictWarning);
        } else {
            treatmentPicker.clearError();
        }
    }

    private boolean hasOverlappingAppointments(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // Get all appointments
        List<Appointment> appointments = appointmentTable.getAll();

        for (Appointment existingAppointment : appointments) {
            if (appointmentId != -1 && existingAppointment.getId() == appointmentId) {
                continue;
            }

            LocalDateTime existingStart = LocalDateTime.parse(existingAppointment.getStartDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime existingEnd = LocalDateTime.parse(existingAppointment.getEndDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            if (startDateTime.isBefore(existingEnd) && endDateTime.isAfter(existingStart)) {
                return true;
            }
        }
        return false;
    }

    private Appointment validateInputsAndCreateAppointment() {
        boolean isValid = true;
        Appointment appointment;
        if (appointmentId != -1) {
            appointment = appointmentTable.getById(appointmentId);
            if (appointment == null) {
                return null;
            }
        } else {
            appointment = new Appointment();
        }

        tvMasterError.setText("");
        patientPicker.clearError();
        etStartDate.clearError();
        etStartTime.clearError();
        etDuration.clearError();

        ///// patient check
        if (patientPicker.getSelectedItems().isEmpty()) {
            patientPicker.setError(getString(R.string.warning_invalid_patient));
            isValid = false;
        } else {
            String selectedPatientName = patientPicker.getSelectedItems().get(0);
            Long patientId = null;
            for (Map.Entry<Long, String> entry : patientIdsNames.entrySet()) {
                if (entry.getValue().equals(selectedPatientName)) {
                    patientId = entry.getKey();
                    break;
                }
            }

            if (patientId == null) {
                patientPicker.setError(getString(R.string.warning_invalid_patient));
                isValid = false;
            } else {
                appointment.setPatient_id(patientId);
            }
        }

        // Date time check
        String startDateStr = etStartDate.getText();
        if (startDateStr.isEmpty()) {
            etStartDate.setError(getString(R.string.warning_field_required));
            isValid = false;
        }
        // Validate start time
        String startTimeStr = etStartTime.getText();
        if (startTimeStr.isEmpty()) {
            etStartTime.setError(getString(R.string.warning_field_required));
            isValid = false;
        }

        // Parse and validate start date and time
        LocalDateTime startDateTime = null;
        if (!startDateStr.isEmpty() && !startTimeStr.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                startDateTime = LocalDateTime.parse(startDateStr + "T" + startTimeStr, formatter);
            } catch (Exception e) {
                tvMasterError.setText(R.string.warning_invalid_date_time_format);
                isValid = false;
            }
        }
        if (startDateTime != null) {
            if (appointmentId == -1 && startDateTime.isBefore(LocalDateTime.now())) {
                startDateTime = null;
                tvMasterError.setText(R.string.warning_start_date_time_is_from_the_past);
                isValid = false;
            } else {
                appointment.setStartDateTime(startDateTime.toLocalDate().toString());
            }
        }

        // Validate duration
        int durationMinutes = 0;
        try {
            durationMinutes = Integer.parseInt(etDuration.getText());
        } catch (NumberFormatException e) {
            etDuration.setError(getString(R.string.warning_invalid_duration));
            isValid = false;
        }
        LocalDateTime endDateTime = null;
        if (startDateTime != null && durationMinutes > 0) {
            endDateTime = startDateTime.plusMinutes(durationMinutes);
        }

        if (endDateTime != null) {
            if ((hasOverlappingAppointments(startDateTime, endDateTime))) {
                tvMasterError.setError(getString(R.string.warning_overlapping_appointment));
                isValid = false;
            } else {
                appointment.setStartDateTime(startDateTime.toLocalDate().toString());
            }
        }

        if (appointmentId != -1) {

            // Only set Diagnoses and Treatments if status is CONSULTING or FINISHED
            AppointmentStatus status = appointment.getStatus(LocalDateTime.now());
            if (status == AppointmentStatus.CONSULTING || status == AppointmentStatus.FINISHED) {
                // Get selected diagnoses
                List<String> selectedDiagnoses = diagnosisPicker.getSelectedItems();
                String diagnosisIds = diagnoseTable.objectFieldsToIdsString(diagnoses, selectedDiagnoses, Diagnose::getName);
                appointment.setDiagnoses(diagnosisIds);

                // Get selected treatments
                List<String> selectedTreatments = treatmentPicker.getSelectedItems();
                String treatmentIds = treatmentTable.objectFieldsToIdsString(treatments, selectedTreatments, t -> t.getName(medicationIdsNames));
                appointment.setTreatments(treatmentIds);
            } else {
                appointment.setDiagnoses("");
                appointment.setTreatments("");
            }


            // Validate treatments for conflicts
//            checkTreatmentConflicts(treatmentPicker.getSelectedItems());
        }


        return (isValid ? appointment : null);
    }

    private void populateFormForEditing(long appointmentId) {
        Appointment appointment = appointmentTable.getById(appointmentId);
        if (appointment != null) {
            inThis.on(R.id.btBackToAppointmentList).setText(R.string.edit_appointment_header_text);

            // Set patient
            Long patientId = appointment.getPatient_id();
            String patientName = patientIdsNames.get(patientId);
            if (patientName != null) {
                patientPicker.setSelectedItems(List.of(patientName));
            }

            // Set start date and time
            String startDateTimeStr = appointment.getStartDateTime();
            LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String startDateStr = startDateTime.toLocalDate().toString();
            String startTimeStr = startDateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            inThis.on(R.id.etAppointmentStartDate).setText(startDateStr);
            inThis.on(R.id.etAppointmentStartTime).setText(startTimeStr);

            LocalDateTime endDateTime = LocalDateTime.parse(appointment.getEndDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            long durationMinutes = Duration.between(startDateTime, endDateTime).toMinutes();
            inThis.on(R.id.etAppointmentDuration).setText(String.valueOf(durationMinutes));

            List<String> selectedSymptoms = symptomTable.idsStringToObjectFields(appointment.getSymptoms(), Symptom::getName);
            symptomPicker.setSelectedItems(selectedSymptoms);

            // Only set diagnoses and treatments if status is CONSULTING or FINISHED
            AppointmentStatus status = appointment.getStatus(LocalDateTime.now());
            if (status == AppointmentStatus.CONSULTING || status == AppointmentStatus.FINISHED) {
                List<String> selectedDiagnoses = diagnoseTable.idsStringToObjectFields(appointment.getDiagnoses(), Diagnose::getName);
                diagnosisPicker.setSelectedItems(selectedDiagnoses);

                List<String> selectedTreatments = treatmentTable.idsStringToObjectFields(appointment.getTreatments(), t -> t.getName(medicationIdsNames));
                treatmentPicker.setSelectedItems(selectedTreatments);
                diagnosisPicker.setVisibility(View.VISIBLE);
                treatmentPicker.setVisibility(View.VISIBLE);
            } else {
                diagnosisPicker.setVisibility(View.GONE);
                treatmentPicker.setVisibility(View.GONE);
            }
            inThis.on(R.id.btSubmitAppointment).setText(R.string.update_appointment);
        } else {
            inThis.showToast(getString(R.string.noti_no_appointment_found, String.valueOf(appointmentId)));
        }
    }
}

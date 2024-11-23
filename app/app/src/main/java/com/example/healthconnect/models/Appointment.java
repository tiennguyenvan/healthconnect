package com.example.healthconnect.models;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.healthconnect.R;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Appointment implements Serializable {
//    private long resetTable; // enable, run, disable, run to reset this table
    private long id;

    public static String columnPatientId() {
        return "patient_id";
    }

    private long patient_id;
    private String startDateTime; // Combines date and startTime
    private String endDateTime;   // Combines date and endTime
    private String diagnoses;     // IDs of diagnoses in string
    private String symptoms;      // IDs of symptoms in string
    private String treatments;    // IDs of treatments in string

    public Appointment() {
        this.diagnoses = "";
        this.symptoms = "";
        this.treatments = "";
    }

    public Appointment(long id, long patient_id, String startDateTime, String endDateTime, String diagnoses, String symptoms, String treatments) {
        this.id = id;
        this.patient_id = patient_id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.diagnoses = diagnoses;
        this.symptoms = symptoms;
        this.treatments = treatments;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(String diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getTreatments() {
        return treatments;
    }

    public void setTreatments(String treatments) {
        this.treatments = treatments;
    }

    public AppointmentStatus getStatus(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(this.startDateTime, formatter);
        LocalDateTime end = LocalDateTime.parse(this.endDateTime, formatter);

        if (now.isBefore(start)) {
            return AppointmentStatus.UPCOMING;
        } else if (now.isAfter(start) && now.isBefore(end)) {
            return AppointmentStatus.CONSULTING;
        } else {
            return AppointmentStatus.FINISHED;
        }
    }

    public static int sortByStartDateTime(Appointment a1, Appointment a2) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start1 = a1.getStartDateTime() != null ? LocalDateTime.parse(a1.getStartDateTime(), formatter) : null;
            LocalDateTime end1 = a1.getEndDateTime() != null ? LocalDateTime.parse(a1.getEndDateTime(), formatter) : null;
            LocalDateTime start2 = a2.getStartDateTime() != null ? LocalDateTime.parse(a2.getStartDateTime(), formatter) : null;
            LocalDateTime end2 = a2.getEndDateTime() != null ? LocalDateTime.parse(a2.getEndDateTime(), formatter) : null;

            if (start1 == null && start2 == null) {
                Log.d("APPOINTMENT 12", "sortByStartDateTime: " + a1.getId() + " :: " + a2.getId());
                return 0;
            }
            if (start1 == null) {
                Log.d("APPOINTMENT 11", "sortByStartDateTime: " + a1.getId() + " :: " + a2.getId());
                return 1;
            }
            if (start2 == null) {
                Log.d("APPOINTMENT 22", "sortByStartDateTime: " + a1.getId() + " :: " + a2.getId());
                return -1;
            }

            LocalDateTime now = LocalDateTime.now();
            AppointmentStatus status1 = a1.getStatus(now);
            AppointmentStatus status2 = a2.getStatus(now);

            int statusComparison = status1.compareTo(status2);
            if (statusComparison != 0) {
                return statusComparison;
            }

            if (status1 == AppointmentStatus.UPCOMING || status1 == AppointmentStatus.CONSULTING) { // For Upcoming and Consulting
                return start1.compareTo(start2);
            } else {
                return end2.compareTo(end1);
            }
        } catch (Exception e) {
            return 0; // If parsing fails, treat as equal
        }
    }

    public void applyStatusOnTV(View parent, TextView tv) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime startDateTime = LocalDateTime.parse(this.startDateTime, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(this.endDateTime, formatter);
            LocalDateTime now = LocalDateTime.now();

            if (now.isBefore(startDateTime)) {
                tv.setText(R.string.upcoming);
                tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.primary));
            } else if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
                tv.setText(R.string.consulting);
                tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.text));
            } else {
                tv.setText(R.string.finished);
                tv.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.lighterText));
            }
        } catch (Exception e) {
            tv.setText("!" + this.getId());
        }
    }

    public String getFormatStartDateTime() {
        return getFormatDate(startDateTime);
    }

    public String getFormatEndDateTime() {
        return getFormatDate(endDateTime);
    }

    private String getFormatDate(String isoDateTime) {
        try {
            // Define the formatter for the ISO_LOCAL_DATE_TIME format
            DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            // Define the desired output format
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("HH:mm - MMM dd, yy");

            // Parse the input string into a LocalDateTime object
            LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);

            // Format the LocalDateTime into the desired format
            return dateTime.format(outputFormatter);
        } catch (Exception e) {
            return "!" + this.getId();
        }
    }

    // Demo Data
    public static List<Appointment> demoData() {
        List<Appointment> demoData = new ArrayList<>();

        // Fully detailed appointments
        demoData.add(new Appointment(1, 1, "2024-10-22T09:00", "2024-10-22T10:00", "1,2", "1,3,9", "2,4"));
        demoData.add(new Appointment(2, 2, "2024-10-23T11:00", "2024-10-23T12:00", "3", "2,5", "3"));

        // Appointments with some details missing
        demoData.add(new Appointment(3, 3, "2024-11-24T14:00", "2024-11-24T15:00", "", "4,7", "6,10")); // Missing diagnoses
        demoData.add(new Appointment(4, 2, "2024-10-25T16:00", "2024-10-25T17:00", "4", "", "1,8"));    // Missing symptoms

        // Upcoming or incomplete appointments with empty lists
        demoData.add(new Appointment(5, 3, "2024-11-26T08:00", "2024-11-26T09:00", "", "", ""));
        demoData.add(new Appointment(6, 3, "2024-11-27T10:00", "2024-11-27T11:00", "", "", ""));

        return demoData;
    }
}


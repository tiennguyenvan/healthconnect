package com.example.healthconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Appointment implements Serializable {
    private long appointment_id;
    private long patient_id;
    private String patientName;
    private String dateOfAppointment;
    private int durationAppointment;

    public Appointment(){}

    public Appointment(long patient_id, String patientName, String dateOfAppointment, int durationAppointment)
    {
        this.patient_id = patient_id;
        this.patientName = patientName;
        this.dateOfAppointment = dateOfAppointment;
        this.durationAppointment = durationAppointment;
    }

    public Appointment(long appointment_id, long patient_id, String patientName, String dateOfAppointment, int durationAppointment) {
        this(patient_id, patientName, dateOfAppointment, durationAppointment);
        this.appointment_id = appointment_id;
    }

    public long getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(long appointment_id) {
        this.appointment_id = appointment_id;
    }

    public long getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(long patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDateOfAppointment() {
        return dateOfAppointment;
    }

    public void setDateOfAppointment(String dateOfAppointment) {
        this.dateOfAppointment = dateOfAppointment;
    }

    public int getDurationAppointment() {
        return durationAppointment;
    }

    public void setDurationAppointment(int durationAppointment) {
        this.durationAppointment = durationAppointment;
    }

    public static List<Appointment> demoData() {
        List<Appointment> demoData = new ArrayList<>();
        demoData.add(new Appointment(1, "Tim Nguyen", "2024-09-11", 30));
        demoData.add(new Appointment(2,"Phyo Thaw", "2024-09-10", 60));
        demoData.add(new Appointment(3, "Bruno Beserra", "2024-10-11", 22));
        return demoData;
    }
}


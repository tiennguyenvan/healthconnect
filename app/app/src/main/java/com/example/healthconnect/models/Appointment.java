package com.example.healthconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Appointment implements Serializable {
    private long id;
    public static String columnAppointmentName() { return "appointmentName";}
    private long patient_id;
    private String date;
    private String startTime;
    private String endTime;
    private String diagnosis;
    private String symptoms;
    private String prescriptionDetail;

    public Appointment(){}

    public Appointment(long patient_id, String date, String startTime, String endTime)
    {
        this.patient_id = patient_id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Appointment(long id, long patient_id, String date, String startTime, String endTime) {
        this(patient_id, date, startTime, endTime);
        this.id = id;
    }

    public Appointment(long id, long patient_id, String date, String startTime, String endTime, String diagnosis, String symptoms, String prescriptionDetail)
    {
        this(id, patient_id, date, startTime, endTime);
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.prescriptionDetail = prescriptionDetail;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String startDate) {
        this.date = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPrescriptionDetail() {
        return prescriptionDetail;
    }

    public void setPrescriptionDetail(String prescriptionDetail) {
        this.prescriptionDetail = prescriptionDetail;
    }

    public static List<Appointment> demoData() {
        List<Appointment> demoData = new ArrayList<>();

        demoData.add(new Appointment(1, 1, "2024-09-10", "09:00", "10:00", "Flu", "1,2", "Rest and hydration"));
        demoData.add(new Appointment(2, 2, "2024-09-11", "11:00", "12:00", "Cold", "2,3", "Vitamin C and rest"));
        demoData.add(new Appointment(3, 3, "2024-10-11", "14:00", "15:00", "Headache", "4", "Pain relievers"));

        return demoData;
    }
}


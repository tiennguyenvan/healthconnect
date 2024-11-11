package com.example.healthconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Appointment implements Serializable {
    private long appointment_id;
    private long patient_id;
    private String startDate;
    private String startTime;
    private String endTime;
    private String diagnosis;
    private String[] symptoms;
    private String prescriptionDetail;

    public Appointment(){}

    public Appointment(long patient_id, String startDate, String startTime, String endTime)
    {
        this.patient_id = patient_id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Appointment(long appointment_id, long patient_id, String startDate, String startTime, String endTime) {
        this(patient_id, startDate, startTime, endTime);
        this.appointment_id = appointment_id;
    }

    public Appointment(long appointment_id, long patient_id, String startDate, String startTime, String endTime, String diagnosis, String[] symptoms, String prescriptionDetail)
    {
        this(appointment_id, patient_id, startDate, startTime, endTime);
        this.diagnosis = diagnosis;
        this.symptoms = symptoms;
        this.prescriptionDetail = prescriptionDetail;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public String[] getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String[] symptoms) {
        this.symptoms = symptoms;
    }

    public String getPrescriptionDetail() {
        return prescriptionDetail;
    }

    public void setPrescriptionDetail(String prescriptionDetail) {
        this.prescriptionDetail = prescriptionDetail;
    }

//    public static List<Appointment> demoData() {
//        List<Appointment> demoData = new ArrayList<>();
//        demoData.add(new Appointment(1, 1, "2024-09-10", "2024-09-11"));
//        demoData.add(new Appointment(2,"Phyo Thaw", "2024-09-10", 60));
//        demoData.add(new Appointment(3, "Bruno Beserra", "2024-10-11", 22));
//        return demoData;
//    }
}


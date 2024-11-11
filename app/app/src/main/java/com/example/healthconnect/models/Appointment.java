package com.example.healthconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Appointment implements Serializable {
    private long appointment_id;
    private long patient_id;
    private String startDate;
    private String endDate;
    private String diagnosis;
    private String[] symptoms;
    private String prescriptionDetail;

    public Appointment(){}

    public Appointment(long patient_id, String startDate, String endDate)
    {
        this.patient_id = patient_id;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Appointment(long appointment_id, long patient_id, String startDate, String endDate) {
        this(patient_id, startDate, endDate);
        this.appointment_id = appointment_id;
    }

    public Appointment(long appointment_id, long patient_id, String startDate, String endDate, String diagnosis, String[] symptoms, String prescriptionDetail)
    {
        this(appointment_id, patient_id, startDate, endDate);
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
//        demoData.add(new Appointment(1, "Tim Nguyen", "2024-09-11", 30));
//        demoData.add(new Appointment(2,"Phyo Thaw", "2024-09-10", 60));
//        demoData.add(new Appointment(3, "Bruno Beserra", "2024-10-11", 22));
//        return demoData;
//    }
}


package com.example.healthconnect.models;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Treatment {
    private long id;
    public static String columnMedicationId() { return "medicationId";}
    private long medicationId;
    private String dose;
    private String duration;
    private String note;

    public Treatment() {}

    public Treatment(long id, long medicationId, String dose, String duration, String note) {
        this.id = id;
        this.medicationId = medicationId;
        this.dose = dose;
        this.duration = duration;
        this.note = note;
    }

    // Getters and Setters for each field
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(long medicationId) {
        this.medicationId = medicationId;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getName(Map<Long, String> medicationIsNames) {
        String treatmentName = medicationIsNames.getOrDefault(this.getMedicationId(), "Undefined");
        treatmentName += ", " + this.getDose();
        treatmentName += ", " + this.getDuration();
        treatmentName += ", " + this.getNote();
        return treatmentName;
    }

    

    // Demo data
    public static List<Treatment> demoData() {
        List<Treatment> demoData = new ArrayList<>();
        demoData.add(new Treatment(1, 1, "3x/day", "3 days", "After meals"));
        demoData.add(new Treatment(2, 2, "2x/day", "5 days", "With water"));
        demoData.add(new Treatment(3, 5, "1x/day", "7 days", "Before bed"));
        demoData.add(new Treatment(4, 8, "2x/day", "10 days", "After breakfast"));
        demoData.add(new Treatment(5, 10, "1x/day", "5 days", "With a full glass of water"));
        return demoData;
    }
}
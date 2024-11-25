package com.example.healthconnect.models;

import java.util.ArrayList;
import java.util.List;

public class Diagnose {
    private long id;
    public static String columnName() {
        return "name";
    };
    private String name;
    private String symptomIds;   // Comma-separated list of symptom IDs
    private String treatmentIds; // Comma-separated list of treatment IDs

    public Diagnose() {}

    public Diagnose(long id, String name, String symptomIds, String treatmentIds) {
        this.id = id;
        this.name = name;
        this.symptomIds = symptomIds;
        this.treatmentIds = treatmentIds;
    }

    // Getters and Setters for each field
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymptomIds() {
        return symptomIds;
    }

    public void setSymptomIds(String symptomIds) {
        this.symptomIds = symptomIds;
    }

    public String getTreatmentIds() {
        return treatmentIds;
    }

    public void setTreatmentIds(String treatmentIds) {
        this.treatmentIds = treatmentIds;
    }

    // Demo data
    public static List<Diagnose> demoData() {
        List<Diagnose> demoData = new ArrayList<>();
        demoData.add(new Diagnose(1, "Common Cold", "1,3,9", "2,4"));
        demoData.add(new Diagnose(2, "Hypertension", "6", "5,7"));
        demoData.add(new Diagnose(3, "Diabetes Type II", "2,5", "3"));
        demoData.add(new Diagnose(4, "Allergic Rhinitis", "3,9", "1,8"));
        demoData.add(new Diagnose(5, "Gastroenteritis", "4,7", "6,10"));
        return demoData;
    }
}

package com.example.healthconnect.models;

import java.util.ArrayList;
import java.util.List;

public class Symptom {
    private long id;
    public static String columnName() { return "name";}
    private String name;
    public Symptom() {
        // Optional: Initialize default values
    }
    public Symptom(long id, String name) {
        this.id = id;
        this.name = name;
    }
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

    public static List<Symptom> demoData() {
        List<Symptom> demoData = new ArrayList<>();
        demoData.add(new Symptom(1, "Headache"));
        demoData.add(new Symptom(2, "Fever"));
        demoData.add(new Symptom(3, "Cough"));
        demoData.add(new Symptom(4, "Nausea"));
        demoData.add(new Symptom(5, "Fatigue"));
        demoData.add(new Symptom(6, "Chest Pain"));
        demoData.add(new Symptom(7, "Shortness of Breath"));
        demoData.add(new Symptom(8, "Back Pain"));
        demoData.add(new Symptom(9, "Sore Throat"));
        demoData.add(new Symptom(10, "Joint Pain"));
        return demoData;
    }
}

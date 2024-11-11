package com.example.healthconnect.models;

import java.util.ArrayList;
import java.util.List;

public class Medication {
    private long medicationId;
    private String medicationName;
    private int stock;
    private String conflicts; // List of medication IDs that conflict with this medication

    public Medication() {
        this.conflicts = "";
    }

    public Medication(String medicationName, int stock, String conflicts) {
        this.medicationName = medicationName;
        this.stock = stock;
        this.conflicts = conflicts;
    }

    public Medication(long medicationId, String medicationName, int stock, String conflicts) {
        this(medicationName, stock, conflicts);
        this.medicationId = medicationId;
    }

    // Getters and Setters for each field
    public long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(long medicationId) {
        this.medicationId = medicationId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getConflicts() {
        return conflicts;
    }

    public void setConflicts(String conflicts) {
        this.conflicts = conflicts;
    }

    public static String columnName() {
        return "medicationName";
    }

    // Method to add a conflicting medication ID
    public void addConflict(long conflictId) {
        String conflictIdStr = String.valueOf(conflictId);

        // Split conflicts into a list of strings
        List<String> conflictList = new ArrayList<>(List.of(conflicts.split(",")));

        // Check if the conflictId is already in the list
        if (!conflictList.contains(conflictIdStr)) {
            conflictList.add(conflictIdStr); // Add the new conflict ID
        }

        // Join the list back into a comma-separated string
        conflicts = String.join(",", conflictList);
    }

    // Demo data
    public static List<Medication> demoData() {
        List<Medication> demoData = new ArrayList<>();
        demoData.add(new Medication(1, "Aspirin", 100, "2,4"));       // Conflicts with Ibuprofen and Warfarin
        demoData.add(new Medication(2, "Ibuprofen", 80, "1,5"));      // Conflicts with Aspirin and Naproxen
        demoData.add(new Medication(3, "Metformin", 60, "6"));        // Conflicts with Insulin
        demoData.add(new Medication(4, "Warfarin", 40, "1,7"));       // Conflicts with Aspirin and Clopidogrel
        demoData.add(new Medication(5, "Naproxen", 90, "2"));         // Conflicts with Ibuprofen
        demoData.add(new Medication(6, "Insulin", 70, "3"));          // Conflicts with Metformin
        demoData.add(new Medication(7, "Clopidogrel", 50, "4,8"));    // Conflicts with Warfarin and Omeprazole
        demoData.add(new Medication(8, "Omeprazole", 30, "7"));       // Conflicts with Clopidogrel
        demoData.add(new Medication(9, "Amoxicillin", 120, ""));      // No conflicts listed
        demoData.add(new Medication(10, "Ciprofloxacin", 65, "11"));  // Conflicts with Tizanidine
        demoData.add(new Medication(11, "Tizanidine", 45, "10"));     // Conflicts with Ciprofloxacin
        // Conflicts with Ciprofloxacin
        return demoData;
    }

}

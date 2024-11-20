package com.example.healthconnect.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Medication {
    private long id;

    public static String columnMedicationName() {
        return "medicationName";
    }

    private String medicationName;
    private double stock;
    private double maxStock;

    public static String columnConflicts() {
        return "conflicts";
    }

    private String conflicts; // List of medication IDs that conflict with
    // this medication

    public Medication() {
        this.conflicts = "";
    }

    public Medication(String medicationName, double stock, double maxStock, String conflicts) {
        this.medicationName = medicationName;
        this.stock = stock;
        this.maxStock = maxStock;
        this.conflicts = conflicts;
    }

    public Medication(long id, String medicationName, double stock, double maxStock, String conflicts) {
        this(medicationName, stock, maxStock, conflicts);
        this.id = id;
    }

    // Getters and Setters for each field
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public double getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(double maxStock) {
        this.maxStock = maxStock;
    }

    public String getConflicts() {
        return conflicts;
    }

    public void setConflicts(String conflicts) {
        this.conflicts = conflicts;
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

    public static Map<Long, String> mapToIdsNames(List<Medication> medications) {
        return medications.stream()
                .collect(Collectors.toMap(Medication::getId, Medication::getMedicationName));
    }

    // Demo data
    public static List<Medication> demoData() {
        List<Medication> demoData = new ArrayList<>();
        demoData.add(new Medication(1, "Aspirin", 100, 1000, "2,4"));       // Conflicts with Ibuprofen and Warfarin
        demoData.add(new Medication(2, "Ibuprofen", 80, 1000, "1,5"));      // Conflicts with Aspirin and Naproxen
        demoData.add(new Medication(3, "Metformin", 60, 1000, "6"));        // Conflicts with Insulin
        demoData.add(new Medication(4, "Warfarin", 40, 1000, "1,7"));       // Conflicts with Aspirin and Clopidogrel
        demoData.add(new Medication(5, "Naproxen", 90, 1000, "2"));         // Conflicts with Ibuprofen
        demoData.add(new Medication(6, "Insulin", 70, 1000, "3"));          // Conflicts with Metformin
        demoData.add(new Medication(7, "Clopidogrel", 50, 1000, "4,8"));    // Conflicts with Warfarin and Omeprazole
        demoData.add(new Medication(8, "Omeprazole", 30, 1000, "7"));       // Conflicts with Clopidogrel
        demoData.add(new Medication(9, "Amoxicillin", 120, 1000, ""));      // No conflicts listed
        demoData.add(new Medication(10, "Ciprofloxacin", 65, 1000, "11"));  // Conflicts with Tizanidine
        demoData.add(new Medication(11, "Tizanidine", 45, 1000, "10"));     // Conflicts with Ciprofloxacin
        // Conflicts with Ciprofloxacin
        return demoData;
    }

}

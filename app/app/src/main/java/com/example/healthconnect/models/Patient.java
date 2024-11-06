package com.example.healthconnect.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// define Patient table
public class Patient implements Serializable {
    private long id;
    private String name;
    private double height;
    private double weight;
    private String dateOfBirth;
    private String contactNumber;

    public Patient() {
    }
    public Patient(String name, double height, double weight, String dateOfBirth, String contactNumber) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
    }
    public Patient(long id, String name, double height, double weight, String dateOfBirth, String contactNumber) {
        this(name, height, weight, dateOfBirth, contactNumber);
        this.id = id;
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
    public void setName(String name) { this.name = name; }

    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public static List<Patient> demoData() {
        List<Patient> demoData = new ArrayList<>();
        demoData.add(new Patient("Tim Nguyen", 170.0, 75.0, "1985-07-15", "0987654321"));
        demoData.add(new Patient("Phyo Thaw", 165.0, 60.0, "1985-05-15", "0912345678"));
        demoData.add(new Patient("Bruno Beserra", 171.0, 85.0, "1985-05-15", "0998765432"));
        return demoData;
    }

}

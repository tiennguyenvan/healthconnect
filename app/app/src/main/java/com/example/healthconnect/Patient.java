package com.example.healthconnect;

import java.io.Serializable;

public class Patient implements Serializable {
    private long id;
    private String name;
    private double height;
    private double weight;
    private String dateOfBirth;
    private String contactNumber;

    public Patient(long id, String name, double height, double weight, String dateOfBirth, String contactNumber) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
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
}

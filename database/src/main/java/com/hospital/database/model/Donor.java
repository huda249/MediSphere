package com.hospital.database.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;
    private String bloodType;
    private boolean available = true;

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    private String organType;

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public Donor(int id, String name, String address, String bloodType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.bloodType = bloodType;
    }


    public int getId() { return id; }

    public String getBloodType() {
        return bloodType;
    }

    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() {
        return address;
    }
}

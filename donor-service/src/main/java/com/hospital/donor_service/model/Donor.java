package com.hospital.donor_service.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Donor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String address;

    public Donor(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() {
        return address;
    }
}

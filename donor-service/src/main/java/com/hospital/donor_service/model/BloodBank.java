package com.hospital.donor_service.model;

import com.hospital.database.model.*;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class BloodBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bloodType;     // e.g., "O-", "AB+"
    private int quantityUnits;    // Number of blood bags
    private LocalDate donationDate = LocalDate.now();
    private Long donorId;

    @Version
    private Long version;         // Prevents double-booking in concurrent surgeries

    public BloodBank() {}

    public BloodBank(Blood b) { // casts blood to bloodbank
        this.id = (long)b.getId();
        this.bloodType = b.getBloodType();
        this.quantityUnits = b.getUnits();
        this.donationDate = LocalDate.parse(b.getHarvestTime());
        this.donorId = (long)b.getDonorId();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public int getQuantityUnits() { return quantityUnits; }
    public void setQuantityUnits(int quantityUnits) { this.quantityUnits = quantityUnits; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public Long getDonorId() { return donorId; }
    public void setDonorId(Long donorId) { this.donorId = donorId; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}

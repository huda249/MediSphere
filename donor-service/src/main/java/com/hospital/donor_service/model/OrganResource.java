package com.hospital.donor_service.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OrganResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String organType;     // e.g., "Kidney", "Heart", "Liver"
    private String bloodType;     // e.g., "O+", "A-"
    private String status = "AVAILABLE"; // AVAILABLE, RESERVED, TRANSPLANTED
    private LocalDateTime harvestTime = LocalDateTime.now();

    @Version
    private Long version;         // Prevents two surgeries from claiming the same organ

    public OrganResource() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrganType() { return organType; }
    public void setOrganType(String organType) { this.organType = organType; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getHarvestTime() { return harvestTime; }
    public void setHarvestTime(LocalDateTime harvestTime) { this.harvestTime = harvestTime; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}

package com.hospital.donor_service.model;

public class Blood { // data class for blood samples
    public int id;
    public String bloodType;
    public int donorId;
    public int operationId;
    private String status;
    private String harvestTime;

    public Blood(int id, String bloodType, int donorId, int operationId, String status, String harvestTime) {
        this.id = id;
        this.bloodType = bloodType;
        this.donorId = donorId;
        this.operationId = operationId;
        this.status = status;
        this.harvestTime = harvestTime;
    }

    public int getId() { return id;}

    public String getBloodType() {
        return bloodType;
    }

    public int getDonorId() {
        return donorId;
    }

    public int getOperationId() {
        return operationId;
    }
    public String getHarvestTime() {
        return harvestTime;
    }

    public String getStatus() {
        return status;
    }
}

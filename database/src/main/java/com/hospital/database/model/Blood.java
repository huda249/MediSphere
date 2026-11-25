package com.hospital.database.model;

public class Blood { // data class for blood samples
    public int id;
    public String bloodType;
    public int donorId;
    public int operationId;
    private String status;
    private String harvestTime;
    private int units;

    public Blood(int id, String bloodType, int donorId, int operationId, String status, String harvestTime, int units) {
        this.id = id;
        this.bloodType = bloodType;
        this.donorId = donorId;
        this.operationId = operationId;
        this.status = status;
        this.harvestTime = harvestTime;
        this.units = units;
    }

    public int getId() { return id;}

    public int getUnits() {
        return units;
    }

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

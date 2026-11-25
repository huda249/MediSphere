package com.hospital.database.model;

public class Organ {
    private int id;
    private String name;
    private int donorId;
    private int operationId;
    private String status;
    private String harvestTime;
    private String bloodType;
    private int version = 1;

    public Organ(int id, String name, int donorId, int operationId, String status, String harvestTime, String bloodType){
        this.id = id;
        this.name = name;
        this.donorId = donorId;
        this.operationId = operationId;
        this.status = status;
        this.harvestTime = harvestTime;
        this.bloodType = bloodType;
    }

    public String getBloodType() {
        return bloodType;
    }

    public int getVersion() {
        return version;
    }

    public String getHarvestTime() {
        return harvestTime;
    }

    public String getStatus() {
        return status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getOperationId() {
        return operationId;
    }

    public int getDonorId() {
        return donorId;
    }
}

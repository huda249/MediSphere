package com.hospital.donor_service.model;

public class Organ {
    private int id;
    private String name;
    private int donorId;
    private int operationId;
    private String status;
    private String harvestTime;

    public Organ(int id, String name, int donorId, int operationId, String status, String harvestTime){
        this.id = id;
        this.name = name;
        this.donorId = donorId;
        this.operationId = operationId;
        this.status = status;
        this.harvestTime = harvestTime;
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

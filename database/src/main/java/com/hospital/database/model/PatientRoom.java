package com.hospital.database.model;

public class PatientRoom {
    private int id;
    private String name;
    private boolean free;
    private int patientId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPatientId() {
        return patientId;
    }

    public boolean isFree() {
        return free;
    }

    public PatientRoom(int id, String name, boolean free) {
        this.id = id;
        this.name = name;
        this.free = free;
    }
}

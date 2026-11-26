package com.hospital.database.model;

public class OperatingRoom {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public OperatingRoom(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

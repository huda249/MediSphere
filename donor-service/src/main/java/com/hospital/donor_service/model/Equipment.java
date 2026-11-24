package com.hospital.donor_service.model;

public class Equipment {
    private int id;
    private String state;
    private String name;
    private String model;

    public Equipment(int id, String name, String state, String model) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.model = model;
    }

    public int getId() { return id;}
    public String getState() {return state;}
    public String getName() {return name;}
    public String getModel() {return model;}
}

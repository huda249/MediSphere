package com.hospital.database.model;

public class Patient {
    private int id;
    private String name;
    private String ailment;
    private int age;
    private String status;
    private String arrival;
    private String departure;
    private String symptoms;
    private int priority;
    private int queuepos;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAilment() {
        return ailment;
    }

    public String getStatus() {
        return status;
    }

    public String getArrival() {
        return arrival;
    }

    public int getPriority() {
        return priority;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getDeparture() {
        return departure;
    }

    public int getQueuepos() {
        return queuepos;
    }

    public Patient(int id, String name, int age, String ailment, String symptoms, String status, String arrival, String departure, int priority, int queuepos) {
        this.id = id;
        this.name = name;
        this.ailment = ailment;
        this.symptoms = symptoms;
        this.age = age;
        this.arrival = arrival;
        this.departure = departure;
        this.priority = priority;
        this.queuepos = queuepos;
        this.status = status;
    }
}

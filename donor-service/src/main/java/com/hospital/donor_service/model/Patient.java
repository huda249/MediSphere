package com.hospital.donor_service.model;

public class Patient {
    private int id;
    private String name;
    private String address;
    private String condition;
    private int age;
    private String birthday;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCondition() {
        return condition;
    }

    public Patient(int id, String name, String address, String condition, int age, String birthday) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.condition = condition;
        this.age = age;
        this.birthday = birthday;
    }
}

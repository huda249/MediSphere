package com.hospital.database.model;
public class Staff {
    private int id;
    private String name;
    private int salary;
    private String address;
    private String role;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getSalary() {
        return salary;
    }

    public String getRole() {
        return role;
    }

    public Staff(int id, String name, int salary, String address, String role) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.role = role;
        this.salary = salary;
    }
}

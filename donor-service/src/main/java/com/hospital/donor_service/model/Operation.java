package com.hospital.donor_service.model;

import java.util.ArrayList;

public class Operation {
    public int id;
    public String name;
    public String date;
    public int patientId;
    public ArrayList<Integer> staff;
    public ArrayList<Integer> equipment;
    public ArrayList<Integer> blood;
    public ArrayList<Integer> organs;
    public ArrayList<Integer> operatingRooms;

    public Operation(int id,
                     String name,
                     String date,
                     int patientId,
                     ArrayList<Integer> staff,
                     ArrayList<Integer> equipment,
                     ArrayList<Integer> blood,
                     ArrayList<Integer> organs,
                     ArrayList<Integer> operatingRooms){
        this.id = id;
        this.name = name;
        this.date = date;
        this.patientId = patientId;
        this.staff = staff;
        this.equipment = equipment;
        this.blood = blood;
        this.organs = organs;
        this.operatingRooms = operatingRooms;

    }

    public int getId() {return this.id;}
    public String getName() {return this.name;}
    public String getDate() {return this.date;}
    public int getPatientId() { return this.patientId;}
    public ArrayList<Integer> getStaff() {return staff;}
    public ArrayList<Integer> getEquipment() {return equipment;}
    public ArrayList<Integer> getBlood() {return blood;}
    public ArrayList<Integer> getOrgans() {return organs;}
    public ArrayList<Integer> getOperatingRooms() {return operatingRooms;}
}

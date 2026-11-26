package com.hospital.hospital_backend.model;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Patient {
    
    //patient info
    private int id;
    private String name;
    private int age;
    private String ailment;
    private String symptoms;

    //emergency room info
    private int priority;
    private String status;
    private Integer roomNum;
    private final LocalDateTime arrivalTime;
    private LocalDateTime departureTime;

    //constructor for new patient
    @JsonCreator 
    public Patient(@JsonProperty("name") String name,
     @JsonProperty("age") int age, @JsonProperty("ailment") String ailment, 
     @JsonProperty("symptoms") String symptoms, @JsonProperty("priority") int priority){
        this.name = name;
        this.age = age;
        this.ailment = ailment;
        this.symptoms = symptoms;
        this.priority = priority;

        //setting the default emergency waiting room info
        this.status = "WAITING";
        this.roomNum = null;
        this.arrivalTime = LocalDateTime.now();
        this.departureTime = null;
    }

    public Patient(com.hospital.database.model.Patient p) {
        this.id = p.getId();
        this.name = p.getName();
        this.age = p.getAge();
        this.ailment = p.getAilment();
        this.symptoms = p.getSymptoms();
        this.priority = p.getPriority();
        this.status = p.getStatus();
        this.roomNum = null;
        this.arrivalTime = LocalDateTime.parse(p.getArrival());
        if(p.getDeparture() != null && !p.getDeparture().isBlank()){
            this.departureTime = LocalDateTime.parse(p.getDeparture());
        }
        else{
            this.departureTime = null;
        }
    }

    //getter and setter methods
    //getter for patient ID
    public int getPatientID(){
        return id;
    }

    //getter for patient name
    public String getPatientName(){
        return name;
    }

    //setter for patient name
    public void setPatientName(String name){
        this.name = name;
    }

    //getter for patient age
    public int getPatientAge(){
        return age;
    }

    //setter for patient age
    public void setPatientAge(int age){
        this.age = age;
    }

    //getter for patient ailment
    public String getPatientAilment(){
        return ailment;
    }

    //settter for patient ailment 
    public void setPatientAilment(String ailment){
        this.ailment = ailment;
    }

    //getter for patient symptoms
    public String getPatientSymptoms(){
        return symptoms;
    }

    //setter for patient symptoms
    public void setPatientSymptoms(String symptoms){
        this.symptoms = symptoms;
    }

    //getter for patient priority
    public int getPatientPriority(){
        return priority;
    }

    //setter for patient priority
    public void setPatientPriority(int priority){
        this.priority = priority;
    }

    //getter for patient status
    public String getPatientStatus(){
        return status;
    }

    //setter for patient status
    public void setPatientStatus(String status){
        this.status = status;
    }

    //getter for patient room number
    public Integer getRoomNum(){
        return roomNum;
    }

    //setter for patient room number
    public void setRoomNum(Integer roomNum){
        this.roomNum = roomNum;
    }

    //getter for patient arrival time
    public LocalDateTime getArrivalTime(){
        return arrivalTime;
    }

    //getter for patient departure time
    public LocalDateTime getDepartureTime(){
        return departureTime;
    }

    //setter for patient departure time
    public void setDepartureTime(LocalDateTime departureTime){
        this.departureTime = departureTime;
    }


}

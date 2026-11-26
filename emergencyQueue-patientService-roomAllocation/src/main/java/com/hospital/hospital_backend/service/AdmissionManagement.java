package com.hospital.hospital_backend.service;

import com.hospital.database.model.DatabaseHelper;


import org.springframework.stereotype.Service;


@Service 
public class AdmissionManagement {


    private final DatabaseHelper db = new DatabaseHelper();

    //admitting patient
    //changing their status from "WAITING" to "ADMITTED"
    //this removes patient from emergency queue 
    //admitting a patient and assigning a room
    public synchronized boolean admitPatient(int id, int roomNum){

        //validating patient ID and room number
        if(id <= 0 || roomNum <= 0){
            return false;
        }

        //admitting the patient through a database transaction
        return db.admitPatientTransaction(id, roomNum);
    }

    //discharging the patient
    //changing their status from "ADMITTED" to "DISCHARGED"
    //sets the departure time
    //discharging a patient and releasing their room
    public synchronized boolean dischargePatient(int id){

        //validating patient ID
        if(id <= 0){
            return false;
        }

        //discharging the patient through a database transaction
        return db.dischargePatientTransaction(id);
    }
    
}

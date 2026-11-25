package com.hospital.hospital_backend.service;

import org.springframework.stereotype.Service;
import com.hospital.database.model.DatabaseHelper;

import com.hospital.database.model.PatientRoom;
import java.util.List;


@Service 
public class RoomAllocation {
    
    //accessing the database
    private final DatabaseHelper db = new DatabaseHelper();

    //assigning a room to a patient
    public synchronized boolean roomAssignment(int patientID, int roomNum){

        //validating patient ID and room number
        if(patientID <= 0 || roomNum <= 0){
            return false;
        }

        //reserving the room in the database
        int updated = db.reservePatientRoom(roomNum, patientID);

        //checking if the room was successfully assigned
        return updated > 0;
    }

    //releasing a room
    public synchronized boolean releaseRoom(int patientID){

        //validating patient ID
        if(patientID <= 0){
            return false;
        }

        //releasing the room in the database
        int updated = db.releasePatientRoom(patientID);

        //checking if the room was successfully released
        return updated > 0;
    }

    //retrieving all available patient rooms
    public List<PatientRoom> getAvailableRooms(){

        List<PatientRoom> rooms = db.getFreePatientRooms();

        //checking if the database query failed
        if(rooms == null){
            throw new IllegalStateException("Unable to retrieve available rooms");
        }

        return rooms;
    }

}

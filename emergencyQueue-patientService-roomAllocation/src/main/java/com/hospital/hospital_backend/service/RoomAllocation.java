package com.hospital.hospital_backend.service;

import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;

@Service 
public class RoomAllocation {
    
    private final PatientManagement patientManagement;
    private final TemporaryRoomService temporaryRoomService;

    public RoomAllocation(PatientManagement patientManagement, TemporaryRoomService temporaryRoomService){
        this.patientManagement = patientManagement;
        this.temporaryRoomService = temporaryRoomService;
    }

    //assigning a room to a patient 
    public synchronized boolean roomAssignment(int patientID, int roomNum){

        //getting patient ID
        Patient patient = patientManagement.getPatientByID(patientID);

        //validation to ensure patient exists
        if(patient == null){
            return false;
        }

        //validation to ensure patient is waiting to be admitted
        if(!"WAITING".equals(patient.getPatientStatus())){
            return false;
        }

        //ensuring patient has not already been assigned a room
        if(patient.getRoomNum() != null){
            return false;
        }

        //ensuring room number is not negative or equal to zero
        if(roomNum <= 0){
            return false;
        }

        if(!temporaryRoomService.reserve(roomNum)){
            return false;
        }

        //assigning the room to the patient 
        patient.setRoomNum(roomNum);

        return true;

    }

    //releasing a room
    public synchronized boolean releaseRoom(int patientID){

        //getting patient ID
        Patient patient = patientManagement.getPatientByID(patientID);

        //validation to ensure patient exists
        if(patient == null){
            return false;
        }

        //checking if the patient has already been assigned to a room
        if(patient.getRoomNum() == null){
            return false;
        }

        //getting the room number of the patient
        int roomNum = patient.getRoomNum();

        //releasing the room
        if(!temporaryRoomService.release(roomNum)){
            return false;
        }

        //removing the assigned room from the patient info
        patient.setRoomNum(null);

        return true;
    }

}

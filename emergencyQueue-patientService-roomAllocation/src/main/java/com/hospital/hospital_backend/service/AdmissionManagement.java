package com.hospital.hospital_backend.service;

import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;

import java.time.LocalDateTime;

@Service 
public class AdmissionManagement {

    private final PatientManagement patientManagement;
    private final RoomAllocation roomAllocation;

    public AdmissionManagement(PatientManagement patientManagement, RoomAllocation roomAllocation){
        this.patientManagement = patientManagement;
        this.roomAllocation = roomAllocation;
    }

    //admitting patient
    //changing their status from "WAITING" to "ADMITTED"
    //this removes patient from emergency queue 
    public synchronized boolean admitPatient(int id, int roomNum){

        //retrieving patient by ID
        Patient patient = patientManagement.getPatientByID(id);

        //validation to check if patient exists
        if(patient == null){
            return false;
        }

        //validation to check if patient status is set to "WAITING"
        if(!"WAITING".equals(patient.getPatientStatus())){
            return false;
        }

        //admitting a patient to a room
        if(!roomAllocation.roomAssignment(id, roomNum)){
            return false;
        }

        //admitting patient once room has been reserved
        patient.setPatientStatus("ADMITTED");

        return true;
    }

    //discharging the patient
    //changing their status from "ADMITTED" to "DISCHARGED"
    //sets the departure time
    public synchronized boolean dischargePatient(int id){

        Patient patient = patientManagement.getPatientByID(id);

        //validation to check if patient exists
        if(patient == null){
            return false;
        }

        //checking if patient status is not "ADMITTED"
        if(!"ADMITTED".equals(patient.getPatientStatus())){
            return false;
        }

        //releasing the assigned room 
        if(!roomAllocation.releaseRoom(id)){
            return false;
        }

        //setting status to "DISCHARGED"
        patient.setPatientStatus("DISCHARGED");

        //setting departure time
        patient.setDepartureTime(LocalDateTime.now());

        return true;
    }
    
}

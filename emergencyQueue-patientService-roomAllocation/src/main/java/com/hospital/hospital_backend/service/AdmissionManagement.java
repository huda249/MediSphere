package com.hospital.hospital_backend.service;

import com.hospital.database.model.DatabaseHelper;
import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;

import java.time.LocalDateTime;

@Service 
public class AdmissionManagement {

    private final PatientManagement patientManagement;
    private final RoomAllocation roomAllocation;

    private final DatabaseHelper db = new DatabaseHelper();

    public AdmissionManagement(PatientManagement patientManagement, RoomAllocation roomAllocation){
        this.patientManagement = patientManagement;
        this.roomAllocation = roomAllocation;
    }

    //admitting patient
    //changing their status from "WAITING" to "ADMITTED"
    //this removes patient from emergency queue 
    public synchronized boolean admitPatient(int id, int roomNum){

        //retrieving patient by ID
        com.hospital.database.model.Patient patient = db.getPatientById(id);

        //validation to check if patient exists
        if(patient == null){
            return false;
        }

        //validation to check if patient status is set to "WAITING"
        if(!"WAITING".equals(patient.getStatus())){
            return false;
        }

        //admitting a patient to a room
        var room = db.getPatientRoom(Integer.toString(roomNum)).get(0);
        db.updatePatientRoom(room.getId(), room.getName(), false, id);

        //admitting patient once room has been reserved
        db.updatePatient(id,
                patient.getPriority(),
                patient.getQueuepos(),
                null,
                "ADMITTED",
                patient.getAilment(), patient.getSymptoms());

        return true;
    }

    //discharging the patient
    //changing their status from "ADMITTED" to "DISCHARGED"
    //sets the departure time
    public synchronized boolean dischargePatient(int id){

        com.hospital.database.model.Patient patient = db.getPatientById(id);

        //validation to check if patient exists
        if(patient == null){
            return false;
        }

        //checking if patient status is not "ADMITTED"
        if(!"ADMITTED".equals(patient.getStatus())){
            return false;
        }

        //releasing the assigned room
        var room = db.getPatientRoomByPatientId(id).get(0);
        db.updatePatientRoom(room.getId(), room.getName(), true, room.getPatientId());

        //setting status to "DISCHARGED"
        db.updatePatient(id,
                patient.getPriority(),
                patient.getQueuepos(),
                LocalDateTime.now().toString(),
                "DISCHARGED",
                patient.getAilment(), patient.getSymptoms());

        return true;
    }
    
}

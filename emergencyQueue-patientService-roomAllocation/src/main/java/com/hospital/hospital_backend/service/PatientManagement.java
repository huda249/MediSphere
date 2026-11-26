package com.hospital.hospital_backend.service;

import com.hospital.database.model.DatabaseHelper;
import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;
import java.util.ArrayList;
import java.util.List;

@Service
public class PatientManagement {

    //accessing the database
    private final DatabaseHelper db = new DatabaseHelper();

    //registering a new patient
    public synchronized boolean registerNewPatient(Patient patient){

        //validations:
        //checking if patient info is null
        if(patient == null){
            return false;
        }

        //patient name cannot be blank when registering
        if(patient.getPatientName() == null || patient.getPatientName().isBlank()){
            return false;
        }

        //checking that patient age is not a negative number
        if(patient.getPatientAge() < 0){
            return false;
        }

        //patient priority must be between 1 (high) and 5 (low)
        if(patient.getPatientPriority() < 1 || patient.getPatientPriority() > 5){
            return false;
        }

        //end of validations

        //inserting valid patient into the database
        int patientID = db.insertPatient(
            patient.getPatientName(),
            patient.getPatientAilment(),
            patient.getPatientAge(),
            patient.getPatientStatus(),
            patient.getArrivalTime().toString(),
            patient.getPatientSymptoms(),
            patient.getPatientPriority(),
            0
        );

        //checking if patient was successfully registered
        return patientID > 0;
    }

    //retrieving all patients
    public List<Patient> getPatients(){
        List<com.hospital.database.model.Patient> patients = db.getPatient(null, null);

        //checking if the database query failed
        if(patients == null){
            throw new IllegalStateException("Unable to retrieve patients from the database");
        }

        List<Patient> ret = new ArrayList<>();

        for(int i = 0; i < patients.size(); i++){

            //converting the database patient into the backend patient model
            Patient patient = new Patient(patients.get(i));

            //retrieving the patient's assigned room
            retrieveAssignedRoom(patient);

            ret.add(patient);
        }

        return ret;
    }

    //retrieving a patient by their ID
    public Patient getPatientByID(int id){

        com.hospital.database.model.Patient patient = db.getPatientById(id);

        //checking if the patient exists
        if(patient == null){
            return null;
        }

        //converting the database patient into the backend patient model
        Patient convertedPatient = new Patient(patient);

        //retrieving the patient's assigned room
        retrieveAssignedRoom(convertedPatient);

        return convertedPatient;
    }

    //retrieving the patient's assigned room
    private void retrieveAssignedRoom(Patient patient){

        var rooms = db.getPatientRoomByPatientId(patient.getPatientID());

        //checking if the database query failed
        if(rooms == null){
            throw new IllegalStateException("Unable to retrieve assigned room");
        }

        //checking if the patient has an assigned room
        if(!rooms.isEmpty()){
            patient.setRoomNum(Integer.parseInt(rooms.get(0).getName()));
        }
        else{
            patient.setRoomNum(null);
        }
    }


    //updating the patient info
    public boolean updatePatientInfo(int id, String name, int age, String ailment, String symptoms){

        Patient patient = getPatientByID(id);

        //validations:
        //check if patient does exist before updating
        if(patient == null){
            return false;
        }

        //patient name cannot be blank
        if(name == null || name.isBlank()){
            return false;
        }

        //patient age cannot be negative
        if(age < 0){
            return false;
        }

        //updating patient information in the database
        int updated = db.updatePatientInfo(id, name, age, ailment, symptoms);

        //checking if patient information was successfully updated
        return updated > 0;
    }

}

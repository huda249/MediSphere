package com.hospital.hospital_backend.service;

import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;
import java.util.ArrayList;
import java.util.List;

@Service 
public class PatientManagement {

    //temporary until database implementation
    private final List<Patient> patients = new ArrayList<>();

    //registering a new patient
    public synchronized boolean registerNewPatient(Patient patient){

        //validations:
        //checking if patient info is null
        if(patient == null){
            return false;
        }

        //checking for a valid patient ID
        if(patient.getPatientID() <= 0){
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

        //checking if the patient ID already exists
        if(getPatientByID(patient.getPatientID()) != null){
            return false;
        }

        //end of validations

        //adding valid patient to the patient list
        patients.add(patient);
        return true;
    }

    //retrieving all patients
    public List<Patient> getPatients(){
        return new ArrayList<>(patients);
    }

    //retriving a patient by their ID
    public Patient getPatientByID(int id){
        for(Patient patient : patients){
            if(patient.getPatientID() == id){
                return patient;
            }
        }

        //returns null if patient does not exist
        return null;
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


        //if patient exists, their info is updated
        //using setter methods from Patient class
        patient.setPatientName(name);
        patient.setPatientAge(age);
        patient.setPatientAilment(ailment);
        patient.setPatientSymptoms(symptoms);

        return true;
    }

}

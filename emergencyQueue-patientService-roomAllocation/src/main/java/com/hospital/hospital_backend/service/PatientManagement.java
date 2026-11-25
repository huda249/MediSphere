package com.hospital.hospital_backend.service;

import com.hospital.database.model.DatabaseHelper;
import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;
import java.util.ArrayList;
import java.util.List;

@Service 
public class PatientManagement {

    //temporary until database implementation
    private final List<Patient> patients = new ArrayList<>();
    private final DatabaseHelper db = new DatabaseHelper();

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
        db.insertPatient(patient.getPatientName(),
                patient.getPatientAilment(),
                patient.getPatientAge(),
                patient.getPatientStatus(),
                patient.getArrivalTime().toString(),
                patient.getPatientSymptoms(),
                patient.getPatientPriority(),
                0);
        //patients.add(patient);
        return true;
    }

    //retrieving all patients
    public List<Patient> getPatients(){
        List<com.hospital.database.model.Patient> patients = (db.getPatient(null, null));
        List<Patient> ret = new ArrayList<>();
        for (int i=0 ; i< patients.size(); i++) {
            ret.add(i, new Patient(patients.get(i)));
        }
        return ret;
    }

    //retriving a patient by their ID
    public Patient getPatientByID(int id){
        return new Patient(db.getPatientById(id));
    }

    //updating the patient info
    public boolean updatePatientInfo(int id, String name, int age, String ailment, String symptoms){
        com.hospital.database.model.Patient patient = db.getPatientById(id);

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

        db.updatePatient(id, patient.getPriority(), patient.getQueuepos(),patient.getDeparture(),patient.getStatus(), ailment, symptoms);

        //if patient exists, their info is updated
        //using setter methods from Patient class
        //patient.setPatientName(name);
        //patient.setPatientAge(age);
        //patient.setPatientAilment(ailment);
        //patient.setPatientSymptoms(symptoms);

        return true;
    }

}

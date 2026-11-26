package com.hospital.hospital_backend.service;

import com.hospital.database.model.DatabaseHelper;
import org.springframework.stereotype.Service;
import com.hospital.hospital_backend.model.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Service 
public class EmergencyQueue {

    private final DatabaseHelper db = new DatabaseHelper();

    //method to retrieve all waiting patients
    public List<Patient> getWaitingPatients(){  

        //creating a new list to store waiting patients
        List<Patient> waitingPatients = new ArrayList<>();

        //retrieving waiting patients from the database
        List<com.hospital.database.model.Patient> patients = db.getPatient(null, "WAITING");

        //checking if the database query failed
        if(patients == null){
            throw new IllegalStateException("Unable to retrieve waiting patients");
        }

        //checking patient status for "WAITING"
        for(com.hospital.database.model.Patient patient : patients){
            if("WAITING".equals(patient.getStatus())){
            waitingPatients.add(new Patient(patient));
        }
        }

        return waitingPatients;
    }

    //method to retrieve patients based on and ordered by priority 
    //and arrival time
    public List<Patient> getPriorityQueue(){

        //getting list of waiting patients
        List<Patient> waitingPatients = getWaitingPatients();

        //sorting through list by priority then arrival time
        //priority: lowest number (highest priority) to highest number (lowest priority)
        //arrival time: when patients have the same priority, whoever has the earliest arrival time is ordered 
        //              above
        waitingPatients.sort(Comparator.comparingInt(Patient::getPatientPriority).thenComparing(Patient::getArrivalTime));

        return waitingPatients;
    }

    public boolean updatePatientPriority(int id, int priority){

        //getting patient using their ID
        com.hospital.database.model.Patient patient = db.getPatientById(id);

        //validation to check if patient exists
        if(patient == null){
            return false;
        }

        //validating priority
        if(priority < 1 || priority > 5){
            return false;
        } 

        //if the patient status is not "WAITING"
        //not reprioritized within the queue
        if(!"WAITING".equals(patient.getStatus())){
            return false;
        }

        //updating the patient's priority in the database
        int updated = db.updatePatientPriority(id, priority);

        //checking if the priority was successfully updated
        return updated > 0; 
    }

    //method to retireve the patient's position in the queue
    public int getPatientQP(int patientID){

        //retrieving the waiting list with patient priority
        List<Patient> queue = getPriorityQueue();

        //finding the patient within the queue
        for(int i = 0; i < queue.size(); i++){
            if(queue.get(i).getPatientID() == patientID){
                return i + 1;
            }
        }

        //when patient is not currently in the queue
        return -1;

    }
    
}

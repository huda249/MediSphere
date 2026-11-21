package com.hospital.hospital_backend.controller;

import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hospital.hospital_backend.model.Patient;
import com.hospital.hospital_backend.service.PatientManagement;

import java.util.List;


@RestController 
@RequestMapping("/api/patients")

public class PatientController {
    
    //accessing patient management 
    private final PatientManagement patientManagement;

    public PatientController(PatientManagement patientManagement){
        this.patientManagement = patientManagement;
    }

    //HTTP GET request for all registered patients
    @GetMapping
    public List<Patient> getAllPatients(){
        return patientManagement.getPatients();
    }

    //HTTP POST request to register a new patient
    @PostMapping
    public boolean registerPatient(@RequestBody Patient patient){
        return patientManagement.registerNewPatient(patient);
    }

    //HTTP GET request to retrieve a patient by their ID
    @GetMapping("/{id}")
    public Patient getPatientByID(@PathVariable int id){
        return patientManagement.getPatientByID(id);
    }

    //HTTP PUT request to update patient info
    @PutMapping("/{id}")
    public boolean updatePatInfo(@PathVariable int id, @RequestBody Patient patient){
        return patientManagement.updatePatientInfo(id, patient.getPatientName(), 
        patient.getPatientAge(), patient.getPatientAilment(), patient.getPatientSymptoms());
    }
    
}

package com.hospital.hospital_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hospital.hospital_backend.model.Patient;
import com.hospital.hospital_backend.service.EmergencyQueue;

import java.util.List;


@RestController 
@RequestMapping("/api/emergency")
public class EmergencyQueueController {
    
    //accessing the emergency queue
    private final EmergencyQueue emergencyQueue;

    public EmergencyQueueController(EmergencyQueue emergencyQueue){
        this.emergencyQueue = emergencyQueue;
    }

    //HTTP GET request for priority queue of patients
    @GetMapping("/queue")
    public List<Patient> getPriorityQueue(){
        return emergencyQueue.getPriorityQueue();
    }

    //HTTP PUT request to update patient's priority
    @PutMapping("/priority/{id}/{priority}")
    public boolean updatePatientPriority(@PathVariable int id, @PathVariable int priority){
        return emergencyQueue.updatePatientPriority(id, priority);
    }

    //HTTP GET request to get a patient's position in the emergency room queue
    @GetMapping("/queue/position/{id}")
    public int getPatientQP(@PathVariable int id){
        return emergencyQueue.getPatientQP(id);
    }
    
}

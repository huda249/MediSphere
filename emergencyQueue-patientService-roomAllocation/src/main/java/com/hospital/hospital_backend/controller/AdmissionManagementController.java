package com.hospital.hospital_backend.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hospital.hospital_backend.service.AdmissionManagement;
import com.hospital.hospital_backend.service.RoomAllocation;
import com.hospital.database.model.PatientRoom;

import java.util.List;

@RestController
@RequestMapping("/api/admissions")
public class AdmissionManagementController {

    private final AdmissionManagement admissionManagement;
    private final RoomAllocation roomAllocation;

    public AdmissionManagementController(
        AdmissionManagement admissionManagement,
        RoomAllocation roomAllocation){

        this.admissionManagement = admissionManagement;
        this.roomAllocation = roomAllocation;
    }

    //Admitting a patient with their patient ID
    @PutMapping("/admit/{id}/{roomNum}")
    public boolean admitPatient(@PathVariable int id, @PathVariable int roomNum){
        return admissionManagement.admitPatient(id, roomNum);
    }

    //Discharging a patient with their patient ID
    @PutMapping("/discharge/{id}")
    public boolean dischargePatient(@PathVariable int id){
        return admissionManagement.dischargePatient(id);
    }

    //Retrieving all available patient rooms
    @GetMapping("/rooms/available")
    public List<PatientRoom> getAvailableRooms(){
        return roomAllocation.getAvailableRooms();
    }
}
    
    


package com.hospital.donor_service.controller;

import com.hospital.donor_service.model.BloodBank;
import com.hospital.donor_service.model.Donor;
import com.hospital.donor_service.model.OrganResource;
import com.hospital.donor_service.service.DonorMatchingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allows Maisha's frontend to call your APIs
public class DonorController {

    private final DonorMatchingService matchingService;

    public DonorController(DonorMatchingService matchingService) {
        this.matchingService = matchingService;
    }

    // --- Donor Endpoints ---
    @PostMapping("/donors/register")
    public Donor registerDonor(@RequestBody Donor donor) {
        return matchingService.registerDonor(donor);
    }

    @GetMapping("/donors")
    public List<Donor> getAllDonors() {
        return matchingService.getAllDonors();
    }

    // --- Blood Bank Endpoints ---
    @PostMapping("/blood-bank/add")
    public BloodBank addBloodStock(@RequestBody BloodBank bloodBank) {
        return matchingService.addBloodStock(bloodBank);
    }

    @GetMapping("/blood-bank/inventory")
    public List<BloodBank> getBloodInventory() {
        return matchingService.getAllBloodInventory();
    }

    // --- Organ Endpoints ---
    @PostMapping("/organs/add")
    public OrganResource addOrgan(@RequestBody OrganResource organ) {
        return matchingService.addOrganResource(organ);
    }

    @GetMapping("/organs")
    public List<OrganResource> getAllOrgans() {
        return matchingService.getAllOrgans();
    }

    // --- Compatibility & Matching Endpoints ---
    @GetMapping("/match/blood")
    public Map<String, Object> matchBlood(@RequestParam String type,
                                          @RequestParam(defaultValue = "1") int units) {
        return matchingService.findCompatibleBlood(type, units);
    }

    @GetMapping("/match/organ")
    public Map<String, Object> matchOrgan(@RequestParam String organ,
                                          @RequestParam String bloodType) {
        return matchingService.findCompatibleOrgan(organ, bloodType);
    }

    // --- Concurrency-Safe Reservation Endpoints ---
    @PostMapping("/reserve/blood/{bloodId}")
    public String reserveBlood(@PathVariable Long bloodId,
                               @RequestParam(defaultValue = "1") int units) {
        return matchingService.reserveBlood(bloodId, units);
    }

    @PostMapping("/reserve/organ/{organId}")
    public String reserveOrgan(@PathVariable Long organId) {
        return matchingService.reserveOrgan(organId);
    }
}

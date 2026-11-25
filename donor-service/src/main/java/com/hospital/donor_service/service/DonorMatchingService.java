package com.hospital.donor_service.service;

import com.hospital.database.model.*;
import com.hospital.donor_service.model.*;
import com.hospital.donor_service.repository.BloodBankRepository;
import com.hospital.donor_service.repository.DonorRepository;
import com.hospital.donor_service.repository.OrganResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonorMatchingService {

    private final DonorRepository donorRepository;
    private final BloodBankRepository bloodBankRepository;
    private final OrganResourceRepository organRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final DatabaseHelper db = new DatabaseHelper();

    @Value("${or.service.url}")
    private String orServiceUrl;

    @Value("${er.service.url}")
    private String erServiceUrl;

    public DonorMatchingService(DonorRepository donorRepository,
                                BloodBankRepository bloodBankRepository,
                                OrganResourceRepository organRepository) {
        this.donorRepository = donorRepository;
        this.bloodBankRepository = bloodBankRepository;
        this.organRepository = organRepository;
    }

    // 1. Blood Compatibility Rules
    public List<String> getCompatibleDonorTypes(String recipientBloodType) {
        return switch (recipientBloodType.toUpperCase()) {
            case "O-"  -> List.of("O-");
            case "O+"  -> List.of("O-", "O+");
            case "A-"  -> List.of("O-", "A-");
            case "A+"  -> List.of("O-", "O+", "A-", "A+");
            case "B-"  -> List.of("O-", "B-");
            case "B+"  -> List.of("O-", "O+", "B-", "B+");
            case "AB-" -> List.of("O-", "A-", "B-", "AB-");
            case "AB+" -> List.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
            default    -> List.of();
        };
    }

    // 2. Register & Add Resources
    public Donor registerDonor(Donor donor) {
        var id = db.insertDonor(donor.getName(), donor.getAddress(), donor.getBloodType());
        return db.getDonorById(id);
        //return null;
    }

    public BloodBank addBloodStock(BloodBank bloodBank) {

        var id = db.insertBlood(bloodBank.getBloodType(),
                (int) (long) bloodBank.getDonorId(),
                "",
                bloodBank.getDonationDate().toString(),
                0,
                bloodBank.getQuantityUnits());
        var b =  db.getBloodById(id);
        return new BloodBank(b);
    }

    public OrganResource addOrganResource(OrganResource organ) {
        var id = db.insertOrgan(organ.getOrganType(), 0, organ.getStatus(), organ.getHarvestTime().toString(), 0, organ.getBloodType());

        var o = db.getOrganById(id);
        return new OrganResource(o);
    }

    public List<Donor> getAllDonors() {
        return db.getDonor(null, null, null);
    }

    public List<BloodBank> getAllBloodInventory() {
        var list = db.getBlood(null, null);
        var ret = new ArrayList<BloodBank>();
        for(int i=0; i < list.size(); i++) {
            ret.set(i, new BloodBank(list.get(i)));
        }
        return ret;
    }

    public List<OrganResource> getAllOrgans() {
        var list = db.getOrgan(null, null, null, null);
        var ret = new ArrayList<OrganResource>();
        for(int i=0; i < list.size(); i++) {
            ret.set(i, new OrganResource(list.get(i)));
        }
        return ret;
    }

    // 3. Search Compatible Blood & Organs
    public Map<String, Object> findCompatibleBlood(String patientBloodType, int requiredUnits) {
        List<String> compatibleTypes = getCompatibleDonorTypes(patientBloodType);
        List<BloodBank> matchingStock = new ArrayList<>();
        List<Donor> matchingDonors = new ArrayList<>();
        for (int i=0; i< compatibleTypes.size(); i++) {
            ArrayList<Blood> matchingBlood = db.getBlood(compatibleTypes.get(i), "AVAILABLE");
            ArrayList<Donor> matchingDonorsOfType = db.getDonor(null, null, compatibleTypes.get(i));
            for (int j = 0; j< matchingBlood.size(); j++) {
                matchingStock.add(new BloodBank(matchingBlood.get(j)));
            }
            matchingDonors.addAll(matchingDonorsOfType);
        }

        int totalAvailableUnits = matchingStock.stream().mapToInt(BloodBank::getQuantityUnits).sum();

        Map<String, Object> result = new HashMap<>();
        result.put("patientBloodType", patientBloodType);
        result.put("compatibleDonorTypes", compatibleTypes);
        result.put("totalAvailableUnits", totalAvailableUnits);
        result.put("hasEnoughStock", totalAvailableUnits >= requiredUnits);
        result.put("matchingInventory", matchingStock);
        result.put("availableDonors", matchingDonors);
        return result;
    }

    public Map<String, Object> findCompatibleOrgan(String organType, String patientBloodType) {
        List<String> compatibleTypes = getCompatibleDonorTypes(patientBloodType);

        List<OrganResource> availableOrgans = new ArrayList<>();
        List<Donor> matchingDonors = new ArrayList<>();
        for (int i=0; i < compatibleTypes.size(); i++) {
            ArrayList<Organ> matchingOrgans = db.getOrgan(null, "AVAILABLE", null, compatibleTypes.get(i));
            ArrayList<Donor> matchingDonorsOfType = db.getDonor(null, null, compatibleTypes.get(i));
            for (int j = 0 ; j < matchingOrgans.size(); j++) {
                availableOrgans.add(new OrganResource(matchingOrgans.get(j)));
            }
            matchingDonors.addAll(matchingDonorsOfType);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("requestedOrgan", organType);
        result.put("patientBloodType", patientBloodType);
        result.put("compatibleDonorTypes", compatibleTypes);
        result.put("availableOrgansInStorage", availableOrgans);
        result.put("registeredLiveDonors", matchingDonors);
        return result;
    }

    // 4. Concurrency-Safe Reservation + Notify Teammates' Services
    @Transactional
    public String reserveBlood(Long bloodId, int unitsToReserve) {
        Blood stock = db.getBloodById((int)(long)bloodId);
        //BloodBank stock = bloodBankRepository.findById(bloodId)
        //        .orElseThrow(() -> new RuntimeException("Blood record not found"));

        if (stock.getUnits() < unitsToReserve) {
            return "FAILED: Not enough units available.";
        }


        db.updateBloodUnits((int)(long)bloodId, stock.getUnits() - unitsToReserve);
        //stock.setQuantityUnits(stock.getQuantityUnits() - unitsToReserve);
        //bloodBankRepository.save(stock);

        notifyDepartments("Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood (ID: " + bloodId + ")");
        return "SUCCESS: Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood.";
    }

    @Transactional
    public String reserveOrgan(Long organId) {
        Organ organ = db.getOrganById((int)(long)organId);

        if (!"AVAILABLE".equalsIgnoreCase(organ.getStatus())) {
            return "FAILED: Organ is already " + organ.getStatus();
        }

        db.updateOrgan((int)(long)organId, organ.getName(), organ.getDonorId(), organ.getOperationId(), "RESERVED", organ.getHarvestTime());

        notifyDepartments("Reserved " + organ.getName() + " (Blood Type: " + organ.getBloodType() + ", ID: " + organId + ")");
        return "SUCCESS: Organ ID " + organId + " (" + organ.getName() + ") is now RESERVED.";
    }

    // Fault-Tolerant Notification Sender to Geraline (OR) & Hannah (ER)
    private void notifyDepartments(String message) {
        Map<String, String> payload = Map.of(
                "source", "BloodAndOrganMatchingService",
                "message", message
        );

        try {
            restTemplate.postForObject(orServiceUrl, payload, String.class);
        } catch (Exception e) {
            System.out.println("[Fault Tolerance] OR Service offline. Alert logged locally: " + message);
        }

        try {
            restTemplate.postForObject(erServiceUrl, payload, String.class);
        } catch (Exception e) {
            System.out.println("[Fault Tolerance] ER Service offline. Alert logged locally: " + message);
        }
    }
}

package com.hospital.donor_service.service;

import com.hospital.database.model.DatabaseHelper;
import com.hospital.database.model.Donor;
import com.hospital.database.model.Blood;
import com.hospital.database.model.Organ;
import com.hospital.donor_service.model.BloodBank;
import com.hospital.donor_service.model.OrganResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonorMatchingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final DatabaseHelper db = new DatabaseHelper();

    @Value("${or.service.url}")
    private String orServiceUrl;

    @Value("${er.service.url}")
    private String erServiceUrl;

    

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
        var id = db.insertBlood(
            bloodBank.getBloodType(),
            (int) (long) bloodBank.getDonorId(),
            "AVAILABLE",
            bloodBank.getDonationDate().toString(),
            0,
            bloodBank.getQuantityUnits()
        );

        var b = db.getBloodById(id);
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
            ret.add(new BloodBank(list.get(i)));
        }
        return ret;
    }

    public List<OrganResource> getAllOrgans() {
        var list = db.getOrgan(null, null, null, null);
        var ret = new ArrayList<OrganResource>();
        for(int i=0; i < list.size(); i++) {
            ret.add(new OrganResource(list.get(i)));
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
                Blood b = matchingBlood.get(j);
                if (compatibleTypes.get(i).equalsIgnoreCase(b.getBloodType())
                        && "AVAILABLE".equalsIgnoreCase(b.getStatus())
                        && b.getUnits() > 0) {
                    matchingStock.add(new BloodBank(b));
                }
            }
            for (Donor d : matchingDonorsOfType) {
                if (compatibleTypes.get(i).equalsIgnoreCase(d.getBloodType())
                        && d.isAvailable()) {
                    matchingDonors.add(d);
                }
            }
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
            ArrayList<Organ> matchingOrgans = db.getOrgan(organType, "AVAILABLE", null, compatibleTypes.get(i));
            ArrayList<Donor> matchingDonorsOfType = db.getDonor(null, null, compatibleTypes.get(i));
            for (int j = 0 ; j < matchingOrgans.size(); j++) {
                Organ o = matchingOrgans.get(j);
                if (compatibleTypes.get(i).equalsIgnoreCase(o.getBloodType())
                        && "AVAILABLE".equalsIgnoreCase(o.getStatus())
                        && organType.equalsIgnoreCase(o.getName())) {
                    availableOrgans.add(new OrganResource(o));
                }
            }
            for (Donor d : matchingDonorsOfType) {
                if (compatibleTypes.get(i).equalsIgnoreCase(d.getBloodType())
                        && d.isAvailable()) {
                    matchingDonors.add(d);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("requestedOrgan", organType);
        result.put("patientBloodType", patientBloodType);
        result.put("compatibleDonorTypes", compatibleTypes);
        result.put("availableOrgansInStorage", availableOrgans);
        result.put("registeredLiveDonors", matchingDonors.stream()
                .filter(d -> organType.equalsIgnoreCase(d.getOrganType()))
                .toList());
        return result;
    }

    // 4. Concurrency-Safe Reservation + Notify Teammates' Services
    public String reserveBlood(Long bloodId, int unitsToReserve) {

        if (unitsToReserve <= 0) {
            return "FAILED: Units must be greater than zero.";
        }

        Blood stock = db.getBloodById((int)(long)bloodId);

        if (stock == null) {
            return "FAILED: Blood record not found.";
        }

        int result = db.reserveBloodUnits((int)(long)bloodId, unitsToReserve);

        if (result != 1) {
            return "FAILED: Blood reservation could not be completed.";
        }

        notifyDepartments("Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood (ID: " + bloodId + ")");

        return "SUCCESS: Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood.";
    }

    public String reserveOrgan(Long organId) {

        Organ organ = db.getOrganById((int)(long)organId);

        if (organ == null) {
            return "FAILED: Organ record not found.";
        }

        int result = db.reserveOrgan((int)(long)organId);

        if (result != 1) {
            return "FAILED: Organ reservation could not be completed.";
        }

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


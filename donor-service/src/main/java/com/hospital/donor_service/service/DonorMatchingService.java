package com.hospital.donor_service.service;

import com.hospital.donor_service.model.BloodBank;
import com.hospital.donor_service.model.Donor;
import com.hospital.donor_service.model.OrganResource;
import com.hospital.donor_service.repository.BloodBankRepository;
import com.hospital.donor_service.repository.DonorRepository;
import com.hospital.donor_service.repository.OrganResourceRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DonorMatchingService {

    private final DonorRepository donorRepository;
    private final BloodBankRepository bloodBankRepository;
    private final OrganResourceRepository organRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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
        return donorRepository.save(donor);
    }

    public BloodBank addBloodStock(BloodBank bloodBank) {
        return bloodBankRepository.save(bloodBank);
    }

    public OrganResource addOrganResource(OrganResource organ) {
        return organRepository.save(organ);
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public List<BloodBank> getAllBloodInventory() {
        return bloodBankRepository.findAll();
    }

    public List<OrganResource> getAllOrgans() {
        return organRepository.findAll();
    }

    // 3. Search Compatible Blood & Organs
    public Map<String, Object> findCompatibleBlood(String patientBloodType, int requiredUnits) {
        List<String> compatibleTypes = getCompatibleDonorTypes(patientBloodType);
        List<BloodBank> matchingStock = bloodBankRepository.findByBloodTypeInAndQuantityUnitsGreaterThan(compatibleTypes, 0);
        List<Donor> matchingDonors = donorRepository.findByBloodTypeInAndAvailableTrue(compatibleTypes);

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
        List<OrganResource> availableOrgans = organRepository.findByOrganTypeIgnoreCaseAndBloodTypeInAndStatus(
                organType, compatibleTypes, "AVAILABLE");
        List<Donor> matchingDonors = donorRepository.findByOrganTypeIgnoreCaseAndBloodTypeInAndAvailableTrue(
                organType, compatibleTypes);

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
        BloodBank stock = bloodBankRepository.findById(bloodId)
                .orElseThrow(() -> new RuntimeException("Blood record not found"));

        if (stock.getQuantityUnits() < unitsToReserve) {
            return "FAILED: Not enough units available.";
        }

        stock.setQuantityUnits(stock.getQuantityUnits() - unitsToReserve);
        bloodBankRepository.save(stock);

        notifyDepartments("Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood (ID: " + bloodId + ")");
        return "SUCCESS: Reserved " + unitsToReserve + " unit(s) of " + stock.getBloodType() + " blood.";
    }

    @Transactional
    public String reserveOrgan(Long organId) {
        OrganResource organ = organRepository.findById(organId)
                .orElseThrow(() -> new RuntimeException("Organ record not found"));

        if (!"AVAILABLE".equalsIgnoreCase(organ.getStatus())) {
            return "FAILED: Organ is already " + organ.getStatus();
        }

        organ.setStatus("RESERVED");
        organRepository.save(organ);

        notifyDepartments("Reserved " + organ.getOrganType() + " (Blood Type: " + organ.getBloodType() + ", ID: " + organId + ")");
        return "SUCCESS: Organ ID " + organId + " (" + organ.getOrganType() + ") is now RESERVED.";
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

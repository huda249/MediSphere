package com.hospital.donor_service.repository;

import com.hospital.donor_service.model.BloodBank;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BloodBankRepository extends JpaRepository<BloodBank, Long> {
    List<BloodBank> findByBloodTypeInAndQuantityUnitsGreaterThan(List<String> bloodTypes, int minUnits);
}

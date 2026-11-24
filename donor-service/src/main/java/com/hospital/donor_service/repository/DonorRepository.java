package com.hospital.donor_service.repository;

import com.hospital.donor_service.model.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DonorRepository extends JpaRepository<Donor, Long> {
    List<Donor> findByBloodTypeInAndAvailableTrue(List<String> bloodTypes);
    List<Donor> findByOrganTypeIgnoreCaseAndBloodTypeInAndAvailableTrue(String organType, List<String> bloodTypes);
}
package com.hospital.donor_service.repository;

import com.hospital.donor_service.model.OrganResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrganResourceRepository extends JpaRepository<OrganResource, Long> {
    List<OrganResource> findByOrganTypeIgnoreCaseAndBloodTypeInAndStatus(String organType, List<String> bloodTypes, String status);
}

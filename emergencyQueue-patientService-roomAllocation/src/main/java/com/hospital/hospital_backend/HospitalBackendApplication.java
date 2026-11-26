package com.hospital.hospital_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({
		"com.hospital.database.model",
		"com.hospital.donor_service.model"
})
public class HospitalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HospitalBackendApplication.class, args);
	}

}

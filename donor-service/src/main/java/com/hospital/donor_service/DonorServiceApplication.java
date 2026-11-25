package com.hospital.donor_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan({
		"com.hospital.database.model",
		"com.hospital.donor_service.model"
})
public class DonorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonorServiceApplication.class, args);
	}

}

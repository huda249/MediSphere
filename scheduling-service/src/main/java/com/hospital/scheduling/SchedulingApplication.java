package com.hospital.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Entry point for the scheduling service. Spring Boot starts the HTTP server
// and discovers the controller and repository in this package.
@SpringBootApplication
public class SchedulingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulingApplication.class, args);
    }
}
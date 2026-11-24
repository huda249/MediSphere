package com.hospital.scheduling;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/scheduling")
class SchedulingController {
    record Request(
            @Positive int patientId,
            @NotBlank String department,
            @NotBlank String room,
            @NotBlank String bed,
            @NotBlank String equipment,
            @NotNull OffsetDateTime starts,
            @NotNull OffsetDateTime ends,
            @NotNull OffsetDateTime recoveryEnds
    ) {}

    private final BookingStore bookings;
    private final RestClient patients;

    SchedulingController(
            BookingStore bookings,
            @Value("${medisphere.patients-url}") String patientsUrl
    ) {
        this.bookings = bookings;

        // Bound the connection and read waits when calling the patient service.
        HttpClient http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(2));

        patients = RestClient.builder()
                .baseUrl(patientsUrl)
                .requestFactory(factory)
                .build();
    }

    @GetMapping("/resources")
    Map<String, ?> resources() {
        return Map.of(
                "rooms", BookingStore.ROOMS,
                "beds", BookingStore.BEDS,
                "equipment", BookingStore.EQUIPMENT
        );
    }

    @GetMapping("/availability")
    Map<String, ?> availability(
            @RequestParam String department,
            @RequestParam OffsetDateTime starts,
            @RequestParam OffsetDateTime ends,
            @RequestParam OffsetDateTime recoveryEnds
    ) {
        validateWindow(starts, ends, recoveryEnds);

        var scheduled = bookings.list();

        // The operating room and equipment are occupied during surgery.
        var duringSurgery = scheduled.stream()
                .filter(b -> b.starts().isBefore(ends) && b.ends().isAfter(starts))
                .toList();

        // The recovery bed is occupied from the end of surgery to the end of recovery.
        var duringRecovery = scheduled.stream()
                .filter(b -> b.ends().isBefore(recoveryEnds) && b.recoveryEnds().isAfter(ends))
                .toList();

        var rooms = BookingStore.ROOMS.entrySet().stream()
                .filter(room -> room.getValue().equals(department))
                .map(Map.Entry::getKey)
                .filter(id -> duringSurgery.stream().noneMatch(b -> b.room().equals(id)))
                .sorted()
                .toList();

        var beds = BookingStore.BEDS.stream()
                .filter(id -> duringRecovery.stream().noneMatch(b -> b.bed().equals(id)))
                .toList();

        var equipment = BookingStore.EQUIPMENT.entrySet().stream()
                .filter(item -> item.getValue().equals("READY"))
                .map(Map.Entry::getKey)
                .filter(id -> duringSurgery.stream().noneMatch(b -> b.equipment().equals(id)))
                .sorted()
                .toList();

        return Map.of("rooms", rooms, "beds", beds, "equipment", equipment);
    }

    @GetMapping("/bookings")
    List<BookingStore.Booking> schedule() {
        return bookings.list();
    }

    @PostMapping("/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    BookingStore.Booking book(@Valid @RequestBody Request request) {
        validateWindow(request.starts(), request.ends(), request.recoveryEnds());

        if (!request.department().equals(BookingStore.ROOMS.get(request.room()))
                || !BookingStore.BEDS.contains(request.bed())
                || !BookingStore.EQUIPMENT.containsKey(request.equipment())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Unknown resource or department mismatch");
        }

        if (!"READY".equals(BookingStore.EQUIPMENT.get(request.equipment()))) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Equipment is out of service");
        }

        // Finish the patient lookup before the booking transaction acquires the database lock.
        verifyPatient(request.patientId());

        // Recheck availability inside the transaction. Another request may have
        // booked the same resources since the client's last availability check.
        return bookings.reserve(request);
    }

    @DeleteMapping("/bookings/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void cancel(@PathVariable long id) {
        bookings.cancel(id);
    }

    private void validateWindow(
            OffsetDateTime starts, OffsetDateTime ends, OffsetDateTime recoveryEnds
    ) {
        if (!starts.isBefore(ends) || !ends.isBefore(recoveryEnds)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid surgery or recovery window");
        }
    }

    private void verifyPatient(int id) {
        String body;

        try {
            body = patients.get()
                    .uri("/api/patients/{id}", id)
                    .retrieve()
                    .body(String.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Patient service unavailable");
        }

        // The current patient API can return an empty response for an unknown ID.
        if (body == null || body.isBlank() || body.trim().equals("null")) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
        }
    }
}
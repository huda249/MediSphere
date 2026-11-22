package com.hospital.scheduling;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Repository
class BookingStore {
    // Demo resource catalog. Each operating room belongs to one department.
    static final Map<String, String> ROOMS = Map.of(
            "OR-1", "General",
            "OR-2", "General",
            "OR-3", "Orthopedics"
    );

    static final List<String> BEDS = List.of("BED-1", "BED-2");

    static final Map<String, String> EQUIPMENT = Map.of(
            "EQ-1", "READY",
            "EQ-2", "READY",
            "EQ-3", "MAINTENANCE"
    );

    record Booking(
            long id,
            int patientId,
            String room,
            String bed,
            String equipment,
            OffsetDateTime starts,
            OffsetDateTime ends,
            OffsetDateTime recoveryEnds
    ) {}

    private final JdbcClient db;

    BookingStore(JdbcClient db) {
        this.db = db;
    }

    List<Booking> list() {
        return db.sql("""
                SELECT id, patient_id, room, bed, equipment, starts, ends, recovery_ends
                FROM bookings
                WHERE NOT cancelled
                ORDER BY starts, id
                """)
                .query(Booking.class)
                .list();
    }

    @Transactional
    public Booking reserve(SchedulingController.Request request) {
        lockSchedule();

        // Room and equipment conflicts use the surgery window. Beds use the
        // recovery window. A patient is occupied throughout both windows.
        // Strict comparisons allow one booking to start when another ends.
        int conflicts = db.sql("""
                SELECT COUNT(*)
                FROM bookings
                WHERE NOT cancelled AND (
                    ((room = :room OR equipment = :equipment)
                        AND starts < :ends AND ends > :starts)
                    OR (bed = :bed AND ends < :recovery AND recovery_ends > :ends)
                    OR (patient_id = :patient
                        AND starts < :recovery AND recovery_ends > :starts)
                )
                """)
                .param("room", request.room())
                .param("equipment", request.equipment())
                .param("bed", request.bed())
                .param("patient", request.patientId())
                .param("starts", request.starts())
                .param("ends", request.ends())
                .param("recovery", request.recoveryEnds())
                .query(Integer.class)
                .single();

        if (conflicts > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Requested time overlaps an existing booking");
        }

        // Store all three resource assignments in one row so the booking
        // commits as a single unit.
        var key = new GeneratedKeyHolder();

        db.sql("""
                INSERT INTO bookings (patient_id, room, bed, equipment, starts, ends, recovery_ends)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """)
                .params(request.patientId(), request.room(), request.bed(),
                        request.equipment(), request.starts(), request.ends(),
                        request.recoveryEnds())
                .update(key, "id");

        return new Booking(
                key.getKey().longValue(), request.patientId(), request.room(),
                request.bed(), request.equipment(), request.starts(),
                request.ends(), request.recoveryEnds());
    }

    @Transactional
    public void cancel(long id) {
        lockSchedule();

        // Keep the booking record while freeing its resources for future requests.
        int updated = db.sql("UPDATE bookings SET cancelled = TRUE WHERE id = ?")
                .param(id)
                .update();

        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found");
        }
    }

    private void lockSchedule() {
        // The database holds this lock until commit or rollback, so concurrent
        // requests take turns checking conflicts and updating the schedule.
        db.sql("SELECT id FROM booking_lock WHERE id = 1 FOR UPDATE")
                .query(Integer.class)
                .single();
    }
}
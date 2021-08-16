package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.model.Booking;
import com.hubpan.IslandBooking.core.model.Campsite;
import com.hubpan.IslandBooking.core.model.CampsiteAvailability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReservationServiceImplUnitTest {


    private ReservationServiceImpl subject;

    private Campsite campsite;

    @BeforeEach
    void init() {
        subject = new ReservationServiceImpl();
        campsite = new Campsite("dummy");
    }

    @Test
    void testComputeAvailability_noReservation_shouldReturnOriginal() {

        LocalDateTime from = LocalDateTime.of(1995, 10, 10, 10, 10);
        LocalDateTime to = LocalDateTime.of(1995, 10, 20, 10, 10);

        List<CampsiteAvailability> availabilities = subject.computeAvailability(
                campsite, from, to, Collections.emptyList());

        assertEquals(1, availabilities.size());
        CampsiteAvailability first = availabilities.get(0);

        assertAll(
                () -> assertEquals(from, first.getFrom()),
                () -> assertEquals(to, first.getTo())
        );
    }

    @Test
    void testComputeAvailability_completeReserved_shouldReturnEmpty() {

        LocalDateTime from = LocalDateTime.of(1995, 10, 10, 10, 10);
        LocalDateTime to = LocalDateTime.of(1995, 10, 15, 10, 10);

        long days = Duration.between(from, to).toDays();
        List<Booking> collision = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            collision.add(newSimpleBooking(from.plus(i, ChronoUnit.DAYS), from.plus(i + 1, ChronoUnit.DAYS)));
        }

        List<CampsiteAvailability> availabilities = subject.computeAvailability(
                campsite, from, to, collision);

        assertEquals(0, availabilities.size());
    }

    @Test
    void testComputeAvailability_interleaveReserved_shouldReturnSome() {

        LocalDateTime from = LocalDateTime.of(1995, 10, 10, 10, 10);
        LocalDateTime to = LocalDateTime.of(1995, 10, 15, 10, 10);

        long days = Duration.between(from, to).toDays();
        List<Booking> collision = new ArrayList<>();
        // 10  11  12  13  14  15
        // | x |   | x |   | x |
        // x == collision
        for (int i = 0; i < days; i++) {
            if (i % 2 == 0) {
                collision.add(newSimpleBooking(from.plus(i, ChronoUnit.DAYS), from.plus(i + 1, ChronoUnit.DAYS)));
            }
        }

        List<CampsiteAvailability> availabilities = subject.computeAvailability(
                campsite, from, to, collision);

        assertEquals(2, availabilities.size());

        CampsiteAvailability first = availabilities.get(0);
        CampsiteAvailability second = availabilities.get(1);

        LocalDateTime firstOpeningFrom = LocalDateTime.of(1995, 10, 11, 10, 10);
        LocalDateTime firstOpeningTo = LocalDateTime.of(1995, 10, 12, 10, 10);
        LocalDateTime secondOpeningFrom = LocalDateTime.of(1995, 10, 13, 10, 10);
        LocalDateTime secondOpeningTo = LocalDateTime.of(1995, 10, 14, 10, 10);

        assertAll(
                () -> assertEquals(firstOpeningFrom, first.getFrom()),
                () -> assertEquals(firstOpeningTo, first.getTo()),
                () -> assertEquals(secondOpeningFrom, second.getFrom()),
                () -> assertEquals(secondOpeningTo, second.getTo())
        );
    }

    Booking newSimpleBooking(LocalDateTime from, LocalDateTime to) {
        Booking result = new Booking();
        result.setFrom(from);
        result.setTo(to);

        return result;
    }


}

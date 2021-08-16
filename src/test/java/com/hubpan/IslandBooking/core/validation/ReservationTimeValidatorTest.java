package com.hubpan.IslandBooking.core.validation;

import com.hubpan.IslandBooking.core.exceptions.ReservationTimeViolationException;
import com.hubpan.IslandBooking.core.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationTimeValidatorTest {

    private static LocalDateTime now = LocalDateTime.of(1995, 10, 10, 10, 10);

    private ReservationTimeValidator validator;

    @BeforeEach
    void init() {
        validator = new ReservationTimeValidator(now);
    }

    @ParameterizedTest
    @CsvSource({
            "1995-10-11T10:10:10.000,  1995-10-12T15:15:15.000",
            "1995-10-15T09:10:10.000,  1995-10-18T09:10:10.000",
            "1995-11-09T10:10:10.000,  1995-11-12T10:10:10.000"
    })
    void testReserve_validTimeRange_shouldNotThrowException(LocalDateTime from, LocalDateTime to) {
        assertDoesNotThrow(() -> validator.validate(newSimpleBooking(from, to)));
    }


    @ParameterizedTest
    @CsvSource({
            "1995-10-09T10:10:10.000,  1995-10-07T10:10:10.000",
            "1995-10-11T10:10:10.000,  1995-10-09T10:10:10.000",
            "1995-10-15T10:10:10.000,  1995-10-14T10:10:10.000"
    })
    void testReserve_fromAfterTo_shouldThrowException(LocalDateTime from, LocalDateTime to) {
        ReservationTimeViolationException ex = assertThrows(ReservationTimeViolationException.class, () ->
                validator.validate(newSimpleBooking(from, to)));

        assertEquals("\"from\" is before \"to\"", ex.getErrorMessage());
    }


    @ParameterizedTest
    @CsvSource({
            "1995-10-07T10:10:10.000,  1995-10-09T10:10:10.000",
            "1995-10-08T10:10:10.000,  1995-10-10T10:10:10.000",
            "1995-10-10T09:10:10.000,  1995-10-11T10:10:10.000"
    })
    void testReserve_timesBeforeNow_shouldThrowException(LocalDateTime from, LocalDateTime to) {
        ReservationTimeViolationException ex = assertThrows(ReservationTimeViolationException.class, () ->
                validator.validate(newSimpleBooking(from, to)));
        assertEquals("\"from\" is in the past", ex.getErrorMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "1995-10-10T10:10:10.000,  1995-10-11T10:10:10.000",
            "1995-10-11T10:09:10.000,  1995-10-12T10:10:10.000"
    })
    void testReserve_lessThan1dayArrival_shouldThrowException(LocalDateTime from, LocalDateTime to) {
        ReservationTimeViolationException ex = assertThrows(ReservationTimeViolationException.class, () ->
                validator.validate(newSimpleBooking(from, to)));

        assertEquals("reservation needs a minimum of 1 day ahead of arrival", ex.getErrorMessage());
    }


    @ParameterizedTest
    @CsvSource({
            "1995-11-11T10:10:10.000,  1995-11-12T10:10:10.000",
            "1995-11-15T10:10:10.000,  1995-11-16T10:10:10.000",
            "1995-12-08T10:10:10.000,  1995-12-11T10:10:10.000"
    })
    void testReserve_moreThan1MonthAdvanced_shouldThrowException(LocalDateTime from, LocalDateTime to) {
        ReservationTimeViolationException ex = assertThrows(ReservationTimeViolationException.class, () ->
                validator.validate(newSimpleBooking(from, to)));

        assertEquals("reservation cannot be made beyond 1 month in advance", ex.getErrorMessage());
    }


    @ParameterizedTest
    @CsvSource({
            "1995-10-13T10:10:10.000,  1995-10-17T10:10:10.000",
            "1995-10-13T11:10:10.000,  1995-10-17T10:10:10.000",
            "1995-10-13T09:10:10.000,  1995-10-16T11:10:10.000"
    })
    void testReserve_exceed3days_shouldThrowException(LocalDateTime from, LocalDateTime to) {
        ReservationTimeViolationException ex = assertThrows(ReservationTimeViolationException.class, () ->
                validator.validate(newSimpleBooking(from, to)));

        assertEquals("campsite can only be reserved for maximum of 3 days", ex.getErrorMessage());
    }

    Booking newSimpleBooking(LocalDateTime from, LocalDateTime to) {
        Booking booking = new Booking();
        booking.setFrom(from);
        booking.setTo(to);
        return booking;
    }

}

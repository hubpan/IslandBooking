package com.hubpan.IslandBooking.core.validation;

import com.hubpan.IslandBooking.core.exceptions.ReservationTimeViolationException;
import com.hubpan.IslandBooking.core.model.Booking;

import java.time.Duration;
import java.time.LocalDateTime;

public class ReservationTimeValidator implements IEntityValidator<Booking> {

    protected static final long THREE_DAYS_IN_MINUTES = 60 * 24 * 3;

    private LocalDateTime now;

    public ReservationTimeValidator(LocalDateTime now) {
        this.now = now;
    }

    @Override
    public void validate(Booking subject) {
        LocalDateTime from = subject.getFrom();
        LocalDateTime to = subject.getTo();

        if (from.isAfter(to)) {
            throw new ReservationTimeViolationException("\"from\" is before \"to\"");
        }

        if (now.isAfter(from)) {
            throw new ReservationTimeViolationException("\"from\" is in the past");
        }

        long daysAhead = Duration.between(now, from).toDays();
        if (daysAhead < 1) {
            throw new ReservationTimeViolationException("reservation needs a minimum of 1 day ahead of arrival");
        } else if (daysAhead > 30) {
            throw new ReservationTimeViolationException("reservation cannot be made beyond 1 month in advance");
        }

        Duration reservedLength = Duration.between(from, to);
        if (reservedLength.toMinutes() > THREE_DAYS_IN_MINUTES) {
            throw new ReservationTimeViolationException("campsite can only be reserved for maximum of 3 days");
        }

    }

}

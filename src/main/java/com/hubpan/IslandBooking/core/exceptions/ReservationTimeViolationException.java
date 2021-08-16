package com.hubpan.IslandBooking.core.exceptions;

import lombok.Getter;

@Getter
public class ReservationTimeViolationException extends RuntimeException {

    private final String errorMessage;

    public ReservationTimeViolationException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

}

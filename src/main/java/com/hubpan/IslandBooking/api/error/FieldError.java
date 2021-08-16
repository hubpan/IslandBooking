package com.hubpan.IslandBooking.api.error;

import lombok.Data;

@Data
public class FieldError {
    private final String fieldName;
    private final String errorMessage;
}



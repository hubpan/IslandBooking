package com.hubpan.IslandBooking.api.error;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data
@RequiredArgsConstructor
public class ApiErrorResponse {
    private final Instant timestamp;
    private final Object error;
    private final int status;
}

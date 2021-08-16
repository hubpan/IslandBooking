package com.hubpan.IslandBooking.api;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CampsiteAvailabilityDTO {
    private final CampsiteDTO campsite;
    private final ZonedDateTime from;
    private final ZonedDateTime to;
}

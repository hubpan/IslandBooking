package com.hubpan.IslandBooking.core.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampsiteAvailability {
    private final Campsite campSite;
    private final LocalDateTime from;
    private final LocalDateTime to;
}

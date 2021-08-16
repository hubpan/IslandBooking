package com.hubpan.IslandBooking.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
public class BookingDTO {
    private final Long id;

    @ApiModelProperty(notes = "a unique code representing the booking", name = "code", required = true)
    private final String code;

    @ApiModelProperty(notes = "the start of the booking period", name = "from", required = true)
    private final ZonedDateTime from;

    @ApiModelProperty(notes = "the end of the booking period", name = "to", required = true)
    private final ZonedDateTime to;

    @ApiModelProperty(notes = "the owner account", name = "owner", required = true)
    private final AccountDTO owner;

    @ApiModelProperty(notes = "the reserved campsite", name = "campSite", required = true)
    private final CampsiteDTO campSite;

}

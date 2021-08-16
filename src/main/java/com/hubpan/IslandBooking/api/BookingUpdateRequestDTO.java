package com.hubpan.IslandBooking.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
public class BookingUpdateRequestDTO {

    @NotNull
    @ApiModelProperty(notes = "id of the campsite", name = "campSiteId", required = true)
    private final Long campSiteId;

    @NotNull
    @FutureOrPresent
    @ApiModelProperty(notes = "the start of the reservation", name = "from", required = true)
    private final ZonedDateTime from;

    @NotNull
    @Future
    @ApiModelProperty(notes = "then end of the reservation", name = "to", required = true)
    private final ZonedDateTime to;
}
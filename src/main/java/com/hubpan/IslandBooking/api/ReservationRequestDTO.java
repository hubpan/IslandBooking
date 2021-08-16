package com.hubpan.IslandBooking.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Data
public class ReservationRequestDTO {

    @NotNull
    @ApiModelProperty(notes = "the id of campsite", name = "campSiteId", required = true)
    private final Long campSiteId;

    @Valid
    @NotNull
    private final AccountDTO owner;

    @NotNull
    @FutureOrPresent
    @ApiModelProperty(notes = "the start of reservation", name = "from", required = true)
    private final ZonedDateTime from;

    @NotNull
    @Future
    @ApiModelProperty(notes = "the end of reservation", name = "to", required = true)
    private final ZonedDateTime to;
}

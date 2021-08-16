package com.hubpan.IslandBooking.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CampsiteDTO {

    @ApiModelProperty(notes = "id of the campsite", name = "id", required = true)
    private final Long id;

    @ApiModelProperty(notes = "the name of campsite", name = "name", required = true)
    private final String name;

}

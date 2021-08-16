package com.hubpan.IslandBooking.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AccountDTO {

    private final Long id;

    @NotBlank
    @ApiModelProperty(notes = "first name", name = "firstName", required = true)
    private final String firstName;

    @NotBlank
    @ApiModelProperty(notes = "last name", name = "lastName", required = true)
    private final String lastName;

    @Email
    @ApiModelProperty(notes = "email", name = "email", required = true)
    private final String email;
}


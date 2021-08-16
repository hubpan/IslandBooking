package com.hubpan.IslandBooking.core.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String identifierName;
    private final String identifierValue;

    public ResourceNotFoundException(String resourceName, String identifierName, String identifierValue) {
        super(String.format("Unable to find %s with %s = %s",
                resourceName, identifierName, identifierValue));
        this.resourceName = resourceName;
        this.identifierName = identifierName;
        this.identifierValue = identifierValue;
    }
}

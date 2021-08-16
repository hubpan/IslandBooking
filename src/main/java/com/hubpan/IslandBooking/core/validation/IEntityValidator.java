package com.hubpan.IslandBooking.core.validation;

public interface IEntityValidator<T> {
    void validate(T subject);
}

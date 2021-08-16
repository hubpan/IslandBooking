package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.model.Account;
import com.hubpan.IslandBooking.core.model.Booking;
import com.hubpan.IslandBooking.core.model.CampsiteAvailability;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {

    Booking reserveByEmail(Long campsiteId, Account owner,
                           LocalDateTime from, LocalDateTime to);

    Booking save(Booking booking);

    Booking verifySave(Booking booking);

    Booking findById(Long id);

    Booking findByCode(String code);

    List<Booking> findAll();

    boolean deleteById(Long id);

    List<CampsiteAvailability> findAvailabilityByCampsite(Long campsiteId,
                                                          LocalDateTime from, LocalDateTime to);

}

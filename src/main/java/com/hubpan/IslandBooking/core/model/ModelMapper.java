package com.hubpan.IslandBooking.core.model;

import com.hubpan.IslandBooking.api.AccountDTO;
import com.hubpan.IslandBooking.api.BookingDTO;
import com.hubpan.IslandBooking.api.CampsiteAvailabilityDTO;
import com.hubpan.IslandBooking.api.CampsiteDTO;

import java.util.TimeZone;

public class ModelMapper {

    private TimeZone tz;

    public ModelMapper(TimeZone tz) {
        this.tz = tz;
    }

    public AccountDTO map(Account account) {
        return new AccountDTO(account.getId(), account.getFirstName(), account.getLastName(), account.getEmail());
    }

    public Account map(AccountDTO accountDTO) {

        Account account = new Account();
        account.setEmail(accountDTO.getEmail());
        account.setFirstName(accountDTO.getFirstName());
        account.setLastName(accountDTO.getLastName());

        return account;
    }

    public CampsiteDTO map(Campsite campSite) {
        return new CampsiteDTO(campSite.getId(), campSite.getName());
    }

    public BookingDTO map(Booking booking) {
       return new BookingDTO(booking.getId(),
                booking.getCode(),
                booking.getFrom().atZone(tz.toZoneId()),
                booking.getTo().atZone(tz.toZoneId()),
                map(booking.getOwner()), map(booking.getCampsite()));
    }

    public CampsiteAvailabilityDTO map(CampsiteAvailability avail) {
        return new CampsiteAvailabilityDTO(map(avail.getCampSite()),
                avail.getFrom().atZone(tz.toZoneId()),
                avail.getTo().atZone(tz.toZoneId()));
    }


}

package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.exceptions.ReservationTimeViolationException;
import com.hubpan.IslandBooking.core.exceptions.ResourceNotFoundException;
import com.hubpan.IslandBooking.core.model.Account;
import com.hubpan.IslandBooking.core.model.Booking;
import com.hubpan.IslandBooking.core.model.Campsite;
import com.hubpan.IslandBooking.core.model.CampsiteAvailability;
import com.hubpan.IslandBooking.core.validation.ReservationTimeValidator;
import com.hubpan.IslandBooking.db.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CampsiteService campSiteService;

    @Override
    public Booking reserveByEmail(Long campsiteId, Account owner,
                                  LocalDateTime from, LocalDateTime to) {

        Campsite campsite = campSiteService.findById(campsiteId);
        Account persistedAccount = accountService.createOrUpdate(owner);

        Booking booking = new Booking();
        booking.setFrom(from);
        booking.setTo(to);

        booking.setOwner(persistedAccount);
        booking.setCampsite(campsite);

        booking.setCode(UUID.randomUUID().toString());

        try {
            return save(booking);
        } catch (OptimisticLockingFailureException ex) {
            // try again as there is another transaction consuming the same target campsite
            return save(booking);
        }
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public Booking save(Booking booking) {
        booking = verifySave(booking);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking verifySave(Booking booking) {
        ReservationTimeValidator reservationTimeValidator = new ReservationTimeValidator(LocalDateTime.now());

        Pair<LocalDateTime, LocalDateTime> range = sanitizeBookingTimeRange(booking.getFrom(), booking.getTo());
        booking.setFrom(range.getFirst());
        booking.setTo(range.getSecond());

        reservationTimeValidator.validate(booking);

        // Optimistic locking
        // acquire the current version on the associated CampSite entity
        Campsite campsite = campSiteService.findByIdAndOptimisticLocked(booking.getCampsite().getId());

        // Check for conflicting bookings
        Iterable<Booking> collision = booking.getId() != null ?
                bookingRepository.findConflictingBookingExcludeSelection(booking.getFrom(), booking.getTo(),
                        campsite.getId(), booking.getId()) :
                bookingRepository.findConflictingBooking(booking.getFrom(), booking.getTo(),
                        campsite.getId());

        if (collision.iterator().hasNext()) {
            throw new ReservationTimeViolationException("the selected reservation time has already been taken");
        }
        return booking;
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("booking", "id", id.toString()));
    }

    @Override
    public Booking findByCode(String code) {
        return bookingRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("booking", "code", code));
    }

    @Override
    public List<Booking> findAll() {
        return StreamSupport.stream(
                bookingRepository.findAll().spliterator(),
                false).collect(Collectors.toList());
    }


    @Override
    public boolean deleteById(Long id) {
        bookingRepository.deleteById(id);
        return true;
    }

    @Override
    public List<CampsiteAvailability> findAvailabilityByCampsite(Long campsiteId,
                                                                 LocalDateTime from, LocalDateTime to) {

        if (from.isAfter(to)) {
            throw new ReservationTimeViolationException("\"from\" is before \"to\"");
        }

        Campsite campsite = campSiteService.findById(campsiteId);
        Iterable<Booking> collisionIterable = bookingRepository.findConflictingBooking(from, to, campsite.getId());

        List<Booking> collision = StreamSupport.stream(collisionIterable.spliterator(), false)
                .sorted(Comparator.comparing(Booking::getFrom)).collect(Collectors.toList());

        Pair<LocalDateTime, LocalDateTime> sanitizedTimeRange = sanitizeBookingTimeRange(from, to);

        return computeAvailability(campsite,
                sanitizedTimeRange.getFirst(), sanitizedTimeRange.getSecond(),
                collision);
    }

    protected List<CampsiteAvailability> computeAvailability(Campsite campsite,
        LocalDateTime from, LocalDateTime to, List<Booking> collision) {

        List<CampsiteAvailability> result = new ArrayList<>();

        LocalDateTime left = from;
        LocalDateTime right = to;

        for (Booking c : collision) {

            // ignore collision block if it's completely on the left of search range
            if (c.getTo().isEqual(left) || c.getTo().isBefore(left)) {
                continue;
            }

            // ignore collision block if it's completely on the right of search range
            if (c.getFrom().isEqual(right) || c.getFrom().isAfter(right)) {
                continue;
            }

            // if there is an opening on the left
            if (c.getFrom().isAfter(left)) {
                result.add(new CampsiteAvailability(campsite, left, c.getFrom()));
            }

            left = c.getTo();

            // if there is no more possible openings
            if (!left.isBefore(right)) {
                break;
            }
        }

        if (left.isBefore(right)) {
            result.add(new CampsiteAvailability(campsite, left, right));
        }

        return result;
    }


    protected Pair<LocalDateTime, LocalDateTime> sanitizeBookingTimeRange(LocalDateTime from, LocalDateTime to) {
        if (from.equals(to)) {
            from = from.with(LocalTime.of(0, 1));
            to = to.plusDays(1).with(LocalTime.of(0, 0));
        } else {
            from = from.with(LocalTime.of(0, 1));
            if (!(to.getHour() == 0 && to.getMinute() == 0)) {
                // advance "to" to the nearest check-out time in the future
                to = to.plusDays(1).with(LocalTime.of(0, 0));
            }
        }
        return Pair.of(from, to);
    }
}

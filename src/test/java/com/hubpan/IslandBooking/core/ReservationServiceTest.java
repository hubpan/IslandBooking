package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.exceptions.ReservationTimeViolationException;
import com.hubpan.IslandBooking.core.model.Account;
import com.hubpan.IslandBooking.core.model.Booking;
import com.hubpan.IslandBooking.core.model.Campsite;
import com.hubpan.IslandBooking.db.AccountRepository;
import com.hubpan.IslandBooking.db.BookingRepository;
import com.hubpan.IslandBooking.db.CampsiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ReservationServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CampsiteRepository campsiteRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    private LocalDateTime now;

    private Account ownerAccount1;
    private Account ownerAccount2;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        now = LocalDate.now().atStartOfDay().plus(1, ChronoUnit.DAYS);
        ownerAccount1 = accountRepository.findById(1L).orElseThrow();
        ownerAccount2 = accountRepository.findById(2L).orElseThrow();
    }


    @Test
    void testReserveSequential_conflictingBooking_shouldNotSave() {

        Campsite targetCampsite = campsiteRepository.findById(1L).orElseThrow();

        Booking persisted = transactionTemplate.execute((status) -> {
            return reservationService.save(newSimpleBooking(targetCampsite, ownerAccount1, 3, 5));
        });

        assertAll(
                () -> assertNotNull(persisted),
                () -> assertNotNull(persisted.getId())
        );

        ReservationTimeViolationException violation = assertThrows(ReservationTimeViolationException.class, () ->
                transactionTemplate.execute((status) -> {
                    return reservationService.save(newSimpleBooking(targetCampsite, ownerAccount2,
                            4, 6));
                }));

        assertEquals("the selected reservation time has already been taken", violation.getErrorMessage());

        List<Booking> bookings =
                StreamSupport.stream(bookingRepository.findByCampsiteId(targetCampsite.getId()).spliterator(), false)
                        .collect(Collectors.toList());
        assertEquals(1, bookings.size());
        Booking first = bookings.get(0);
        assertAll(
                () -> assertEquals(now.plus(3, ChronoUnit.DAYS).getDayOfMonth(), first.getFrom().getDayOfMonth()),
                () -> assertEquals(now.plus(5, ChronoUnit.DAYS).getDayOfMonth(), first.getTo().getDayOfMonth()),
                () -> assertEquals(ownerAccount1.getId(), first.getOwner().getId())
        );
    }

    @Test
    void testReserveParallel_conflictingBooking_shouldNotSave() throws InterruptedException {

        Campsite targetCampsite = campsiteRepository.findById(2L).orElseThrow();

        final CountDownLatch mainThreadLatch = new CountDownLatch(1);
        final CountDownLatch workerThreadLatch = new CountDownLatch(1);

        /**
         * Thread should throw ObjectOptimisticLockingFailureException when attempting to commit transaction
         */
        Thread thread1 = new Thread(() -> {
            transactionTemplate.execute((status) -> {
                Booking verified = reservationService.verifySave(newSimpleBooking(targetCampsite, ownerAccount1, 7, 9));
                mainThreadLatch.countDown();
                try {
                    workerThreadLatch.await();
                } catch (InterruptedException ignored) {
                }
                // verified won't be saved because of detection of stale version on Campsite
                return bookingRepository.save(verified);
            });
        });
        thread1.start();

        mainThreadLatch.await();
        transactionTemplate.execute((status) -> {
            Booking persisted = reservationService.save(newSimpleBooking(targetCampsite, ownerAccount2, 8, 10));
            return persisted;
        });
        workerThreadLatch.countDown();

        thread1.join();
        List<Booking> bookings =
                StreamSupport.stream(bookingRepository.findByCampsiteId(targetCampsite.getId()).spliterator(), false)
                        .collect(Collectors.toList());
        assertEquals(1, bookings.size());
        Booking first = bookings.get(0);
        assertAll(
                () -> assertEquals(now.plus(8, ChronoUnit.DAYS).getDayOfMonth(), first.getFrom().getDayOfMonth()),
                () -> assertEquals(now.plus(10, ChronoUnit.DAYS).getDayOfMonth(), first.getTo().getDayOfMonth()),
                () -> assertEquals(ownerAccount2.getId(), first.getOwner().getId())
        );
    }


    Booking newSimpleBooking(Campsite campsite, Account owner, int fromDayOffset, int toDayOffset) {
        Booking booking = new Booking();
        booking.setFrom(now.plus(fromDayOffset, ChronoUnit.DAYS));
        booking.setTo(now.plus(toDayOffset, ChronoUnit.DAYS));
        booking.setOwner(owner);
        booking.setCampsite(campsite);
        booking.setCode(UUID.randomUUID().toString());
        return booking;
    }

}

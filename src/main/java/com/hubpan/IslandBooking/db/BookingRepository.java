package com.hubpan.IslandBooking.db;

import com.hubpan.IslandBooking.core.model.Booking;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    Optional<Booking> findByCode(String code);

    @Query("select b from Booking b where b.campsite.id = :campsiteId")
    Iterable<Booking> findByCampsiteId(@Param("campsiteId") long campId);

    @Query("select b from Booking b where b.campsite.id = :campsiteId AND b.from <= :to AND b.to >= :from")
    Iterable<Booking> findConflictingBooking(
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
            @Param("campsiteId") long campId);

    @Query("select b from Booking b where b.campsite.id = :campsiteId AND b.from <= :to AND b.to >= :from AND b.id <> :excludedId")
    Iterable<Booking> findConflictingBookingExcludeSelection(
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to,
            @Param("campsiteId") long campId, @Param("excludedId") long excludedId);

}

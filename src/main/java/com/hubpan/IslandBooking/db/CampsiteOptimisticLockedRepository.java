package com.hubpan.IslandBooking.db;

import com.hubpan.IslandBooking.core.model.Campsite;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface CampsiteOptimisticLockedRepository extends CrudRepository<Campsite, Long> {

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    Optional<Campsite> findById(Long id);

}

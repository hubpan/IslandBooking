package com.hubpan.IslandBooking.db;

import com.hubpan.IslandBooking.core.model.Campsite;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CampsiteRepository extends CrudRepository<Campsite, Long> {
}

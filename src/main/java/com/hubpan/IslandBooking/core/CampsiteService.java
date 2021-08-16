package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.model.Campsite;

import java.util.List;

public interface CampsiteService {

    List<Campsite> findAll();

    Campsite findById(Long id);

    Campsite findByIdAndOptimisticLocked(Long id);
}

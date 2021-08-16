package com.hubpan.IslandBooking.core;

import com.hubpan.IslandBooking.core.exceptions.ResourceNotFoundException;
import com.hubpan.IslandBooking.core.model.Campsite;
import com.hubpan.IslandBooking.db.CampsiteOptimisticLockedRepository;
import com.hubpan.IslandBooking.db.CampsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Service
public class CampsiteServiceImpl implements CampsiteService {

    @Autowired
    CampsiteRepository campSiteRepository;

    @Autowired
    CampsiteOptimisticLockedRepository campSiteOptimisticLockedRepository;

    @Override
    public Campsite findById(Long id) {
        return campSiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("campsite", "id", id.toString()));
    }

    @Override
    public Campsite findByIdAndOptimisticLocked(Long id) {
        return campSiteOptimisticLockedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("campsite", "id", id.toString()));
    }

    @Override
    public List<Campsite> findAll() {
        return StreamSupport.stream(
                campSiteRepository.findAll().spliterator(),
                false).collect(Collectors.toList());
    }
}

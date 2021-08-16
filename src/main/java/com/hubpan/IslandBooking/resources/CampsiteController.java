package com.hubpan.IslandBooking.resources;

import com.hubpan.IslandBooking.api.CampsiteAvailabilityDTO;
import com.hubpan.IslandBooking.api.CampsiteDTO;
import com.hubpan.IslandBooking.core.ReservationService;
import com.hubpan.IslandBooking.core.model.Campsite;
import com.hubpan.IslandBooking.core.CampsiteService;
import com.hubpan.IslandBooking.core.model.ModelMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/campsites")
public class CampsiteController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CampsiteService campSiteService;

    @Autowired
    private ReservationService reservationService;

    @ApiOperation(value = "list all campsites")
    @GetMapping()
    public List<CampsiteDTO> findAll() {
        Collection<Campsite> campsites = campSiteService.findAll();
        return campsites.stream().map(modelMapper::map).collect(Collectors.toList());
    }

    @ApiOperation(value = "retrieve availability of an existing campsite")
    @GetMapping("/{id}/availability")
    public Collection<CampsiteAvailabilityDTO> getAvailabilityById(
            @ApiParam(
                    name =  "id",
                    value = "the id of an existing campsite",
                    required = true)
            @PathVariable Long id,
            @ApiParam(
                    name =  "from",
                    value = "search for availability since this time",
                    required = false)
            @RequestParam(required = false) ZonedDateTime from,
            @ApiParam(
                    name =  "to",
                    value = "search for availability until this time",
                    required = false)
            @RequestParam(required = false) ZonedDateTime to) {

        if (from == null && to == null) {
            from = ZonedDateTime.now();
        }

        if (from == null) {
            from = to.minusDays(30);
        }

        if (to == null) {
            to = from.plusDays(30);
        }

        return reservationService.findAvailabilityByCampsite(id, from.toLocalDateTime(), to.toLocalDateTime())
                .stream().map(modelMapper::map).collect(Collectors.toList());
    }


}

package com.hubpan.IslandBooking.resources;

import com.hubpan.IslandBooking.api.BookingDTO;
import com.hubpan.IslandBooking.api.BookingUpdateRequestDTO;
import com.hubpan.IslandBooking.api.ReservationRequestDTO;
import com.hubpan.IslandBooking.core.CampsiteService;
import com.hubpan.IslandBooking.core.ReservationService;
import com.hubpan.IslandBooking.core.model.Booking;
import com.hubpan.IslandBooking.core.model.ModelMapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private CampsiteService campSiteService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ModelMapper modelMapper;

    @ApiOperation(value = "create a booking that reserves an existing campsite")
    @PostMapping()
    public BookingDTO reserve(
            @ApiParam(
                    name =  "request",
                    value = "reservation request",
                    required = true)
            @Valid
            @RequestBody
            ReservationRequestDTO request) {

        Booking booking = reservationService.reserveByEmail(
                request.getCampSiteId(),
                modelMapper.map(request.getOwner()),
                request.getFrom().toLocalDateTime(),
                request.getTo().toLocalDateTime());

        return modelMapper.map(booking);
    }

    @ApiOperation(value = "list all bookings")
    @GetMapping()
    public List<BookingDTO> getAll() {
        return reservationService.findAll().stream().map(modelMapper::map).collect(Collectors.toList());
    }

    @ApiOperation(value = "retrieve an existing booking by id or code")
    @GetMapping("/{idOrCode}")
    public BookingDTO findById(
            @ApiParam(
                    name =  "idOrCode",
                    value = "id or code that uniquely identify a booking",
                    required = true)
            @PathVariable String idOrCode) {
        return modelMapper.map(retrieveBookingByIdOrCode(idOrCode));
    }

    @ApiOperation(value = "update an existing booking")
    @PostMapping("/{idOrCode}")
    public BookingDTO updateById(
            @ApiParam(
                    name =  "idOrCode",
                    value = "id or code that uniquely identify a booking",
                    required = true)
            @PathVariable String idOrCode,
            @Valid
            @RequestBody
            BookingUpdateRequestDTO request) {
        Booking existing = retrieveBookingByIdOrCode(idOrCode);

        if (existing.getCampsite().getId() != request.getCampSiteId()) {
            existing.setCampsite(campSiteService.findById(request.getCampSiteId()));
        }

        existing.setFrom(request.getFrom().toLocalDateTime());
        existing.setTo(request.getTo().toLocalDateTime());

        return modelMapper.map(reservationService.save(existing));
    }

    @ApiOperation(value = "delete an existing booking")
    @DeleteMapping("/{idOrCode}")
    public void deleteById(
            @ApiParam(
                    name =  "idOrCode",
                    value = "id or code that uniquely identify a booking",
                    required = true)
            @PathVariable String idOrCode) {
        Booking existing = retrieveBookingByIdOrCode(idOrCode);
        reservationService.deleteById(existing.getId());
    }

    protected Booking retrieveBookingByIdOrCode(String idOrCode) {

        try {
            long id = Long.parseLong(idOrCode);
            return reservationService.findById(id);
        } catch (Exception ignored) {
        }

        return reservationService.findByCode(idOrCode);
    }

}

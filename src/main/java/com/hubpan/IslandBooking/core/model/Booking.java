package com.hubpan.IslandBooking.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "from_ts", columnDefinition = "TIMESTAMP")
    private LocalDateTime from;

    @Column(name = "to_ts", columnDefinition = "TIMESTAMP")
    private LocalDateTime to;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Account owner;

    @OneToOne
    @JoinColumn(name = "campsite_id", nullable = false)
    private Campsite campsite;

}

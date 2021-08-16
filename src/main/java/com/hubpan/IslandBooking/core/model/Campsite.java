package com.hubpan.IslandBooking.core.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "campsites")
@Getter
@Setter
@NoArgsConstructor
public class Campsite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Version
    private Long version;

    public Campsite(String name) {
        this.name = name;
    }

}

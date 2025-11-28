package com.example.pdp_project.entity;

import com.example.pdp_project.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

    @ManyToOne
    private Hotel hotel;

    private LocalDate checkIn;
    private LocalDate checkOut;

    @ManyToOne
    private Trip trip;

    @ManyToOne
    private TgUser user;

    private int days;

    private LocalDate bookingDate = LocalDate.now();

}

package com.example.pdp_project.controller;

import com.example.pdp_project.entity.Booking;
import com.example.pdp_project.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hotel/{hotelId}")
    public ResponseEntity<?> bookHotel(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam Integer guests,
            @RequestParam Integer rooms
    ) {
        Booking booking = bookingService.book(hotelId, checkIn, checkOut, guests, rooms);
        return ResponseEntity.ok(booking);
    }
}

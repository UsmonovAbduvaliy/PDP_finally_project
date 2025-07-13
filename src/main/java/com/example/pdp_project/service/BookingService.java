package com.example.pdp_project.service;

import com.example.pdp_project.entity.Booking;
import com.example.pdp_project.entity.Hotel;
import com.example.pdp_project.repo.BookingRepository;
import com.example.pdp_project.repo.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepo;
    private final HotelRepository hotelRepo;

    public Booking book(Long hotelId, LocalDate checkIn, LocalDate checkOut, int guests, int rooms) {
        Hotel hotel = hotelRepo.findById(hotelId).orElseThrow();

        Booking booking = new Booking();
        booking.setHotel(hotel);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuests(guests);
        booking.setRooms(rooms);

        return bookingRepo.save(booking);
    }
}


package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Booking;
import com.example.pdp_project.entity.TgUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserOrderByBookingDateDesc(TgUser tgUser);

    @Query("SELECT DISTINCT b.user FROM Booking b WHERE b.trip.country = :country")
    List<TgUser> findUsersByTripCountry(@Param("country") String country);


}
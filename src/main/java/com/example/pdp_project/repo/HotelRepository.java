package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
  @Query("""
    select h from Hotel h
    where lower(h.city) like lower(concat('%', :city, '%'))
      and h.availableRooms >= :rooms
      and h.maxGuestsPerRoom * h.availableRooms >= :guests
      and not exists (
          select b from Booking b
          where b.hotel = h
            and b.checkIn < :checkOut
            and b.checkOut > :checkIn
      )
""")
  List<Hotel> searchAvailableHotels(
          @Param("city") String city,
          @Param("rooms") Integer rooms,
          @Param("guests") Integer guests,
          @Param("checkIn") LocalDate checkIn,
          @Param("checkOut") LocalDate checkOut
  );

}
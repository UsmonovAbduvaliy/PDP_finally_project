package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Integer> {
  @Query("select h from Hotel h where lower(h.city) like lower(concat('%', :city,'%'))")
  List<Hotel> searchByCity(@Param("city") String city);
}
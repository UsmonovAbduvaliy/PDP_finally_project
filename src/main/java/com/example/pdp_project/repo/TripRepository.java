package com.example.pdp_project.repo;

import com.example.pdp_project.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findTop8ByOrderByRatingDesc();

}
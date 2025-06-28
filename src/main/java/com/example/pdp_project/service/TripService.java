package com.example.pdp_project.service;

import com.example.pdp_project.entity.Trip;
import com.example.pdp_project.repo.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository repo;
    public List<Trip> popularTrips() {
        return repo.findTop8ByOrderByRatingDesc();
    }
    public Trip getOneTrip(Integer id){return repo.findById(id).orElseThrow();}
}

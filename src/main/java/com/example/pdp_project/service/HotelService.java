package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.HotelSearchCriteria;
import com.example.pdp_project.entity.Hotel;
import com.example.pdp_project.repo.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository repo;
    public List<Hotel> search(HotelSearchCriteria c) {
        return repo.searchAvailableHotels(
                c.city(),
                c.rooms(),
                c.guests(),
                c.checkIn(),
                c.checkOut()
        );
    }
    public Hotel get(Long id){return repo.findById(id).orElseThrow();}
}
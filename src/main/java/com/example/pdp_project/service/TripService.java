package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.TripDTO;
import com.example.pdp_project.entity.Attachment;
import com.example.pdp_project.entity.Trip;
import com.example.pdp_project.repo.CategoryRepository;
import com.example.pdp_project.repo.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository repo;
    private final CategoryRepository categoryRepository;

    public Page<Trip> popularTrips(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        return repo.findAll(pageable);
    }

    public Optional<Trip> getOneTrip(Long id) {
        return repo.findById(id);
    }

    public Trip save(TripDTO tripDTO, Attachment attachment) {

        Trip trip = new Trip();
        trip.setCategory(categoryRepository.findById(tripDTO.getCategoryId()).get());
        trip.setRating(tripDTO.getRating());
        trip.setDescription(tripDTO.getDescription());
        trip.setCountry(tripDTO.getCountry());
        trip.setTitle(tripDTO.getTitle());
        trip.setPrice(tripDTO.getPrice());
        trip.setPhoto(attachment);
        return repo.save(trip);
    }
}

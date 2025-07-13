package com.example.pdp_project.controller;

import com.example.pdp_project.entity.Trip;
import com.example.pdp_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/trips") @RequiredArgsConstructor
public class TripController {
    private final TripService service;
    @GetMapping("/popular")
    public ResponseEntity<?> popular(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.popularTrips(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable Long id){
        Optional<Trip> oneTrip = service.getOneTrip(id);
        if(oneTrip.isPresent()){
            return ResponseEntity.ok(oneTrip.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }
}

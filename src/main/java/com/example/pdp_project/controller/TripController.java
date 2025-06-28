package com.example.pdp_project.controller;

import com.example.pdp_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trips") @RequiredArgsConstructor
public class TripController {
    private final TripService service;
    @GetMapping("/popular")
    public ResponseEntity<?> popular(){
        return ResponseEntity.ok(service.popularTrips());
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable Integer id){
        return ResponseEntity.ok(service.getOneTrip(id));
    }
}

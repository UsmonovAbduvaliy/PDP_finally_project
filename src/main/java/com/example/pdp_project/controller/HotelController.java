package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.HotelSearchCriteria;
import com.example.pdp_project.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hotels") @RequiredArgsConstructor
public class HotelController {
    private final HotelService service;
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody HotelSearchCriteria c){
        return ResponseEntity.ok(service.search(c));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable Long id){
        return ResponseEntity.ok(service.get(id));
    }
}

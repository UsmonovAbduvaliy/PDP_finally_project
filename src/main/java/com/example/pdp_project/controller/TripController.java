package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.TripDTO;
import com.example.pdp_project.entity.Attachment;
import com.example.pdp_project.entity.Trip;
import com.example.pdp_project.repo.AttachmentRepository;
import com.example.pdp_project.repo.CategoryRepository;
import com.example.pdp_project.repo.TripRepository;
import com.example.pdp_project.service.AttachmentService;
import com.example.pdp_project.service.BotService;
import com.example.pdp_project.service.CategoryService;
import com.example.pdp_project.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/trips") @RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    private final BotService botService;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/popular")
    public ResponseEntity<?> popular(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(tripService.popularTrips(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable Long id){
        Optional<Trip> oneTrip = tripService.getOneTrip(id);
        if(oneTrip.isPresent()){
            return ResponseEntity.ok(oneTrip.get());
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<?> addTrip(@RequestBody TripDTO tripDTO ) throws IOException {

        System.out.println(tripDTO.getAttachmentId() + "============================");
            Attachment attachment = attachmentRepository.findById(tripDTO.getAttachmentId()).orElse(null);
        Trip save = tripService.save(tripDTO, attachment);
        botService.addTripForChannel(save);
        return ResponseEntity.ok().build();
    }

}

package com.example.pdp_project.controller;

import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.repo.UserRepository;
import com.example.pdp_project.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> byEmail = userRepository.findByEmail(loginRequest.getEmail());
        if (byEmail.isPresent()) {
            if (!byEmail.get().getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.ok(jwtService.generateToken(byEmail.get()));
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
}

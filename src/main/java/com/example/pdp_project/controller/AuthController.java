package com.example.pdp_project.controller;

import com.example.pdp_project.config.security.JwtService;
import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.RegisterRequest;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.repo.UserRepository;
import com.example.pdp_project.service.SendMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> byEmail = userRepository.findByEmail(loginRequest.getEmail());
        if (byEmail.isPresent()) {
            if (!byEmail.get().getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).build();
            }
            if (!byEmail.get().getIsVerified()) {
                return ResponseEntity.notFound().build();
            }
//            var auth = UsernamePasswordAuthenticationToken(
//                    loginRequest.getEmail(),
//                    loginRequest.getPassword()
//            );
//            authenticationManager.authenticate(auth);
            return ResponseEntity.ok(jwtService.generateToken(byEmail.get()));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, @RequestParam MultipartFile image) {
        Optional<User> byEmail = userRepository.findByEmail(registerRequest.getEmail());

        User user;
        user = byEmail.orElseGet(User::new);
        user.setPassword(registerRequest.getPassword());
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setIsActive(true);
        String code = String.valueOf((int) (Math.random() * 9000) + 1000);
        Thread thread = new Thread(() -> {
            sendMailService.sendVerificationCode(user.getEmail(), code);
        });
        thread.start();
        user.setVerificationCode(code);
        User save = userRepository.save(user);
        return ResponseEntity.ok(save);
    }

    @PostMapping("/verify/{id}")
    public ResponseEntity<?> checkVerificationCode( @PathVariable Integer id,@RequestParam String code) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            if (user.getVerificationCode().equals(code)) {
                user.setIsVerified(true);
                userRepository.save(user);
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.badRequest().build();
    }
}

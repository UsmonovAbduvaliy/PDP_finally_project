package com.example.pdp_project.controller;
import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.RegisterRequest;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        User user = authService.getLoginUser(loginRequest);
        if (user != null) {
            return ResponseEntity.ok().body(user);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, @RequestParam(value = "photo", required = false) MultipartFile image) {
       User user = authService.getRegisteredUser(registerRequest,image);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify/{id}")
    public ResponseEntity<?> checkVerificationCode( @PathVariable Integer id,@RequestParam String code) {
        User user = authService.chackVerificationCode(id, code);
        if(user!=null){
            return ResponseEntity.ok(user);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
}

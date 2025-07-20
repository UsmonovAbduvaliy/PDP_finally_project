package com.example.pdp_project.controller;
import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.RegisterRequest;
import com.example.pdp_project.dto.response.LoginResponse;
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
        LoginResponse response = authService.getLoginUser(loginRequest);
        if (response != null) {
            return ResponseEntity.ok().body(response);
        }else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@ModelAttribute RegisterRequest registerRequest, @RequestParam(value = "photo", required = false) MultipartFile image) {
       User user = authService.getRegisteredUser(registerRequest,image);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/verify/{id}")
    public ResponseEntity<?> checkVerificationCode( @PathVariable Long id,@RequestParam String code) {
        User user = authService.chackVerificationCode(id, code);
        if(user!=null){
            return ResponseEntity.ok().body("Success");
        }else {
            return ResponseEntity.badRequest().build();
        }
    }
}

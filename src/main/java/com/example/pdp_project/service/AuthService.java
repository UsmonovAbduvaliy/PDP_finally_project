package com.example.pdp_project.service;

import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.RegisterRequest;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final SendMailService sendMailService;

    public User getRegisteredUser(RegisterRequest registerRequest, MultipartFile image) {
        Optional<User> byEmail = Optional.ofNullable(userRepository.findByEmail(registerRequest.getEmail()));

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
        return userRepository.save(user);
    }

    public User chackVerificationCode(Integer id, String code) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            if (user.getVerificationCode().equals(code)) {
                user.setIsVerified(true);
               return userRepository.save(user);

            }
        }
        return null;
    }

    public User getLoginUser(LoginRequest loginRequest) {
        Optional<User> byEmail = Optional.ofNullable(userRepository.findByEmail(loginRequest.getEmail()));
        if (byEmail.isPresent()) {
            if (!byEmail.get().getPassword().equals(loginRequest.getPassword())) {
                return null;
            }
            if (!byEmail.get().getIsVerified()) {
                return null;
            }
            return byEmail.get();
        } else {
            return null;
        }
    }
}

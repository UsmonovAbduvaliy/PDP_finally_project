package com.example.pdp_project.service;

import com.example.pdp_project.config.security.JwtService;
import com.example.pdp_project.dto.request.LoginRequest;
import com.example.pdp_project.dto.request.RegisterRequest;
import com.example.pdp_project.dto.response.LoginResponse;
import com.example.pdp_project.entity.Attachment;
import com.example.pdp_project.entity.Role;
import com.example.pdp_project.entity.User;
import com.example.pdp_project.repo.AttachmentRepository;
import com.example.pdp_project.repo.RoleRepository;
import com.example.pdp_project.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {


    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final AttachmentRepository attachmentRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;

    @SneakyThrows
    public User getRegisteredUser(RegisterRequest registerRequest, MultipartFile image) {
        if(!registerRequest.getPassword().equals(registerRequest.getPasswordRepeat())){
            throw new IllegalArgumentException("Password dont match !");
        }

        User byEmail =userRepository.findByEmail(registerRequest.getEmail());
        if (byEmail != null) {
            throw new IllegalArgumentException("Email already exist !");
        }
        User user = new User();
        user.setPassword(registerRequest.getPassword());
        user.setPhone(registerRequest.getPhone());
        user.setEmail(registerRequest.getEmail());
        user.setName(registerRequest.getName());
        user.setIsActive(true);
        Attachment attachment;
        if(image!=null && !image.isEmpty()){
             attachment = Attachment.builder()
                    .originalName(image.getOriginalFilename())
                    .contentType(image.getContentType())
                    .content(image.getBytes())
                    .build();
           Attachment savedAttachment = attachmentRepository.save(attachment);
            user.setPhoto(savedAttachment);
        }

        String code = String.valueOf((int) (Math.random() * 9000) + 1000);
        Thread thread = new Thread(() -> sendMailService.sendVerificationCode(user.getEmail(), code));
        thread.start();
        user.setVerificationCode(code);
        user.setRoles(getRole());
        return userRepository.save(user);
    }

    private List<Role> getRole() {
        Role role = roleRepository.findByName("USER").orElse(new Role("USER"));
        Role save = roleRepository.save(role);
        return List.of(save);
    }

    public User chackVerificationCode(Long id, String code) {
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

    public LoginResponse getLoginUser(LoginRequest loginRequest) {
        Optional<User> byEmail = Optional.ofNullable(userRepository.findByEmail(loginRequest.getEmail()));
        if (byEmail.isPresent()) {
            if (!byEmail.get().getPassword().equals(loginRequest.getPassword())) {
                return null;
            }
            if (!byEmail.get().getIsVerified()) {
                return null;
            }
            return new LoginResponse(jwtService.generateToken(byEmail.get()),jwtService.generateRefreshToken(byEmail.get()));
        } else {
            return null;
        }
    }
}

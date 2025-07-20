package com.example.pdp_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    String name;
    String password;
    String passwordRepeat;
    String email;
    String phone;
}

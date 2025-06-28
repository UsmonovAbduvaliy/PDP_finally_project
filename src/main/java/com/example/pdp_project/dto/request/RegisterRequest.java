package com.example.pdp_project.dto.request;

import lombok.Value;

@Value
public class RegisterRequest {
    String name;
    String password;
    String email;
    String phone;
}

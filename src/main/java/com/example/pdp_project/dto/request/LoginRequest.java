package com.example.pdp_project.dto.request;

import lombok.Value;

@Value
public class LoginRequest {
    String email;
    String password;
}

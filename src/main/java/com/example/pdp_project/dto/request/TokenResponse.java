package com.example.pdp_project.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}

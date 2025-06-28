package com.example.pdp_project.service;

import com.example.pdp_project.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(signKey())
                .compact();
    }

    private SecretKey signKey() {
        return Keys.hmacShaKeyFor("12345678901234567890123456789012".getBytes());
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(signKey())
                    .build()
                    .parseSignedContent(token)
                    .getPayload();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUserEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
